//
//  Oracle.hpp
//  Chess
//
//  Created by Frederick Benjamin Woodruff on 03/03/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#ifndef Oracle_hpp
#define Oracle_hpp

#include <stdio.h>
#include <iostream>
#include <sstream>
#include <vector>
#include <atomic>
#include <string>
#include <shared_mutex>
#include <unordered_map>
#include <thread>
#include <cstddef>
#include <condition_variable>

namespace frd {
    /**
     
     Construct this functor object with:
     A stateful function with a 'quality' parameter that takes a long time to produce high 'quality' results.
     A function that knows the possible inputs to this other function for a given state.
     User requirements:
     When the state changes, update the state.

     */
    
    
    template <typename State, int maxChoices, typename Decision, typename Response> // implement threadjoiner
    struct Oracle {
    private:

        using ThisType = Oracle<State,maxChoices,Decision,Response>; // using thisT = decltype(this*);
        
        using fp_findMapping = Response (State::*)(int,Decision,std::atomic<bool>&) const;
        using fp_getInputs =  const std::vector<Decision>& (State::*)() const;
        fp_findMapping findMapping_;
        fp_getInputs getInputs_;
        
        
        mutable std::shared_mutex mut;
        
        
        const long maxDifficulty_;
        long max_counter;
        
        std::unordered_map<Decision,int> map_;
        std::atomic<unsigned> counter {0};
        std::atomic<bool> wait_fast {false};
        std::atomic<bool> wait_slow {false};
        std::atomic<bool> terminate_fast {false};
        size_t N_inputs;
        State* p_state_;
        
        
        const Response nullValue_;
        std::vector<Decision> inputs_;
        alignas(std::max_align_t) std::array<std::atomic<Response>,maxChoices> outputs_;
        std::condition_variable_any new_state;

        
        unsigned long const hardware_threads=std::thread::hardware_concurrency();
        
        long x = hardware_threads?hardware_threads:2;
        unsigned long const thread_count = (x < 4) ? 2 : x-2;
        //unsigned long const thread_count = 1;
        std::vector<std::thread> threads;
        
        void prepareResponse() {

            const auto index = counter.fetch_add(1,std::memory_order_relaxed);
            if(index>max_counter) { wait_fast.store(true,std::memory_order_release); return; }
            const auto trem = index%N_inputs;
            
            const auto currentResponse = outputs_[trem].load(std::memory_order_acquire);
            
            Response qmov = currentResponse;
            long idx=trem;
            
            if(currentResponse.getQuality() == Response::c_maxQ) {
                bool foundOne = false;
                for(long i = 0 ; i < N_inputs ; i++) {
                    idx = (trem + i) % N_inputs;
                    qmov = outputs_[idx].load();
                    if(qmov.getQuality() != Response::c_maxQ) {
                        foundOne = true;
                        break;
                    }
                }
                if(!foundOne) {
                    wait_fast.store(true,std::memory_order_release);
                    return;
                }
            }


            const auto response = (p_state_->*findMapping_)(qmov.getQuality()+1,inputs_.at(idx),wait_fast);
            
            bool succeed;
            
            do {
                if(wait_fast.load(std::memory_order_acquire)) return;
                auto oldResponse = outputs_[idx].load();
                if(oldResponse.getQuality() < response.getQuality())
                    succeed =std::atomic_compare_exchange_weak(&(outputs_[idx]), &oldResponse,response );
                else break;
            } while (!succeed);
        }
        
        
        void worker_thread() {
            while(!terminate_fast.load(std::memory_order_acquire)) {
                {
                    while(! wait_fast.load(std::memory_order_acquire) and
                          ! wait_slow.load(std::memory_order_acquire)) {
                        std::this_thread::yield();
                        std::shared_lock lk(mut);
                        prepareResponse();
                    }
                }
                
                {std::unique_lock lk(mut);
                    new_state.wait(lk, [this]{
                        return !wait_fast.load() and !wait_slow.load() ;
                    });
                }
            }
        }

        
    public:
        template<typename Callable>
        void lockedUpdate(const Callable& updater) {
            wait_fast.store(true,std::memory_order_release);
            {
                std::lock_guard lk(mut);
                for(int i = 0; i<N_inputs; i++) outputs_[i].store(nullValue_,std::memory_order_relaxed);
                map_.clear();
                
                updater();
                if(p_state_->terminal()) {
                    terminate_fast.store(true,std::memory_order_relaxed);
                } else {
                    inputs_ = (p_state_->*getInputs_)();
                    N_inputs = inputs_.size();
                    max_counter = N_inputs*maxDifficulty_;
                    for(int i = 0; i < N_inputs; i++)  map_.insert(std::pair(inputs_[i],i));
                    counter.store(0,std::memory_order_relaxed);
                }
                wait_slow.store(false,std::memory_order_relaxed);
                wait_fast.store(!N_inputs);
            }
            new_state.notify_all();
        }
        
        void hintSleep() { wait_slow.store(true,std::memory_order_release); }

        explicit Oracle(fp_findMapping findMapping,
                        fp_getInputs getInputs,
                        const Response nullValue,
                        const long maxDifficulty) :
            findMapping_(findMapping),
            getInputs_(getInputs),
            nullValue_(nullValue),
            maxDifficulty_(maxDifficulty) {}
        
        void initOracle(State* p_state) {
            
            for(int i = 0; i< outputs_.size(); i++) outputs_[i].store(nullValue_);
            map_.clear();
            p_state_ = p_state;

            inputs_ = (p_state_->*getInputs_)();
            N_inputs = inputs_.size();
            max_counter = N_inputs*maxDifficulty_;
            for(int i = 0; i < N_inputs; i++)  map_.insert(std::pair(inputs_[i],i));
            counter.store(0);
            wait_fast.store(!N_inputs);
            wait_slow.store(false);
            terminate_fast.store(false);
            
            for(unsigned i=0;i<thread_count;++i) {
                threads.push_back(std::thread(&ThisType::worker_thread,this));
            }
            for(unsigned i=0;i<thread_count;++i) {
                threads[i].detach();
            }
        }
        
        Response operator()(const Decision& key) const {
            std::shared_lock lk(mut);
            auto x = map_.at(key);
            if(auto out = outputs_[x].load(); out != nullValue_) {
                return out;
            } else {
                std::cerr << "bad move\n";
                auto tmp = std::atomic<bool>();
                return (p_state_->*findMapping_)(0,inputs_[x],tmp);
            }
            assert(false);
            return nullValue_;
        }
    };
}

#endif /* Oracle_hpp */

//
//  TreeSearch.hpp
//  Chess
//
//  Created by Frederick Benjamin Woodruff on 12/06/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#ifndef TreeSearch_hpp
#define TreeSearch_hpp

#include "NaiveChessPosition.hpp"
#include <array>
#include <stdio.h>

/*
Use an incremented mutable thread_local static int to periodically call
std::this_thread::yield()
*/

namespace  chs {

    template<e_moveType move_type>
    Score NaiveChessPosition::alphaBeta(const MoveList& old_list, int depth, int alpha, int beta,
                                std::atomic<bool>& state_did_change, bool maximising_player,
                                        int parent_score, e_boardSquare bsq) noexcept {
        [[maybe_unused]] auto copy = *this;
        if(state_did_change.load(std::memory_order_acquire)) { return {0,0}; }
        
        if(!pieces[e_colour::white][e_pieceType::king] or !pieces[e_colour::black][e_pieceType::king]) {
            return { heuristic<move_type>(parent_score),true };
        }
        if(depth == 0 ) return { heuristic<move_type>(parent_score),false};
        
        auto movelist =  old_list.nextVector();
        int value = maximising_player? 100'000 : -100'000;
        getMoveList<move_type>(movelist,depth-1,maximising_player,bsq);
        
        if(movelist.empty()) return {heuristic<move_type>(parent_score),true};
        
        int standardPat;
        if constexpr (move_type==e_moveType::materialChanging) {
            standardPat = heuristic<move_type>(parent_score);
            if( standardPat >= beta )
                return {beta,false};
            if( alpha < standardPat )
                alpha = standardPat;
        }
        
        bool complete = true; // only for all
        for(const auto& move : movelist ) {
            if(state_did_change.load(std::memory_order_acquire)) { return {0,0}; }
            Score score;
            auto _= ConsiderMove<move_type>(*this, move);
            if constexpr(move_type==e_moveType::all) {
                if(depth==1) {
                    if(!move.wasQuiet())
                        score = alphaBeta<e_moveType::materialChanging>(movelist,3, alpha, beta,state_did_change,!maximising_player,moveHeuristic());
                    else score = alphaBeta<e_moveType::all>(movelist,1, alpha, beta,state_did_change, !maximising_player);
                } else score = alphaBeta<move_type>(movelist,depth-1, alpha, beta,state_did_change, !maximising_player);
            } else {
                score = alphaBeta<move_type>(movelist,depth-1, alpha, beta,state_did_change,
                                                !maximising_player,parent_score,bsq);
            }
            
            if(!score.complete_) complete = false;
            if(!maximising_player) {
                value = std::max(value,score.value_);
                alpha = std::max(alpha, value);
            } else {
                value = std::min(value,score.value_);
                beta = std::min(beta,value);
            }
            if(alpha>=beta) break;
        }
        assert(*this==copy);
        if constexpr(move_type==e_moveType::all) {
            return {value,complete};
        } else {
            return {value,false};
        }
        
    }
    /*
    template<e_moveType move_type>
    Score NaiveChessPosition::alphaBeta(const MoveList& old_list, int depth, int alpha, int beta,
                                        std::atomic<bool>& state_did_change, bool maximising_player,
                                        int parent_score, e_boardSquare bsq) noexcept {
        auto copy = *this;
        if(state_did_change.load(std::memory_order_acquire)) { return {0,0}; }
        
        if(!pieces[e_colour::white][e_pieceType::king] or !pieces[e_colour::black][e_pieceType::king]) {
            return { heuristic<move_type>(parent_score),true };
        }
        if(depth == 0 ) {
            return { heuristic<move_type>(parent_score),false};
        }
        auto movelist =  old_list.nextVector();
        int value = maximising_player? 100'000 : -100'000;
        if(move_type==e_moveType::deferred) {
            getMoveList<e_moveType::all>(movelist,depth-1,maximising_player);
        } else {
            getMoveList<move_type>(movelist,depth-1,maximising_player,bsq);
        }
        if(movelist.empty()) return {heuristic<move_type>(parent_score),true};
        
        bool complete = true;
        for(const auto& move : movelist ) {
            if(state_did_change.load(std::memory_order_acquire)) { return {0,0}; }
            Score score;
            auto _= ConsiderMove<move_type>(*this, move);
            if constexpr(move_type==e_moveType::all) {
                if(depth==1) {
                    const auto unpacked_move = move.unpack();
                    if(unpacked_move.flags_!=normalMove and unpacked_move.flags_!=pawnDouble and unpacked_move.flags_!=OO and unpacked_move.flags_!=OOO) {
                        score = alphaBeta<e_moveType::materialChanging>(movelist,0, alpha, beta,state_did_change,!maximising_player,moveHeuristic());
                    } else {
                        score = alphaBeta<e_moveType::deferred>(movelist,1, alpha, beta,state_did_change, !maximising_player);
                    }
                } else score = alphaBeta<move_type>(movelist,depth-1, alpha, beta,state_did_change, !maximising_player);
            } else if constexpr (move_type==e_moveType::deferred) {
                const auto unpacked_move = move.unpack();
                if( unpacked_move.flags_!=normalMove and unpacked_move.flags_!=pawnDouble and
                   unpacked_move.flags_!=OO and unpacked_move.flags_!=OOO) {
                    score = alphaBeta<e_moveType::staticExchange>(movelist,0, alpha, beta,
                                                                  state_did_change, !maximising_player,moveHeuristic(),unpacked_move.end_);
                } else {
                    score = alphaBeta<e_moveType::quiet>(movelist,2, alpha, beta,state_did_change,!maximising_player,
                                                         pieceHeuristic(),unpacked_move.end_);
                }
            } else score = alphaBeta<move_type>(movelist,depth-1, alpha, beta,state_did_change, !maximising_player,parent_score,bsq);
            if(!score.complete_) complete = false;
            if(maximising_player) {
                value = std::max(value,score.value_);
                alpha = std::max(alpha, value);
            } else {
                value = std::min(value,score.value_);
                beta = std::min(beta,value);
            }
            if(alpha>=beta) break;
        }
        assert(*this==copy);
        if constexpr(move_type==e_moveType::all) {
            return {value,complete};
        } else {
            return {value,false};
        }
        
    }
    */
    
    
    /*
    template<e_moveType move_type>
    Score NaiveChessPosition::alphaBetaDebug(const MoveList& oldList, const int depth, int alpha, int beta,
                                             std::atomic<bool>& state_did_change, bool maximising_player,int parent_score, e_boardSquare bsq) noexcept {
        auto copy = *this; // debug only
        if(state_did_change.load(std::memory_order_acquire)) { return {0,0}; }
        if(!pieces[e_colour::white][e_pieceType::king] or !pieces[e_colour::black][e_pieceType::king]) {
            return { heuristic<e_moveType::all>(parent_score),true };
        }
        if(depth == 0 ) {
            return { heuristic<e_moveType::all>(parent_score),false};
        }
        auto movelist =  oldList.nextVector();
        int value = maximising_player? 100'000 : -100'000;
        getMoveList<e_moveType::all>(movelist,depth-1,maximising_player);
        if(movelist.empty()) return {heuristic<e_moveType::all>(parent_score),true};
        bool complete = true;
        for (const auto& move : movelist) {
            if(state_did_change.load(std::memory_order_acquire)) { return {0,0}; }
            auto _= ConsiderMove<e_moveType::all>(*this, move);
            auto score = alphaBetaDebug<e_moveType::all>(movelist,depth-1, alpha, beta,state_did_change,
                                                         !maximising_player,parent_score,bsq);
            if(!score.complete_) complete = false;
            if(!maximising_player) {
                value = std::max(value,score.value_);
                alpha = std::max(alpha, value);
            } else {
                value = std::min(value,score.value_);
                beta = std::min(beta,value);
            }
            if(alpha>=beta) break;
        }
        assert(*this==copy);
        return {value,complete};
    }*/
}
#endif /* TreeSearch_hpp */

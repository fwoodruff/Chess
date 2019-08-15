//
//  tree_search.hpp
//  ChessGameA
//
//  Created by Frederick Benjamin Woodruff on 12/06/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#ifndef tree_search_hpp
#define tree_search_hpp


#include "NaiveChessPosition.hpp"
#include <array>
#include <stdio.h>

namespace  chs {
    
    

    template<e_moveType move_type>
    Score NaiveChessPosition::alphaBeta(const MoveList& old_list, int depth, int alpha, int beta,
                                std::atomic<bool>& state_did_change, bool maximising_player,
                                        int parent_score, e_boardSquare bsq) noexcept {
        
        if(state_did_change.load(std::memory_order_acquire)) { return {0,0}; }
        if(depth == 0 ) {
            return { heuristic<move_type>(parent_score),false};
        }
        if(!pieces[e_colour::white][e_pieceType::king] or !pieces[e_colour::black][e_pieceType::king]) {
            return { heuristic<move_type>(parent_score),true };
        }
        
        
        // for quiessence search, heuristic should be piece occupancies only
        auto movelist =  old_list.nextVector();
        int value = maximising_player? -100'000 : 100'000;
        if(move_type==e_moveType::deferred) {
            getMoveList<e_moveType::all>(movelist,depth-1,maximising_player);
        } else {
            getMoveList<move_type>(movelist,depth-1,maximising_player,bsq);
        }
        if(!movelist.size()) return {heuristic<move_type>(parent_score),true};
        //for(int i=0; i<movelist.size(); i++) {
        
        bool complete = true;
        for(const auto& move : movelist ) {
            if(state_did_change.load(std::memory_order_acquire)) { return {0,0}; }
            Score score;
            auto _= ConsiderMove<move_type>(*this, move);
            if constexpr(move_type==e_moveType::all) {
                if(depth==1) {
                    const auto unpacked_move = move.unpack();
                    if(unpacked_move.flags_!=normalMove and unpacked_move.flags_!=pawnDouble and unpacked_move.flags_!=OO and unpacked_move.flags_!=OOO) {
                        score = alphaBeta<e_moveType::materialChanging>(movelist,5, alpha, beta,state_did_change,!maximising_player,moveHeuristic());
                    } else {
                        score = alphaBeta<e_moveType::deferred>(movelist,1, alpha, beta,state_did_change, !maximising_player);
                    }
                } else score = alphaBeta<move_type>(movelist,depth-1, alpha, beta,state_did_change, !maximising_player);
            } else if constexpr (move_type==e_moveType::deferred) {
                const auto unpacked_move = move.unpack();
                if( unpacked_move.flags_!=normalMove and unpacked_move.flags_!=pawnDouble and
                   unpacked_move.flags_!=OO and unpacked_move.flags_!=OOO) {
                    score = alphaBeta<e_moveType::staticExchange>(movelist,10, alpha, beta,
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
        return {value,complete};
    }
    
    
    
    
    
}
#endif /* tree_search_hpp */

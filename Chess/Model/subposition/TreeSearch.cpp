//
//  TreeSearch.cpp
//  Chess
//
//  Created by Frederick Benjamin Woodruff on 16/06/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#include <stdio.h>
#include "NaiveChessPosition.hpp"
#include <iostream>
#include <sstream>

namespace chs {
    int NaiveChessPosition::moveHeuristic() const {
        assert(pieces[black][king] or pieces[white][king]);
        int score = 0;
        for(int col=0;col<2;col++) {
            const int addsub[] = {1,-1};
            const auto diagsliders = Bitboard(pieces[col][bishop],pieces[col][queen]);
            const auto rowsliders = Bitboard(pieces[col][rook],pieces[col][queen]);
            score += addsub[col]*sliderMobility<  rook>(rowsliders ,occupancy[col],totalOccupancy);
            score += addsub[col]*sliderMobility<bishop>(diagsliders,occupancy[col],totalOccupancy);
            score += addsub[col]*jumperMobility<knight>(pieces[col][knight],occupancy[col],totalOccupancy);
            score += addsub[col]*jumperMobility<  king>(pieces[col][king  ],occupancy[col],totalOccupancy);
        }
        // pawns?
        return score;
    }

    int NaiveChessPosition::pieceHeuristic() const {
        assert(pieces[black][king] or pieces[white][king]);
        int score = 0;
        for(int col=0;col<2;col++) {
            const int addsub[] = {1,-1};
            score += addsub[col]*30*pieces[col][bishop].occupancy();
            score += addsub[col]*50*pieces[col][rook].occupancy();
            score += addsub[col]*90*pieces[col][queen].occupancy();
            score += addsub[col]*10*pieces[col][pawn].occupancy();
            score += addsub[col]*30*pieces[col][knight].occupancy();
        }
        return score;
    }

    Score NaiveChessPosition::alphaBetaDebug(const MoveList& oldList, const int depth, int alpha, int beta,
                                           std::atomic<bool>& state_did_change, bool maximising_player,int parent_score, e_boardSquare bsq) noexcept {
        auto copy = *this; // debug only
        if(state_did_change.load(std::memory_order_acquire)) { return {0,0}; }
        if(depth == 0 ) {
            return { heuristic<e_moveType::all>(parent_score),false};
        }
        if(!pieces[e_colour::white][e_pieceType::king] or !pieces[e_colour::black][e_pieceType::king]) {
            return { heuristic<e_moveType::all>(parent_score),true };
        }
        auto movelist =  oldList.nextVector();
        int value = maximising_player? -100'000 : 100'000;
        getMoveList<e_moveType::all>(movelist,depth-1,maximising_player);
        if(movelist.empty()) return {heuristic<e_moveType::all>(parent_score),true};
        bool complete = true;
        for (const auto& move : movelist) {
            if(state_did_change.load(std::memory_order_acquire)) { return {0,0}; }
            auto _= ConsiderMove<e_moveType::all>(*this, move);
            auto score = alphaBetaDebug(movelist,depth-1, alpha, beta,state_did_change, !maximising_player,parent_score,bsq);
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
        return {value,complete};
    }
}

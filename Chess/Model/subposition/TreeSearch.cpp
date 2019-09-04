//
//  TreeSearch.cpp
//  Chess
//
//  Created by Frederick Benjamin Woodruff on 16/06/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#include <stdio.h>
#include "NaiveChessPosition.hpp"
//#include "AVXfunctions.h"
#include <immintrin.h>
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
        
        
        static const int16_t weights[16] ={90,50,30,30,10,-90,-50,-30,-30,-10,0,0,0,0,0,0};
        int16_t occupancies[16];
        int score = 0;
        for (int i = 0; i < 2; i++) for(int j = 0; j < 5; j++)
            occupancies[5*i+j] = pieces[i][j+1].occupancy();
#ifdef __AVX2__
        __m256i occupancyVector = _mm256_load_si256( (const __m256i*) occupancies );
        __m256i   weightsVector = _mm256_load_si256( (const __m256i*) weights );
        __m256i outvector = _mm256_mullo_epi16(weightsVector,occupancyVector);
        int16_t *ptr = (int16_t*)&outvector;
        for (int i = 0; i < 10; i++) score += ptr[i];
#else
        for (int i = 0; i < 10; i++) score += occupancies[i] * weights[i];
#endif // AVX2
        return score;
    }

    Score NaiveChessPosition::alphaBetaDebug(const MoveList& oldList, const int depth, int alpha, int beta,
                                           std::atomic<bool>& state_did_change, bool maximising_player,int parent_score, e_boardSquare bsq) noexcept {
        auto copy = *this; // debug only
        if(state_did_change.load(std::memory_order_acquire)) { return {0,0}; }
        if(!pieces[e_colour::white][e_pieceType::king] or !pieces[e_colour::black][e_pieceType::king]) {
            return { heuristic<e_moveType::all>(parent_score),true };
        }
        auto movelist =  oldList.nextVector();
        if(depth == 0 ) {
            //int pieceScore = quiessence(movelist, alpha, beta, state_did_change);
            return  {heuristic<e_moveType::all>(parent_score),false};
        }
        int value = maximising_player? 100'000 : -100'000;
        if(depth < 2) getMoveList<e_moveType::materialChanging>(movelist,depth-1,maximising_player); // awful hack
        else getMoveList<e_moveType::all>(movelist,depth-1,maximising_player);
        getMoveList<e_moveType::all>(movelist,depth-1,maximising_player);
        if(movelist.empty()) return {heuristic<e_moveType::all>(parent_score),true};
        bool complete = true;
        for (const auto& move : movelist) {
            if(state_did_change.load(std::memory_order_acquire)) { return {0,0}; }
            auto before = this->drawBoard();
            // check if depth is 1 and then if move is quiet so standpat calculated
            // before move is executed.
            // also make a constexpr function that bitshifts an enum for fast move comparisons
            auto _= ConsiderMove<e_moveType::all>(*this, move);
            Score score;
            score = alphaBetaDebug(movelist,depth-1, alpha, beta,state_did_change,
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
    }
}

//
//  Movelist.cpp
//  Chess
//
//  Created by Frederick Benjamin Woodruff on 12/06/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#include "Movelist.hpp"

namespace chs {
    void NaiveChessPosition::swap(MoveList& list, ScoreList& scores, const int i, const int j) {
        auto outMove = list[i];
        list[i]=list[j];
        list[j]=outMove;
        auto outScore = scores[i];
        scores[i]=scores[j];
        scores[j]=outScore;
    }
    
    
    int NaiveChessPosition::partitionMoves(MoveList& list, ScoreList& scores, const int lo, const int hi, const bool highFirst) {
        auto pivot = scores[hi];
        auto i = lo;
        for(int j=lo; j<hi;j++) if((scores[j] < pivot) != highFirst) {
            swap(list,scores,i,j); i++;
        }
        swap(list,scores,i,hi);
        return i;
    }
    
    
    void NaiveChessPosition::sortMoves(MoveList& list, ScoreList& scores, const int lo, const int hi, const bool highFirst) {
        if(lo<hi) {
            auto p = partitionMoves(list, scores, lo, hi,highFirst);
            sortMoves(list, scores,lo,p-1,highFirst);
            sortMoves(list, scores,p+1,hi,highFirst);
        }
    }
}

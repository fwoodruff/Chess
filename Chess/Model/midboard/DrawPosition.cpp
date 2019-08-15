//
//  position_draw.cpp
//  Chess_Engine
//
//  Created by Frederick Benjamin Woodruff on 30/03/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#include "ChessGame.hpp"
#include "Bitboard.hpp"
#include <string>
#include <unordered_map>

namespace chs {

    std::string ChessPosition::drawBoard() const {
        std::string s = "";
        for(int i=0;i<c_maxRank;i++) {
            s+="\n";
            for(int j=0;j<c_maxFile;j++) {
                auto sq = Bitboard(e_boardSquare(c_maxFile*i+j));
                if(pieces[white][king] & sq) {
                    s+="K ";
                } else if(pieces[black][king] & sq) {
                    s+="k ";
                } else if(pieces[white][queen] & sq) {
                    s+="Q ";
                } else if(pieces[black][queen] & sq) {
                    s+="q ";
                } else if(pieces[white][rook] & sq) {
                    s+="R ";
                } else if(pieces[black][rook] & sq) {
                    s+="r ";
                } else if(pieces[white][bishop] & sq) {
                    s+="B ";
                } else if(pieces[black][bishop] & sq) {
                    s+="b ";
                } else if(pieces[white][pawn] & sq) {
                    s+="P ";
                } else if(pieces[black][pawn] & sq) {
                    s+="p ";
                } else if(pieces[white][knight] & sq) {
                    s+="N ";
                } else if(pieces[black][knight] & sq) {
                    s+="n ";
                } else {
                    s+=". ";
                }
            }
        }
        s+="\n";
        
        if(pos_flags.enPassant_ != 0) {
            for (int i=0; i<pos_flags.enPassant_-2; i++, s+="  ");
            s+= "^ \n";
        }
        if(pos_flags.blackKingside_) {
            s+="oo ";
        }
        if(pos_flags.blackQueenside_) {
            s+="ooo ";
        }
        if(pos_flags.whiteKingside_) {
            s+="OO ";
        }
        if(pos_flags.whiteQueenside_) {
            s+="OOO ";
        }
        s+="\n";
        
        if(gameState_==e_gameState::insufficientMaterial) {
            s+="insufficient_material";
        }
        if(gameState_==e_gameState::noMoveStaleMate) {
            s+="stale mate";
        }
        if(gameState_==e_gameState::whiteCheckmates) {
            s+= "White checkmate";
        }
        if(gameState_==e_gameState::blackCheckmates) {
            s+= "Black checkmate";
        }
        if(gameState_==e_gameState::whiteClaimedThreefoldRepetition) {
            s+="white claimed three fold repetition draw";
        }
        if(gameState_==e_gameState::blackClaimedThreefoldRepetition) {
            s+="black claimed three fold repetition draw";
        }
        
        if(gameState_==e_gameState::whiteClaimedFiftyQuietMoves) {
            s+="white claimed fifty quiet moves, draw";
        }
        if(gameState_==e_gameState::blackClaimedFiftyQuietMoves) {
            s+="black claimed fifty quiet moves, draw";
        }
        if(gameState_==e_gameState::whiteResigned) {
            s+="white resigned";
        }
        if(gameState_==e_gameState::blackResigned) {
            s+="black resigned";
        }
        
        /*
         if(history.find(*this) == history.end()) {
         s+= "unique";
         } else {
         s+= "seen: ";
         s+=history.at(*this);
         s+= " times";
         }
         */
        s+="\n";
        
        return s;
    }
}

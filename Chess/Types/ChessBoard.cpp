//
//  ChessBoard.cpp
//  Chess
//
//  Created by Frederick Benjamin Woodruff on 19/06/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#include "ChessBoard.hpp"

size_t std::hash<chs::ChessBoard>::operator() (const chs::ChessBoard& p) const {
    size_t seed = 7;
    
    chs::hash_combine(seed, p.pos_flags.enPassant_);
    chs::hash_combine(seed, p.pos_flags.blackKingside_);
    chs::hash_combine(seed, p.pos_flags.whiteQueenside_);
    chs::hash_combine(seed, p.pos_flags.blackQueenside_);
    chs::hash_combine(seed, p.pos_flags.blackQueenside_);
    chs::hash_combine(seed, p.turn==chs::e_colour::white);
    for (int i = 0 ; i < 5 ; i++) {
        chs::hash_combine(seed, p.pieces[chs::e_colour::white][i]);
        chs::hash_combine(seed, p.pieces[chs::e_colour::black][i]);
    }
    return seed;
}


namespace chs {
    std::string ChessBoard::drawBoard() const {
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

        
        return s;
    }
}

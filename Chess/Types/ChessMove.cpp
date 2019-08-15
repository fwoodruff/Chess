//
//  move.cpp
//  Chess_Engine
//
//  Created by Frederick Benjamin Woodruff on 30/03/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#include "ChessMove.hpp"
#include "PiecesAndSquares.hpp"
#include <tuple>

size_t std::hash<chs::Move>::operator() (const chs::Move& m) const {
    return std::hash<uint32_t >{}(m.repr_); // change this
}

namespace chs {
    std::string Move::draw() const {
        const auto unpacked_move = unpack();
        std::string s="";
        for (int rank=e_rank::rank8 ;rank<c_maxRank; rank++) {
            s+="\n";
            for (int file=e_file::fileA ;file<c_maxFile;file++) {
                if(unpacked_move.end_==(file+c_maxFile*rank)) s+= "E ";
                else if(unpacked_move.start_==(file+c_maxFile*rank)) s+= "S ";
                else s+= ". ";
            }
        }
        s+="\n";
        
        switch (unpacked_move.flags_) {
            case normalMove:
            case takingMove:
                break;
            case pawnDouble:
                s+="pawn double\n";
                break;
            case OOO:
                s+="Queen side castling\n";
                break;
            case OO:
                s+= "King side castling\n";
                break;
            case queenPromote:
            case queenPromoteTake:
                s+= "queen promote\n";
                break;
            case rookPromote:
            case rookPromoteTake:
                s+= "rook promote\n";
                break;
            case bishopPromote:
            case bishopPromoteTake:
                s+= "bishop promote\n";
                break;
            case knightPromote:
            case knightPromoteTake:
                s+= "knight promote\n";
                break;
            case enPassant:
                s+="en passant\n";
                break;
            case resignMove:
                s = ". . . . . . . .\n. . . . . . . .\nresign\n";
                break;
            case takeDraw:
                s = ". . . . . . . .\n. . . . . . . .\ntake draw\n";
                break;
        }
        return s;
    }

    

}

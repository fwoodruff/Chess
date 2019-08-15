//
//  PerformMove.hpp
//  Chess
//
//  Created by Frederick Benjamin Woodruff on 30/03/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#ifndef PerformMove_hpp
#define PerformMove_hpp

#include <stdio.h>
#include "Bitboard.hpp"
#include "PiecesAndSquares.hpp"
#include "ChessMove.hpp"
#include "NaiveChessPosition.hpp"
#include <iostream>

namespace  chs {

    template<e_moveType moveType>
    NaiveChessPosition::ExecuteMove<moveType>::ExecuteMove(NaiveChessPosition& p,const Move& m) : position_(p) {
        
        const auto unpackedMove = m.unpack();
        Bitboard moveBoard = Bitboard(unpackedMove.start_, unpackedMove.end_);
        
        if constexpr (moveType == e_moveType::quiet) oldPosition_ = { moveBoard, unpackedMove.piece_};
        else oldPosition_ = p;
        
        
        position_.pieces[position_.turn][unpackedMove.piece_] ^= moveBoard;
        
        auto& our_occupancy = position_.occupancy[position_.turn];
        
        if constexpr (moveType == e_moveType::quiet) {
            our_occupancy ^=moveBoard;
        } else {
            auto& theirPieces = position_.pieces[~position_.turn];
            auto& theirOccupancy = position_.occupancy[~position_.turn];
            
            for (int i=0;i<6;i++)
                theirPieces[i] &= ~moveBoard;
            
            position_.pos_flags.enPassant_ = 0;
            switch (unpackedMove.flags_) {
                case normalMove:
                    break;
                case pawnDouble:
                    if(position_.turn==white) position_.pos_flags.enPassant_ = (unpackedMove.start_+2)-a2;
                    else position_.pos_flags.enPassant_ = (unpackedMove.start_+2)-a7;
                    break;
                case OO:
                    if(position_.turn==white) {
                        position_.pieces[white][rook] ^= Bitboard(f1,h1);
                        position_.pos_flags.whiteKingside_=false;
                        position_.pos_flags.whiteQueenside_=false;
                    } else {
                        position_.pieces[black][rook] ^= Bitboard(f8,h8);
                        position_.pos_flags.blackKingside_=false;
                        position_.pos_flags.blackQueenside_=false;
                    }
                    break;
                case OOO:
                    if(position_.turn==white) {
                        position_.pieces[white][rook] ^= Bitboard(a1,d1);
                        position_.pos_flags.whiteKingside_=false;
                        position_.pos_flags.whiteQueenside_=false;
                    } else {
                        position_.pieces[black][rook] ^= Bitboard(a8,d8);
                        position_.pos_flags.blackKingside_=false;
                        position_.pos_flags.blackQueenside_=false;
                    }
                    break;
                case queenPromote:
                case queenPromoteTake:
                {
                    auto takeBoard = Bitboard(unpackedMove.end_);
                    if(position_.turn==white) {
                        position_.pieces[white][pawn] ^= takeBoard;
                        position_.pieces[white][queen] ^= takeBoard;
                    } else {
                        position_.pieces[black][pawn] ^= takeBoard;
                        position_.pieces[black][queen] ^= takeBoard;
                    }
                    break;
                }
                case rookPromote:
                case rookPromoteTake:
                {
                    auto takeBoard = Bitboard(unpackedMove.end_);
                    if(position_.turn==white) {
                        position_.pieces[white][pawn] ^= takeBoard;
                        position_.pieces[white][rook] ^= takeBoard;
                    } else {
                        position_.pieces[black][pawn] ^= takeBoard;
                        position_.pieces[black][rook] ^= takeBoard;
                    }
                }
                    break;
                case bishopPromote:
                case bishopPromoteTake:
                {
                    auto takeBoard = Bitboard(unpackedMove.end_);
                    if(position_.turn==white) {
                        position_.pieces[white][pawn] ^= takeBoard;
                        position_.pieces[white][bishop] ^= takeBoard;
                    } else {
                        position_.pieces[black][pawn] ^= takeBoard;
                        position_.pieces[black][bishop] ^= takeBoard;
                    }
                    break;
                }
                case knightPromote:
                case knightPromoteTake:
                {
                    auto take_board = Bitboard(unpackedMove.end_);
                    if(position_.turn==white) {
                        position_.pieces[white][pawn]  ^= take_board;
                        position_.pieces[white][knight] ^= take_board;
                    } else {
                        position_.pieces[black][pawn]  ^= take_board;
                        position_.pieces[black][knight] ^= take_board;
                    }
                    break;
                }
                case enPassant:
                    if(position_.turn==white) {
                        auto take_board = Bitboard(unpackedMove.end_+8);
                        position_.pieces[black][pawn] ^= take_board;
                        break;
                    } else {
                        auto take_board = Bitboard(unpackedMove.end_-8);
                        position_.pieces[white][pawn] ^= take_board;
                        break;
                    }
                default:
                    break;
            }
            
            switch(unpackedMove.flags_) {
                case normalMove:
                case pawnDouble:
                    our_occupancy ^=moveBoard;
                    theirOccupancy&=~moveBoard;
                    break;
                default:
                    for(int i = 0 ; i < 2; i++)
                        position_.occupancy[i] = {position_.pieces[i]};
                    position_.totalOccupancy = {position_.occupancy};
                    break;
            }
            if(unpackedMove.piece_ == king or unpackedMove.piece_ == rook) {
                if(position_.turn==white) {
                    if(!(Bitboard(h1) & position_.pieces[white][rook]))
                        position_.pos_flags.whiteKingside_=false;
                    if(!(Bitboard(a1) & position_.pieces[white][rook]))
                        position_.pos_flags.whiteQueenside_=false;
                    if(!(Bitboard(e1) & position_.pieces[white][king])) {
                        position_.pos_flags.whiteKingside_=false;
                        position_.pos_flags.whiteQueenside_=false;
                    }
                } else {
                    if(!(Bitboard(h8) & position_.pieces[black][rook]))
                        position_.pos_flags.blackKingside_=false;
                    if(!(Bitboard(a8) & position_.pieces[black][rook]))
                        position_.pos_flags.blackQueenside_=false;
                    if(!(Bitboard(e8) & position_.pieces[black][king])) {
                        position_.pos_flags.blackKingside_=false;
                        position_.pos_flags.blackQueenside_=false;
                    }
                }
            }
        }
        
        position_.totalOccupancy = position_.occupancy;
        position_.turn=~position_.turn;
    }
}

#endif /* PerformMove_hpp */

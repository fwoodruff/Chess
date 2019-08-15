//
//  LabelledPieces.cpp
//  Chess
//
//  Created by Frederick Benjamin Woodruff on 20/06/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#include "LabelledPieces.hpp"
#include "PiecesAndSquares.hpp"
#include "ChessPosition.hpp"
#include "Bitboard.hpp"
#include <iostream>

namespace chs {
    void LabelledPieces::performMove(Move move) {
        
        moving_={};
        assert(move!=c_terminalMove);
        assert(move!=c_nullMove);
        
        if(move == c_resignMove or move == c_takeDrawMove) {
            return;
        }
        
        const unpackedMove upm = move.unpack();
        switch (upm.flags_) {
            case resignMove:
            case takeDraw:
                assert(false);
            default:
                break;
        }
        
        auto& movingID = pieceMap_[upm.start_]; // check this
        auto& landingID = pieceMap_[upm.end_];
        auto& moving_piece = IDpieces_[movingID];
        moving_.push_back(movingID);
        
        moving_piece.square_= upm.end_;
        switch (upm.flags_) {
            case e_flagType::queenPromoteTake:
            case e_flagType::rookPromoteTake:
            case e_flagType::bishopPromoteTake:
            case e_flagType::knightPromoteTake:
            case e_flagType::takingMove:
            {
                moving_.push_back(landingID);
                auto& takenPiece = IDpieces_[landingID];
                takenPiece.isPlaced_=false;
                takenPiece.positionInTakens_ = numberTaken[takenPiece.colour_]++;
            }
            default:
                break;
        }
        
        switch (upm.flags_) {
            case e_flagType::queenPromote :
            case e_flagType::queenPromoteTake:
                
                moving_piece.type_ = e_pieceType::queen;
                break;
            case e_flagType::rookPromote :
            case e_flagType::rookPromoteTake:
                moving_piece.type_ = e_pieceType::rook;
                break;
            case e_flagType::bishopPromote :
            case e_flagType::bishopPromoteTake:
                moving_piece.type_ = e_pieceType::bishop;
                break;
            case e_flagType::knightPromote :
            case e_flagType::knightPromoteTake:
                moving_piece.type_ = e_pieceType::knight;
                break;
            case e_flagType::enPassant:
            {
                const auto& passedSquare = upm.end_ + c_maxFile*(((moving_piece.colour_==e_colour::white) *2)-1);
                auto& takenPiece = IDpieces_[pieceMap_[passedSquare]];
                moving_.push_back(pieceMap_[passedSquare]);
                takenPiece.isPlaced_=false;
                takenPiece.positionInTakens_= numberTaken[takenPiece.colour_]++;
                pieceMap_[passedSquare] = c_nulldif;
                break;
            }
            case OO:
            {
                auto& rookID = pieceMap_[upm.end_+1];
                moving_.push_back(rookID);
                auto& rookPiece = IDpieces_[rookID];
                rookPiece.square_= upm.end_-1;
                pieceMap_[upm.end_-1] = rookID;
                rookID = c_nulldif;
                break;
            }
            case OOO:
            {
                auto& rookID = pieceMap_[upm.end_-2];
                moving_.push_back(rookID);
                auto& rookPiece = IDpieces_[rookID];
                rookPiece.square_= upm.end_+1;
                pieceMap_[upm.end_+1] = rookID;
                rookID = c_nulldif;
                break;
            }
            default:
                break;
        }
        landingID = movingID;
        movingID = c_nulldif;
    }
}

//
//  ChessGame.cpp
//  Chess
//
//  Created by Frederick Benjamin Woodruff on 30/03/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#include "ChessGame.hpp"
#include "PiecesAndSquares.hpp"
#include "Bitboard.hpp"
#include "Masks.hpp"
#include "MagicSlider.hpp"
#include "ChessMove.hpp"
#include "PerformMove.hpp"
#include "LabelledPieces.hpp"
#include <vector>
#include <array>
#include <tuple>
#include <iostream>

namespace chs {
    ChessGame::ChessGame() noexcept {
        historyPositions_.emplace(); // make a bitboard position
        historySet_.insert({getPosition().slice(),1}); // add it to the set
        historyMovelist_.push(getPosition().makeMoveList(historySet_)); // find a movelist
        historyPiecelist_.push(LabelledPieces::from_board(getPosition().slice()));
    }

    
    void ChessGame::performMove(Move move) {
        if(move == c_nullMove) assert(false);
        if(move == c_terminalMove) return;

        historyPositions_.push(getPosition());
        getPosition() = getPosition().performMove(move,historySet_);
        historyPiecelist_.push(getPieceList());
        getPieceList().performMove(move);
        
        if(historySet_.find(getPosition().slice()) == historySet_.end()) historySet_.insert({getPosition().slice(),1});
        else historySet_[getPosition().slice()]++; // make a 'hashmapstack' data structure

        const auto moveList = getPosition().makeMoveList(historySet_);
        
        historyMovelist_.push(moveList); // move
        canUndo_ = !(historySet_.size()==1 or getPosition().gameState_!=e_gameState::ongoing);
        canDraw_ = false;
        if(getMoveList().size()>=2)
            canDraw_ = (getMoveList()[(getMoveList().size() - 2)]==c_takeDrawMove);
        canResign_ = historySet_.size()!=1 and getPosition().gameState_==e_gameState::ongoing;
    }
    
    void ChessGame::undoMove() noexcept {
        if(historySet_[getPosition().slice()] == 1) historySet_.erase(getPosition().slice()); // memory leak?
        else historySet_[getPosition().slice()]--;
        historyPositions_.pop();
        historyPiecelist_.pop();
        historyMovelist_.pop();
        canUndo_ = !(historySet_.size()==1 or getPosition().gameState_!=e_gameState::ongoing);
        canDraw_ = false;
        if(getMoveList().size()>=2)
            canDraw_ = (getMoveList()[(getMoveList().size() - 2)]==c_takeDrawMove);
        canResign_ = (historySet_.size()!=1) and getPosition().gameState_==e_gameState::ongoing;
    }
    
    std::vector<placed_piece> get_pieces() {
        return std::vector<placed_piece>();
    }

}

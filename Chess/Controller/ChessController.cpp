//
//  ChessController.cpp
//  Chess
//
//  Created by Frederick Benjamin Woodruff on 13/04/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#include "ChessController.hpp"
#include <stdio.h>
#include <iostream>
namespace chs {
    std::string Controller::board_state() const {
        switch(game_.getPosition().gameState_) {
            case chs::e_gameState::ongoing:
                return "";
            case chs::e_gameState::insufficientMaterial:
                return "Insufficient material. Stalemate.";
            case chs::e_gameState::noMoveStaleMate:
                return "Stalemate.";
            case chs::e_gameState::whiteClaimedThreefoldRepetition:
                return "White observed threefold repetition rule. Stalemate.";
            case chs::e_gameState::whiteClaimedFiftyQuietMoves:
                return "White observed fifty-move rule. Stalemate.";
            case chs::e_gameState::blackClaimedThreefoldRepetition:
                return "Black observed threefold repetition rule. Stalemate.";
            case chs::e_gameState::blackClaimedFiftyQuietMoves:
                return "Black observed fifty-move rule. Stalemate.";
            case e_gameState::draw75quietMoves:
                return "Arbiter observed 75 move rule. Stalemate.";
            case e_gameState::fivefoldRepetition:
                return "Arbiter observed fivefold repetition rule. Stalemate.";
            case chs::e_gameState::whiteResigned:
                return "White resigned. Black wins.";
            case chs::e_gameState::blackResigned:
                return "Black resigned. White wins.";
            case chs::e_gameState::whiteCheckmates:
                return "Checkmate. White wins.";
            case chs::e_gameState::blackCheckmates:
                return "Checkmate. Black wins.";
        }
    }
    
    e_legality Controller::pieceMoveWasLegal(bool outOfBounds) const noexcept { // improve complexity to O(1)
        if(outOfBounds) return e_legality::illegal;
        for (int i=0; i<game_.getMoveList().size(); i++) {
            auto mov = game_.getMoveList()[i];
            if(mov.startSquare()==startSquare_) {
                auto upm = mov.unpack();
                if(upm.end_==endSquare_ and upm.start_!=upm.end_) {
                    if(upm.flags_>4 and upm.flags_<13) { // implementation specific
                        return e_legality::promotion;
                    }
                    return e_legality::legal;
                }
            }
        }
        return e_legality::illegal;
    }

    void Controller::update() {
        for(const auto& mov : game_.getMoveList()) {
            if(mov.startSquare()==startSquare_) {
                auto upm = mov.unpack();
                if(upm.end_==endSquare_) {
                    if(promotionType_==e_flagType::normalMove or promotionType_==upm.flags_) {
                        auto response_move = fastMovePicker_(mov);
                        fastMovePicker_.lockedUpdate([&]{
                            game_.performMove(mov);
                            movers_ = game_.getPieceList().moving_;
                            game_.performMove(response_move.get_move());
                            thinkClock=0;
                            auto vec = game_.getPieceList().moving_;
                            movers_.insert( movers_.end(), vec.begin(), vec.end() );
                        });
                        break;
                    }
                }
            }
        }
        uiState_ = game_.getPosition().gameState_ == e_gameState::ongoing?
                                        e_UIstates::clearBoard : uiState_ = e_UIstates::gameOver;
    }
    
    
    void Controller::undo() {
        fastMovePicker_.lockedUpdate([this]{
            movers_ = game_.getPieceList().moving_;
            game_.undoMove();
            auto vec = game_.getPieceList().moving_;
            movers_.insert( movers_.end(), vec.begin(), vec.end() );
            game_.undoMove();
            thinkClock=0;
        });
        uiState_ = e_UIstates::clearBoard;
    }
    
    Controller::Controller() : fastMovePicker_(&ChessGame::pick_response_debug,
                                               &ChessGame::getMoveList,
                                               c_nullQMove,
                                               100)  {}
}

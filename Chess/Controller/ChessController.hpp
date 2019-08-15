//
//  ChessController.hpp
//  Chess
//
//  Created by Frederick Benjamin Woodruff on 13/04/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#ifndef ChessController_hpp
#define ChessController_hpp

#include "ChessMove.hpp"
#include "Bitboard.hpp"
#include "ChessGame.hpp"
#include "PiecesAndSquares.hpp"
#include "Oracle.hpp"
#include <stack>
#include <array>
#include <vector>

namespace chs {

    enum struct e_legality {legal, illegal, promotion};
    
    

    enum struct e_UIstates {
        dormantAreaPressedWhileClear,
        dormantAreaPressedWhilePromotion,
        dormantAreaPressedWhileTakingPromotion,
        pieceGrabbedPressed,
        undoPressed,
        drawPressed,
        resignPressed,
        promotionPiecePressed,
        takingPromotionPiecePressed,
        clearBoard,
        promotionState,
        takingPromotionState,
        gameOver
    };
    


    struct Controller {
    private:
        static constexpr const int c_level = 8;
        ChessGame game_;
        frd::Oracle<ChessGame,c_maxMoves,Move,QMove> fastMovePicker_;
        int thinkClock;
        bool promotionState_;
        e_boardSquare startSquare_;
        e_boardSquare endSquare_;
        e_flagType promotionType_;
        std::vector<uint8_t> movers_;
    public:
        e_UIstates uiState_;
        static const uint8_t nullPiece = LabelledPieces::c_nulldif;
        
        inline void set_start(e_boardSquare start) noexcept {startSquare_=start;}
        
        inline uint8_t get_piece_idx(e_boardSquare bs) const noexcept {return game_.getPieceList().squareArrayID(bs);}
        inline void set_end(e_boardSquare end) noexcept {endSquare_=end;promotionType_=e_flagType::normalMove;}
        e_legality pieceMoveWasLegal(bool outOfBounds) const noexcept; // checks if move is legal
        inline void set_promotee(e_flagType pt) noexcept {promotionType_=pt;}
        void update();
        void undo();
        inline void takeDraw() {game_.performMove(c_takeDrawMove); uiState_ = e_UIstates::gameOver;}
        
        inline void eachSecond() {if(thinkClock++>c_level)fastMovePicker_.hintSleep();}
        
        inline void resign() { game_.performMove(c_resignMove); uiState_ = e_UIstates::gameOver;}
        
        inline bool canUndo() { return game_.canUndo_;}
        inline bool canResign() {return game_.canResign_;}
        inline bool canDraw() {return game_.canDraw_;}
        std::string board_state() const;
        std::vector<uint8_t> movers() const {return movers_;}
        
        
        inline const int getNextTakenPosition() const {
            return game_.getPieceList().nextTakenPosition(~game_.getPosition().getTurn());}
        
        inline const PossiblyPlacedPiece pieceData(uint8_t i) const { return game_.getPieceList()[i];}
        inline const PossiblyPlacedPiece pieceData() const {
            return game_.getPieceList()[game_.getPieceList().squareArrayID(startSquare_)];
        }
        
        Controller();
        inline void initOracle() {fastMovePicker_.initOracle(&game_);}
    };
}

#endif /*  ChessController_hpp */

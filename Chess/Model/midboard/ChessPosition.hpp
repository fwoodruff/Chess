//
//  midboard.hpp
//  ChessGameA
//
//  Created by Frederick Benjamin Woodruff on 15/06/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#ifndef midboard_hpp
#define midboard_hpp

#include "Bitboard.hpp"
#include "PiecesAndSquares.hpp"
#include "ChessMove.hpp"
#include "NaiveChessPosition.hpp"
#include "Movelist.hpp"
#include <stdio.h>
#include <functional>
#include <string>
#include <unordered_map>
#include <memory>

namespace chs {
    enum struct e_gameState {
        ongoing, insufficientMaterial, noMoveStaleMate,
        whiteClaimedThreefoldRepetition, whiteClaimedFiftyQuietMoves,
        blackClaimedThreefoldRepetition, blackClaimedFiftyQuietMoves,
        draw75quietMoves, fivefoldRepetition,
        whiteResigned, blackResigned,
        whiteCheckmates,blackCheckmates
    };
    
    struct Move;
    
    struct ChessPosition final : private NaiveChessPosition {
    public:
        
    private:
        
        int ruleOfFifty_; // need rule of 75
        bool hasInsufficientMaterial() const noexcept;
        //bool hasNoLegalMoves() noexcept;
        //int pieceHeuristic() const;
        
        [[nodiscard]] QMove pickMove(int depth, const ChessBoard::map& history, std::atomic<bool>& stateDidChange);
        [[nodiscard]] QMove pickMoveDebug(int depth, const ChessBoard::map& history, std::atomic<bool>& stateDidChange);
        
    public:
        
        
        [[nodiscard]] QMove pick_response(const int depth, Move move ,
                                         const ChessBoard::map& history, std::atomic<bool>& stateDidChange) const;
        [[nodiscard]] QMove pick_response_Debug(const int depth, Move move ,
                                         const ChessBoard::map& history, std::atomic<bool>& stateDidChange) const;
        
        [[nodiscard]] ChessPosition performMove(Move,const ChessBoard::map&) const noexcept;
        
        e_gameState gameState_;
        //using NaiveChessPosition::getMoveList;
        [[nodiscard]] std::vector<Move> makeMoveList(const map&) const; // const?
        //[[nodiscard]] static ChessPosition makePosition() noexcept;
        
        ChessPosition() noexcept;
        
        std::string drawBoard() const;
        [[nodiscard]] inline e_colour getTurn() const noexcept {return turn;};
        [[nodiscard]] inline ChessBoard slice() const noexcept { return static_cast<ChessBoard>(*this); }
        //static e_pieceType missing(const ChessPosition& this_position, const ChessPosition& other_position) const noexcept;
        // remove this
    };

}


#endif /* midboard_hpp */

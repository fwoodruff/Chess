//
//  ChessGame.hpp
//  Chess
//
//  Created by Frederick Benjamin Woodruff on 30/03/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

// TODO
// Make a map to the pieces in the UI and then use pointers to drag pieces around.
// For the moment just refresh board after move
//

#ifndef position_impl_hpp
#define position_impl_hpp

#include "Bitboard.hpp"
#include "PiecesAndSquares.hpp"
#include "ChessMove.hpp"
#include "NaiveChessPosition.hpp"
#include <stdio.h>
#include <functional>
#include <string>
#include <unordered_map>
#include <memory>
#include <vector>
#include <stack>
#include "ChessPosition.hpp"
#include "LabelledPieces.hpp"

namespace chs {
    struct Move;
    struct ChessGame final {
    // full chess game logic, UI and processor agnostic
    private:
        ChessBoard::map historySet_; // keep on heap for threading?
        std::stack<ChessPosition>  historyPositions_;
        std::stack<LabelledPieces> historyPiecelist_; 
        std::stack<std::vector<Move>> historyMovelist_;
    public:
        bool canUndo_;
        bool canDraw_;
        bool canResign_;

        ChessGame() noexcept; // constexpr
        void undoMove() noexcept;
        void performMove(Move move);
        
        [[nodiscard]] inline bool terminal() { return getPosition().gameState_!=chs::e_gameState::ongoing; }
        
        inline void performMoves(Move move, Move response) { performMove(move); performMove(response);}

        inline const ChessPosition& getPosition() const { return historyPositions_.top(); }
        
        inline ChessPosition& getPosition() {
            return const_cast<ChessPosition&>(std::as_const(*this).getPosition());
        }
        inline const std::vector<Move>& getMoveList() const { return historyMovelist_.top(); }
        
        inline std::vector<Move>& getMoveList() {
            return const_cast<std::vector<Move>&>(std::as_const(*this).getMoveList());
        }
        inline const LabelledPieces& getPieceList() const {return historyPiecelist_.top();}
        inline LabelledPieces& getPieceList() {
            return const_cast<LabelledPieces&>(std::as_const(*this).getPieceList());
        }
        [[nodiscard]] inline QMove pick_response(int depth, Move move ,std::atomic<bool>& state_did_change) const {
            return getPosition().pick_response(depth, move, historySet_, state_did_change);
        }
        [[nodiscard]] inline QMove pick_response_debug(int depth, Move move ,std::atomic<bool>& state_did_change) const {
            return getPosition().pick_response_Debug(depth, move, historySet_, state_did_change);
        }
    };
}

#endif /* position_impl_hpp */

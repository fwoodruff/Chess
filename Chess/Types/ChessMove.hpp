//
//  move.hpp
//  Chess_Engine
//
//  Created by Frederick Benjamin Woodruff on 30/03/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#ifndef move_hpp
#define move_hpp

#include "PiecesAndSquares.hpp"
#include <stdio.h>
#include <string>
#include <sstream>
#include <iostream>

namespace chs {struct Move;}

template<>
struct std::hash<chs::Move> {
public:
    size_t operator()(const chs::Move&) const;
};

namespace chs {
    enum e_flagType : unsigned {
        normalMove, takingMove, pawnDouble,
        OO, OOO,
        queenPromote, rookPromote, bishopPromote, knightPromote,
        queenPromoteTake, rookPromoteTake, bishopPromoteTake, knightPromoteTake,
        enPassant,
        resignMove, takeDraw
    };
    enum struct e_moveType : unsigned { quiet, materialChanging, staticExchange, all, deferred};
    struct unpackedMove {
    public:
        e_boardSquare start_;
        e_boardSquare end_;
        e_pieceType piece_;
        e_flagType flags_;
    };
    struct QMove;
    struct Move {
    private:
        uint32_t repr_;
        constexpr const static unsigned c_repSize = 19;
    public:
        constexpr Move(e_boardSquare start, e_boardSquare end, e_pieceType thisPiece,
                       e_flagType = e_flagType::normalMove);
        [[nodiscard]] constexpr e_boardSquare startSquare() const;
        [[nodiscard]] constexpr const unpackedMove unpack() const;
        std::string draw() const;
        [[nodiscard]] constexpr inline bool operator==(const Move& other) const noexcept { return repr_==other.repr_; }
        [[nodiscard]] constexpr inline bool operator!=(const Move& other) const noexcept { return repr_!=other.repr_; }
        friend size_t std::hash<Move>::operator() (const Move&) const;
        constexpr Move():repr_(0) {};
        friend QMove;
    };

    struct QMove {
    private:
        Move move_;
    public:
        constexpr const static uint32_t c_maxQ = (uint32_t(-1)) >> Move::c_repSize;
        constexpr QMove(Move move, uint32_t quality);
        [[nodiscard]] constexpr inline bool operator==(const QMove& other) const noexcept { return move_==other.move_; }
        [[nodiscard]] constexpr inline bool operator!=(const QMove& other) const noexcept { return move_!=other.move_; }
        [[nodiscard]] constexpr inline Move get_move() const;
        [[nodiscard]] constexpr inline unsigned getQuality() const;
    };
    
    constexpr Move::Move(e_boardSquare start, e_boardSquare end, e_pieceType thisPiece, e_flagType flagType):
    repr_(start |  end << 6  |  thisPiece << 12 | flagType << 15) {}
    constexpr QMove::QMove(Move move, uint32_t quality) {
        move_.repr_ = move.repr_ |  (quality << Move::c_repSize) ;
    };
    constexpr inline unsigned QMove::getQuality() const {
        return unsigned(move_.repr_ >> 19);
    }
    
    constexpr const unpackedMove Move::unpack() const {
        return {e_boardSquare(repr_ & 0b111111),
            e_boardSquare(repr_ >> 6 & 0b111111),
            e_pieceType(repr_ >> 12 & 0b111),
            e_flagType(repr_ >> 15 & 0b1111)};
    }
    constexpr inline Move QMove::get_move() const {
        auto copy = *this;
        copy.move_.repr_ &= (~(c_maxQ<<Move::c_repSize));
        return copy.move_;
    }
    constexpr e_boardSquare Move::startSquare() const {
        return e_boardSquare(repr_ & 0b111111);
    }
    
    template<e_colour colour> constexpr auto queensideCastle = 0;
    template<e_colour colour> constexpr auto kingsideCastle = 0;
    template<> constexpr auto queensideCastle<white> = Move(e1, c1,king, e_flagType::OOO);
    template<> constexpr auto kingsideCastle<white> = Move(e1, g1,king, e_flagType::OO );
    template<> constexpr auto queensideCastle<black> = Move(e8, c8,king, e_flagType::OOO);
    template<> constexpr auto kingsideCastle<black> = Move(e8, g8,king, e_flagType::OO );
    constexpr const static auto c_resignMove = Move(a8,a8,king,e_flagType::resignMove); // bring these inside move class
    constexpr const static auto c_takeDrawMove = Move(a8,a8,king,e_flagType::takeDraw);
    constexpr const static auto c_nullMove = Move(a8,a8,king, e_flagType::normalMove);
    constexpr const static auto c_terminalMove = Move(a8,a8,king, e_flagType::takingMove);
    constexpr const static auto c_terminalQMove = QMove(c_terminalMove,QMove::c_maxQ);
    constexpr const static auto c_nullQMove = QMove(c_nullMove,0);
}

#endif /* move_hpp */

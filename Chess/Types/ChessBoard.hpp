//
//  ChessBoard.hpp
//  Chess
//
//  Created by Frederick Benjamin Woodruff on 19/06/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#ifndef ChessBoard_hpp
#define ChessBoard_hpp

#include "PiecesAndSquares.hpp"
#include "Bitboard.hpp"
#include <array>
#include <functional>
#include <unordered_map>

namespace chs {class ChessBoard;}
template<>
struct std::hash<chs::ChessBoard> {
public:
    size_t operator()(const chs::ChessBoard&) const;
};
namespace chs {
    struct LabelledPieces;
    struct ChessBoard {
    protected:
        std::array<std::array<Bitboard,6>,2> pieces;
        PositionFlags pos_flags;
        e_colour turn;
    public:
        using map = std::unordered_map<ChessBoard, int>;
        friend size_t std::hash<ChessBoard>::operator() (const ChessBoard&) const;
        constexpr bool operator==(const ChessBoard& other) const noexcept;
        friend LabelledPieces;
        std::string drawBoard() const;
    };
    
    constexpr bool chs::ChessBoard::operator==(const ChessBoard& other) const noexcept {
        for(int i=0;i<6;i++) {
            if(pieces[white][i]!=other.pieces[white][i] or
               pieces[black][i]!=other.pieces[black][i]) return false;
        }
        if (pos_flags.enPassant_ != other.pos_flags.enPassant_
            or pos_flags.blackKingside_ != other.pos_flags.blackKingside_
            or pos_flags.blackQueenside_ != other.pos_flags.blackQueenside_
            or pos_flags.whiteKingside_ != other.pos_flags.whiteKingside_
            or pos_flags.whiteQueenside_ != other.pos_flags.whiteQueenside_
            or turn != other.turn)
            return false;
        return true;
    }
}

#endif /* ChessBoard_hpp */

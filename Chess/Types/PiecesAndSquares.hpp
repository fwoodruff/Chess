

#ifndef types_hpp
#define types_hpp

#include <stdio.h>
#include <array>
#include <string>
#include <cstdint>

namespace chs {

    constexpr int c_maxMoves = 218;
    constexpr int c_maxSquare = 64;
    constexpr int c_maxRank = 8;
    constexpr int c_maxFile = 8;
    
    enum e_colour {white,black};
    [[nodiscard]] inline e_colour operator~(const e_colour& colour) { return e_colour(unsigned(colour)^1); }

    struct PositionFlags {
        unsigned enPassant_ : 4; // 0 = none or 2-9 -> 0-7
        bool whiteKingside_ : 1;
        bool blackKingside_ : 1;
        bool whiteQueenside_ : 1;
        bool blackQueenside_ : 1;
    };


    enum e_pieceType : unsigned char {king, queen, rook, bishop, knight, pawn};



    enum e_boardSquare : unsigned char {
        a8, b8, c8, d8, e8, f8, g8, h8,
        a7, b7, c7, d7, e7, f7, g7, h7,
        a6, b6, c6, d6, e6, f6, g6, h6,
        a5, b5, c5, d5, e5, f5, g5, h5,
        a4, b4, c4, d4, e4, f4, g4, h4,
        a3, b3, c3, d3, e3, f3, g3, h3,
        a2, b2, c2, d2, e2, f2, g2, h2,
        a1, b1, c1, d1, e1, f1, g1, h1,
    };
    constexpr e_boardSquare c_allBoardSquares[] = {
        a8, b8, c8, d8, e8, f8, g8, h8,
        a7, b7, c7, d7, e7, f7, g7, h7,
        a6, b6, c6, d6, e6, f6, g6, h6,
        a5, b5, c5, d5, e5, f5, g5, h5,
        a4, b4, c4, d4, e4, f4, g4, h4,
        a3, b3, c3, d3, e3, f3, g3, h3,
        a2, b2, c2, d2, e2, f2, g2, h2,
        a1, b1, c1, d1, e1, f1, g1, h1,
    };


    enum e_file : int { fileA, fileB, fileC, fileD, fileE, fileF, fileG, fileH };
    enum e_rank : int { rank8, rank7, rank6, rank5, rank4, rank3, rank2, rank1 };

    constexpr inline e_boardSquare board_sq(e_rank rank, e_file file) {
        return e_boardSquare(c_maxFile*rank + file);
    }

    constexpr e_file all_files[] = { e_file::fileA, e_file::fileB, e_file::fileC, e_file::fileD,
        e_file::fileE, e_file::fileF, e_file::fileG, e_file::fileH };
    constexpr e_rank all_ranks[] = { e_rank::rank8, e_rank::rank7, e_rank::rank6, e_rank::rank5,
        e_rank::rank4, e_rank::rank3, e_rank::rank2, e_rank::rank1 };

    [[nodiscard]] constexpr inline e_file operator+(e_file x, int y) noexcept { return e_file(int(x)+y);}
    [[nodiscard]] constexpr inline e_file operator-(e_file x, int y) noexcept { return e_file(int(x)-y); }
    [[nodiscard]] constexpr inline e_file operator+(int y, e_file x) noexcept { return e_file(y+int(x));}
    [[nodiscard]] constexpr inline e_file operator-(int y, e_file x) noexcept { return e_file(y-int(x)); }


    [[nodiscard]] constexpr inline e_boardSquare operator-(e_boardSquare x,int y) noexcept { return e_boardSquare(int(x)-y);}
    [[nodiscard]] constexpr inline e_boardSquare operator-(e_boardSquare x,e_boardSquare y) noexcept { return e_boardSquare(int(x)-int(y));}
    [[nodiscard]] constexpr inline e_boardSquare operator+(e_boardSquare x,int y) noexcept { return e_boardSquare(int(x)+y);}
    constexpr inline e_boardSquare& operator++(e_boardSquare& orig) noexcept {
        orig = static_cast<e_boardSquare>(orig + 1);
        return orig;
    }
    constexpr inline e_boardSquare operator++(e_boardSquare& original, int) noexcept {
        e_boardSquare returnValue = original;
        ++original;
        return returnValue;
    }
    constexpr inline e_boardSquare& operator+=(e_boardSquare& lhs, const e_boardSquare& rhs) noexcept {
        lhs =  e_boardSquare((int)lhs + (int)rhs);
        return lhs;
    }

    template<e_pieceType i> constexpr auto directions = {0};
    template<> constexpr std::array<std::array<int,2>,4> directions<e_pieceType::bishop> =  {{{1,1},{1,-1},{-1,1},{-1,-1}}};
    template<> constexpr std::array<std::array<int,2>,4> directions<e_pieceType::rook> =  {{{1,0},{-1,0},{0,1},{0,-1}}};
    template<> constexpr std::array<std::array<int,2>,8>
    directions<e_pieceType::king> = {{{1,1},{1,0},{0,1},{-1,0},{0,-1},{-1,-1},{-1,1},{1,-1}}};
    template<> constexpr std::array<std::array<int,2>,8>
    directions<e_pieceType::knight> = {{{2,1},{1,2},{2,-1},{-1,2},{-2,1},{1,-2},{-1,-2},{-2,-1}}};

    struct ColouredPiece {
        e_pieceType type_;
        e_colour colour_;
    };
    
    struct placed_piece {
    public:
        e_pieceType piece_;
        e_boardSquare square_;
        e_colour colour_;
    };
    
    template <typename T>
    constexpr inline void hash_combine(std::size_t& seed, const T& v)
    {
        std::hash<T> hasher;
        seed ^= hasher(v) + 0x9e3779b9 + (seed<<6) + (seed>>2);
    } 
}

#endif /* types_hpp */

//
//  identityboard.hpp
//  ChessGameA
//
//  Created by Frederick Benjamin Woodruff on 20/06/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#ifndef identityboard_hpp
#define identityboard_hpp

#include <array>
#include <cstdint>
#include <vector>
#include <tuple>
#include "PiecesAndSquares.hpp"
#include "ChessMove.hpp"
#include "ChessPosition.hpp"

namespace chs {
    struct PossiblyPlacedPiece {
        bool isPlaced_;
        e_colour colour_;
        e_pieceType type_;
        e_boardSquare square_;
        int positionInTakens_;
    };
    
    struct LabelledPieces {
    private:
        std::array<uint8_t,c_maxSquare> pieceMap_;
        std::array<PossiblyPlacedPiece,32> IDpieces_;
        std::array<int,2> numberTaken;
    public:
        std::vector<uint8_t> moving_;
        static constexpr const uint8_t c_nulldif = 67;
        inline constexpr const PossiblyPlacedPiece& operator[](uint8_t i) const {return IDpieces_[i];}
        static inline LabelledPieces from_board(const ChessBoard&);
        void performMove(Move);
        constexpr int nextTakenPosition(e_colour colour) const {return numberTaken[colour];}
        constexpr inline uint8_t squareArrayID(e_boardSquare bs) const { return pieceMap_[bs]; }
        
    };

    
    inline LabelledPieces LabelledPieces::from_board(const ChessBoard& mb) { // constexpr in C++20
        LabelledPieces board;
        int p = 0;
        for(int k = 0; k < c_maxSquare ; k++) {
            board.pieceMap_[k] = c_nulldif;
            for( int j = 0 ; j < 2 ; j++ ) {
                for( int i = 0 ;i < 6; i++ ) {
                    if(mb.pieces[j][i]&Bitboard(e_boardSquare(k))) {
                        board.IDpieces_[p].isPlaced_=true;
                        board.IDpieces_[p].colour_ = e_colour(j);
                        board.IDpieces_[p].type_ = e_pieceType(i);
                        board.IDpieces_[p].square_ = e_boardSquare(k);
                        board.pieceMap_[k] = p;
                        p++;
                    }
                }
            }
        }
        board.numberTaken = {0};
        return board;
    }
}
#endif /* identityboard_hpp */

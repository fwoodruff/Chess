//
//  magic_slider.hpp
//  Chess_Engine
//
//  Created by Frederick Benjamin Woodruff on 30/03/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#ifndef magic_slider_hpp
#define magic_slider_hpp

#include "MagicNumbers.hpp"
#include "Masks.hpp"
#include <array>

namespace chs {
    template<e_pieceType type> constexpr int dbSize = []() constexpr {
        auto total = 0;
        for(const auto& siz : detail::magicSize<type>)
            total += 1<<siz;
        return total;
    }();
    template<e_pieceType type> constexpr auto dbBucketIndex = []{
        std::array<u_int64_t,c_maxSquare> indexVec {0};
        int total = 0;
        for (int i=0;i<c_maxSquare;i++) {
            indexVec[i] = total;
            total += 1ULL<<(detail::magicSize<type>[i]);
        }
        return indexVec;
    }
    ();
    template<e_pieceType type>
    constexpr Bitboard sliderOccupancyToMoves(Bitboard occupancy, const e_boardSquare& startSquare) {
        occupancy &= masks<type>[startSquare];
        Bitboard moveBitboard= 0ULL;
        auto startRank = startSquare%c_maxFile;
        auto startFile = startSquare/c_maxFile;
        for (const auto& dir : directions<type>) {
            for(auto i=1;i<c_maxFile;i++) {
                auto endRank = startRank+(i*dir[0]);
                auto endFile = startFile+(i*dir[1]);
                if(endRank>(c_maxRank-1) or endFile>(c_maxFile-1) or endRank<0 or endFile<0) {
                    break;
                }
                auto end = Bitboard(e_boardSquare(endRank + endFile*c_maxRank));
                moveBitboard|= Bitboard(e_boardSquare(endRank + endFile*c_maxRank));
                if(occupancy&end) {
                    break;
                }
            }
        }
        return moveBitboard;
    }


    constexpr auto get_all_occupancies(const Bitboard& board) {
        std::array<Bitboard,0x1000> occupancies;
        std::array<uint64_t,32> bitdex = {0};
        auto boardCopy = board;
        int N=0;
        e_boardSquare cumulative = a8;
        while(boardCopy) {
            boardCopy.LSByield(cumulative);
            bitdex[N++]=cumulative;
        }
        for(uint64_t i=0;i<(1ULL<<N);i++) {
            boardCopy = board;
            for(int j=0;j<N;j++) {
                uint64_t bit = (i>>j)%2;
                boardCopy ^= (bit << bitdex[j]);
            }
            occupancies[i]=boardCopy;
        }
        return occupancies;
    }

    template<e_pieceType type> constexpr inline auto hashPiece(const Bitboard& occupancy_, const e_boardSquare& square) {
        return ((detail::magics<type>[square]*occupancy_) >> (c_maxSquare-detail::magicSize<type>[square]))+dbBucketIndex<type>[square];
    };

    template<e_pieceType type>
    const auto sliderDatabase = [](){
        std::array<Bitboard,dbSize<type>> moveDatabase;
        for(const auto& square : c_allBoardSquares) {
            auto mask = masks<type>[square];
            auto occupancies = get_all_occupancies(mask);
            auto occupancySize = 1<<mask.occupancy();
            for (int j=0; j<occupancySize; j++) {
                auto moveBitboard = sliderOccupancyToMoves<type>(occupancies[j], square);
                auto mapKey = hashPiece<type>(occupancies[j], square);
                moveDatabase[mapKey] = moveBitboard;
            }
        }
        return moveDatabase;
    }
    ();
}


#endif /* magic_slider_hpp */

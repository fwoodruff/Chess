//
//  masks.hpp
//  Chess_Engine
//
//  Created by Frederick Benjamin Woodruff on 30/03/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#ifndef masks_hpp
#define masks_hpp

#include "PiecesAndSquares.hpp"
#include "Bitboard.hpp"

namespace chs {


    constexpr Bitboard upright = 0x102040810204080ULL;
    constexpr Bitboard upleft = 0x8040201008040201ULL;
    constexpr auto outer = Bitboard(e_file::fileA,e_file::fileH,e_rank::rank8,e_rank::rank1);

    namespace detail {
        using boardlist_t = std::array<Bitboard, c_maxSquare>;
        
        
        template<e_pieceType type>
        constexpr auto compile_masks(bool=true) {};
        template<>
        constexpr auto compile_masks<rook>(bool outer_mask) {
            boardlist_t result;
            for(const auto& rank : all_ranks) {
                for(const auto& file : all_files) {
                    Bitboard last_mask = 0ULL;
                    if (rank!=e_rank::rank8) last_mask |= Bitboard(e_rank::rank8);
                    if (file!=e_file::fileA) last_mask |= Bitboard(e_file::fileA);
                    if (rank!=e_rank::rank1) last_mask |= Bitboard(e_rank::rank1);
                    if (file!=e_file::fileH) last_mask |= Bitboard(e_file::fileH);
                    result[c_maxFile*rank + file] = (Bitboard(file)^Bitboard(rank));
                    if(outer_mask) {
                        result[c_maxFile*rank + file] &=~ last_mask;
                    }
                }
            }
            return result;
        }
        
        template<>
        constexpr auto compile_masks<e_pieceType::bishop>(bool outer_mask) {
            boardlist_t result;
            Bitboard big_right_mask[c_maxFile]{~0ULL};
            Bitboard big_left_mask[c_maxFile]{~0ULL};
            for(const auto& file: all_files) {
                if(file==e_file::fileA) continue;
                big_right_mask[file]=big_right_mask[file-1]&~Bitboard(file-1);
                big_left_mask[file]=big_left_mask[file-1]&~Bitboard(c_maxFile-file);
            }
            
            for(const auto& rank : all_ranks) {
                for(const auto& file : all_files) {
                    Bitboard ur=upright >>(c_maxFile-1)-file<< rank;
                    ur &= ((c_maxFile-1)-file>=e_file(rank)) ?
                    big_left_mask[(c_maxFile-1)-file-rank] : big_right_mask[rank-(c_maxFile-1)+file];
                    Bitboard ul=(file>=e_file(rank)) ?
                    upleft<<(file-rank) & big_right_mask[file-rank] :
                    upleft>>(rank-file) & big_left_mask[rank-file];
                    result[c_maxFile*rank+file] = (ul^ur);
                    if(outer_mask) {
                        result[c_maxFile*rank+file] &=~ outer;
                    }
                }
            }
            return result;
        }
        
        /*
        template<>
        constexpr auto compile_masks<e_pieceType::queen>(bool outer_mask) {
            assert(outer_mask);
            auto queen_masks = detail::compile_masks<rook>(true);
            const auto bishop_masks = detail::compile_masks<bishop>(true);
            for(int i=0;i<bishop_masks.size();i++) {
                queen_masks[i] |= bishop_masks;
            }
            return queen_masks;
        }*/

        
        template<e_pieceType type>
        constexpr auto build_masks() {
            boardlist_t output;
            for(const auto& rank : all_ranks)
                for(const auto& file : all_files)
                    for(const auto& dir : directions<type>)
                        if(rank<(c_maxRank-dir[0]) and file<(c_maxFile-dir[1]) and rank>(-1-dir[0]) and file>(-1-dir[1]))
                            output[c_maxFile*rank+file] |= 1ULL << ((rank+dir[0])*c_maxFile + file+dir[1]);
            return output;
        }
        
        
        

        template<> constexpr auto compile_masks<knight>(bool x) { return build_masks<knight>(); }
        template<> constexpr auto compile_masks<e_pieceType::king>(bool x) { return build_masks<e_pieceType::king>(); }
        
    }

    template<e_pieceType type>
    constexpr auto masks = detail::compile_masks<type>();

    template<e_pieceType type>
    constexpr auto outward_masks = detail::compile_masks<type>(false);
    


    constexpr auto pawn_masks = []{
        std::array<std::array<Bitboard,c_maxSquare>,2> output;
        for(int rank=0;rank<6;rank++) {
            output[white][8*rank]=Bitboard(e_boardSquare(8*rank+9));
            for(int file=1;file<7;file++)
                output[white][8*rank+file]=5ULL<<(8*rank+file+7);
            output[white][8*rank+7]=1ULL<<(8*rank+14);
        }
        for(int rank=2;rank<8;rank++) {
            output[black][8*rank]=Bitboard(e_boardSquare(8*rank-7));
            for(int file=1;file<7;file++)
                output[black][8*rank+file]=5ULL<<(8*rank+file-9);
            output[black][8*rank+7]=1ULL<<(8*rank-2);
        }
        return output;
    }();
}

#endif /* masks_hpp */

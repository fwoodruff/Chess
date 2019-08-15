//
//  subposition.hpp
//  Chess
//
//  Created by Frederick Benjamin Woodruff on 12/06/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#ifndef subposition_hpp
#define subposition_hpp


#include "NaiveChessPosition_h.hpp"
#include "Masks.hpp"
#include "MagicSlider.hpp"

namespace chs {

    template<typename... Args> bool NaiveChessPosition::isUnderAttack(e_colour c, e_boardSquare first, Args... args) const {
        return isUnderAttack(c,first) or isUnderAttack(c,args...);
    }

    template<e_pieceType type>
    int NaiveChessPosition::jumperMobility(Bitboard jumper_occupancy,const Bitboard& our_occupancy, const Bitboard& total_occupancy) {
        auto move_start = a8;
        int x = 0;
        while(jumper_occupancy) {
            jumper_occupancy.LSByield(move_start);
            auto move_bitboard = masks<type>[move_start] &~our_occupancy;
            x+=move_bitboard.occupancy();
        }
        return x;
    }
    
    template<e_pieceType type>
    int NaiveChessPosition::sliderMobility(Bitboard slider_occupancy,const Bitboard& our_occupancy, const Bitboard& total_occupancy) {
        auto move_start = a8;
        int x = 0;
        while(slider_occupancy) {
            slider_occupancy.LSByield(move_start);
            if constexpr(type!=queen) {
                const auto masked_occupancy = total_occupancy & masks<type>[move_start];
                const auto address = hashPiece<type>(masked_occupancy, move_start);
                const auto move_bitboard = sliderDatabase<type>[address] &~our_occupancy;
                x+=move_bitboard.occupancy();
            } else {
                const auto b_masked_occupancy = total_occupancy & masks<bishop>[move_start];
                const auto r_masked_occupancy = total_occupancy & masks<rook>[move_start];
                const auto b_address = hashPiece<bishop>(b_masked_occupancy, move_start);
                const auto r_address = hashPiece<rook>(r_masked_occupancy, move_start);
                x+= (Bitboard(sliderDatabase<bishop>[b_address],sliderDatabase<rook>[r_address]) &~our_occupancy).occupancy();
            }
        }
        return x;
    }
    
    /*
    template<>
    int NaiveChessPosition::sliderMobility<e_pieceType::queen>(Bitboard slider_occupancy,
                                                               const Bitboard& our_occupancy, const Bitboard& total_occupancy) {
        auto move_start = a8;
        int x = 0;
        while(slider_occupancy) {
            slider_occupancy.LSByield(move_start);
            auto masked_occupancy = total_occupancy & masks<rook>[move_start];
            auto address = hashPiece<rook>(masked_occupancy, move_start);
            auto move_bitboard = sliderDatabase<rook>[address] &~our_occupancy;
            x+=move_bitboard.occupancy();
        }
        return x;
    }*/
    
    
    
    inline bool NaiveChessPosition::operator==(const NaiveChessPosition& other) const noexcept {
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
        
        assert(occupancy==other.occupancy);
        assert(totalOccupancy == other.totalOccupancy);
        return true;
    }
    
    inline bool NaiveChessPosition::operator!=(const NaiveChessPosition& other) const noexcept {
        return !(*this==other);
    }
    
    template<e_moveType move_type> inline int NaiveChessPosition::heuristic(int parent_score) const {
        if(!pieces[black][king]) return 10'000;
            if(!pieces[white][king]) return -10'000;
                return pmheuristic<move_type>(parent_score);
    }
    template<e_moveType move_type> inline int NaiveChessPosition::pmheuristic(int x) const
    { return pieceHeuristic() + moveHeuristic();}
    template<> inline int NaiveChessPosition::pmheuristic<e_moveType::materialChanging>(int parent_score) const
    { return pieceHeuristic() + parent_score;} // check if this is ever used
    template<> inline int NaiveChessPosition::pmheuristic<e_moveType::staticExchange>(int parent_score) const
    { return pieceHeuristic() + parent_score;}
    template<> inline int NaiveChessPosition::pmheuristic<e_moveType::quiet>(int parent_score) const
    { return parent_score + moveHeuristic();}
    
    template <e_moveType move_type>
    NaiveChessPosition::ConsiderMove<move_type>::~ConsiderMove() {
        if constexpr (move_type==e_moveType::quiet) {
            this->position_.pieces[this->position_.turn][this->oldPosition_.pt_] ^= this->oldPosition_.moveBoard_;
        } else {
            this->position_ = this->oldPosition_;
        }
    }
    
    template <e_moveType move_type>
    NaiveChessPosition::ConsiderMove<move_type>::ConsiderMove(NaiveChessPosition& p,const Move& m) :
    ExecuteMove<move_type>(p,m) {
        //assert(m!=nullMove and m!=c_resignMove and m!=c_takeDrawMove);
    }
    
}

#endif /* subposition_hpp */

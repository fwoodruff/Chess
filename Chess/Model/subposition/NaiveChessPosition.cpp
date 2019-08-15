//
//  subposition.cpp
//  Chess
//
//  Created by Frederick Benjamin Woodruff on 12/06/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#include "NaiveChessPosition.hpp"

namespace chs {
    NaiveChessPosition NaiveChessPosition::makePosition() noexcept {
        NaiveChessPosition board;
        
        board.pieces[white]  = {Bitboard(e1), Bitboard(d1), Bitboard(a1,h1), Bitboard(c1,f1),  Bitboard(b1,g1), Bitboard(e_rank::rank2)};
        board.pieces[black]  = {Bitboard(e8), Bitboard(d8), Bitboard(a8,h8), Bitboard(c8,f8),  Bitboard(b8,g8), Bitboard(e_rank::rank7)};
        
        board.pos_flags.enPassant_=0;
        board.pos_flags.blackKingside_=true;
        board.pos_flags.blackQueenside_=true;
        board.pos_flags.whiteKingside_=true;
        board.pos_flags.whiteQueenside_=true;
        //history_->clear();
        //history_->insert({*this,1});
        //game_state_ = game_state::ongoing;
        board.occupancy[white] = Bitboard(e_rank::rank1,e_rank::rank2);
        board.occupancy[black] = Bitboard(e_rank::rank7,e_rank::rank8);
        board.totalOccupancy = Bitboard(board.occupancy);
        board.turn = white;
        return board; // make this constexpr
    };
    
    bool NaiveChessPosition::isUnderAttack(e_colour player, e_boardSquare square) const {
        auto diag_slider = Bitboard(pieces[~player][bishop],pieces[~player][queen]) & outward_masks<bishop>[square];
        auto  row_slider = Bitboard(pieces[~player][rook  ],pieces[~player][queen]) & outward_masks<rook>[square];
        Bitboard slider_attack_board;
        e_boardSquare sq = a8;
        Bitboard vul(square);
        while(diag_slider) {
            diag_slider.LSByield(sq);
            auto masked_occupancy = totalOccupancy & masks<bishop>[sq];
            auto address = hashPiece<bishop>(masked_occupancy, sq);
            slider_attack_board = {slider_attack_board,sliderDatabase<bishop>[address]};
        }
        sq = a8;
        while(row_slider) {
            row_slider.LSByield(sq);
            auto masked_occupancy = totalOccupancy & masks<rook>[sq];
            auto address = hashPiece<rook>(masked_occupancy, sq);
            slider_attack_board = {slider_attack_board,sliderDatabase<rook>[address]};
        }
        return bool(Bitboard(masks<e_pieceType::king>[square] & pieces[~player][e_pieceType::king],
                             masks<knight>[square] & pieces[~player][e_pieceType::knight],
                             slider_attack_board & vul,
                             pawn_masks[~player][square] & pieces[~player][e_pieceType::pawn]));
    }
    
    bool NaiveChessPosition::hasInsufficientMaterial() const noexcept {
        int rp=0;
        for(int i=0;i<2;i++)
            rp += !pieces[i][pawn] and !pieces[i][rook] and
            (pieces[i][bishop].occupancy()+pieces[i][knight].occupancy())<=1;
        return rp>1;
    }
}

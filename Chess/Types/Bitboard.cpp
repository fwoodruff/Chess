//
//  Bitboard.cpp
//  Chess
//
//  Created by Frederick Benjamin Woodruff on 30/03/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#include "Bitboard.hpp"
#include "PiecesAndSquares.hpp"
#include <cstddef>

/*
size_t std::hash<chs::Bitboard>::operator() (const chs::Bitboard& bboard) const {
    return std::hash<uint64_t>{}(bboard.repr_);
}*/

namespace chs {
    /*
    std::string Bitboard::draw() const noexcept {
        std::string s = "";
        for(int rank=e_rank::rank8;rank<c_maxRank;rank++) {
            s+="\n";
            for(int file=e_file::fileA;file<c_maxFile;file++) {
                if((repr_ & ( 1ULL << (8*rank+file) )) >> (8*rank+file)) s+=" #";
                else s+=" .";
            }
        }
        s+="\n";
        return s;
    }
     */
}

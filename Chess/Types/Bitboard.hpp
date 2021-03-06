//
//  Bitboard.hpp
//  Chess
//
//  Created by Frederick Benjamin Woodruff on 30/03/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#ifndef Bitboard_h
#define Bitboard_h

#include "Bitboard_h.hpp"
#include <type_traits>
#include <cstddef>
#include <iostream>
#include <bitset>

namespace chs {

    constexpr inline Bitboard::Bitboard(e_boardSquare first) noexcept : repr_(1ULL << first){}
    constexpr inline Bitboard::Bitboard(e_rank first) noexcept : repr_(0xffULL<<(8*first)){}
    constexpr inline Bitboard::Bitboard(e_file first) noexcept : repr_(0x101010101010101<<first){}
    constexpr inline Bitboard::Bitboard(uint64_t first) noexcept : repr_(first){}
    constexpr inline Bitboard::Bitboard() noexcept : repr_(0) {}

    template<typename T, size_t x>
    constexpr inline Bitboard::Bitboard(std::array<T,x> first) noexcept : repr_( [&first] {
        Bitboard z = 0;
        for(const auto& obj : first) z = Bitboard(z,obj);
        return z;
    }().repr_){}

    template<typename Tuple, std::size_t... I>
    constexpr inline auto Bitboard::tuple_to_bitboard(Tuple tupl, std::index_sequence<I...>) {
        return Bitboard(std::get<I>(tupl)...);
    }
    template<typename... T,typename Indices>
    constexpr Bitboard::Bitboard(std::tuple<T...> a) noexcept : repr_(tuple_to_bitboard(a, Indices{}).bb) {}

    /*
    template<typename... Args>
    constexpr Bitboard::Bitboard(Args... args) noexcept : repr_(Bitboard(args...).repr_ | ...) {}
    */
    
    template<typename T,typename, typename... Args>
    constexpr Bitboard::Bitboard(T first, Args... args) noexcept :
    repr_(Bitboard(first).repr_ | Bitboard(args...).repr_) {}
     
    constexpr inline auto Bitboard::LSB() const {
        #ifdef __GNUC__
        return __builtin_ctzll(repr_);
        #else
        static_assert(uint64_t(-1)==UINT64_MAX);
        constexpr const uint64_t bitscan_magic = 0x07edd5e59a4e28c2;
        constexpr const auto x = []() constexpr {
            std::array<int, c_maxSquare> result = {0};
            uint64_t bit=1; int i=0;
            do { result [(bit*bitscan_magic)>>58]=i; i++; bit<<=1; } while(bit);
            return result;
        }();
        return x[((repr_&-repr_)*bitscan_magic)>>58];
        #endif // __GNUC__
    }

    constexpr int Bitboard::occupancy() const noexcept {
        //return int(std::bitset<c_maxSquare>(repr_).count());
        #ifdef __GNUC__
        return __builtin_popcountll(repr_);
        #else // bug here
        constexpr uint64_t three64  = 0x3333333333333333;
        auto x =-( (repr_ >> 1) & 0x5555555555555555);
        x = (x & three64) + ((x >> 2) & three64);
        x = (x + (x >> 4)) & 0x0f0f0f0f0f0f0f0f;
        return (x * 0x0101010101010101) >> 56;
        #endif // __GNUC__
    }

    constexpr inline Bitboard& Bitboard::operator|=(const Bitboard& rhs) noexcept { repr_ |= rhs.repr_; return *this; }
    constexpr inline Bitboard& Bitboard::operator&=(const Bitboard& rhs) noexcept { repr_ &= rhs.repr_; return *this; }
    constexpr inline Bitboard& Bitboard::operator^=(const Bitboard& rhs) noexcept { repr_ ^= rhs.repr_; return *this; }
    constexpr inline Bitboard Bitboard::operator^(const Bitboard& rhs) const noexcept { return repr_^rhs.repr_; }
    constexpr inline Bitboard Bitboard::operator>>(const int& rhs) const noexcept{ return repr_>>rhs; }
    constexpr inline Bitboard Bitboard::operator<<(const int& rhs) const noexcept { return repr_<<rhs; }
    constexpr inline Bitboard Bitboard::operator&(const Bitboard& rhs) const noexcept { return repr_&rhs.repr_; }
    constexpr inline Bitboard Bitboard::operator~() const noexcept {return ~repr_;}
    constexpr inline Bitboard::operator bool() const { return bool(repr_); }
    constexpr inline bool Bitboard::operator==(const Bitboard& other) const noexcept { return repr_==other.repr_; }
    constexpr inline bool Bitboard::operator!=(const Bitboard& other) const noexcept { return repr_!=other.repr_; }
    
    constexpr inline e_boardSquare& Bitboard::LSByield(e_boardSquare& prev) & noexcept {
        auto out = LSB();
        prev += e_boardSquare(out);
        repr_ >>= out;
        repr_ &=~ 1ULL;
        return prev;
    }
    
    constexpr inline e_boardSquare& Bitboard::LSB_yield(e_boardSquare& cumul) const && noexcept {
        auto out = LSB();
        cumul += e_boardSquare(out);
        return cumul;
    }
    
    constexpr inline e_boardSquare Bitboard::LSB_yield() & noexcept {
        auto out = LSB();
        repr_ >>= out;
        repr_ &=~ 1ULL;
        return e_boardSquare(out);
    }
    
    constexpr inline e_boardSquare Bitboard::LSB_yield() const && noexcept {
        return e_boardSquare(LSB());
    }
    
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
}

inline size_t std::hash<chs::Bitboard>::operator() (const chs::Bitboard& bboard) const {
    return std::hash<uint64_t>{}(bboard.repr_);
}

#endif /* bitboard_h */

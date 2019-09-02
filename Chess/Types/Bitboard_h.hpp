//
//  Bitboard_h.hpp
//  Chess
//
//  Created by Frederick Benjamin Woodruff on 30/03/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#ifndef Bitboard_h_hpp
#define Bitboard_h_hpp

#include "PiecesAndSquares.hpp"
#include <stdio.h>
#include <array>
#include <type_traits>

namespace chs {struct Bitboard;}
template<>
struct std::hash<chs::Bitboard> {
public:
    size_t operator()(const chs::Bitboard&) const;
};

namespace chs {
    struct MagicNumber;
    struct Bitboard final {
    private:
        uint64_t repr_;
        constexpr auto LSB() const;
        template<typename Tuple, std::size_t... I>
        static constexpr auto tuple_to_bitboard(Tuple a, std::index_sequence<I...>);
    public:
        
        constexpr Bitboard(e_boardSquare first) noexcept;
        constexpr Bitboard(e_rank first) noexcept;
        constexpr Bitboard(e_file first) noexcept;
        constexpr Bitboard(uint64_t first) noexcept;
        constexpr Bitboard() noexcept;
        template<typename T, size_t x>
        constexpr Bitboard(std::array<T,x> first) noexcept;
        template<typename... T, typename Indices = std::make_index_sequence<sizeof...(T)>>
        constexpr Bitboard(std::tuple<T...> a) noexcept;
        template<typename T, typename = typename std::enable_if_t<!std::is_same_v<T, int>>, typename... Args>
        constexpr Bitboard(T, Args...) noexcept;
        constexpr inline explicit operator bool() const;
        constexpr inline Bitboard& operator|=(const Bitboard& rhs) noexcept;
        constexpr inline Bitboard& operator&=(const Bitboard& rhs) noexcept;
        constexpr inline Bitboard& operator^=(const Bitboard& rhs) noexcept;
        [[nodiscard]] constexpr inline bool operator==(const Bitboard& other) const noexcept;
        [[nodiscard]] constexpr inline bool operator!=(const Bitboard& other) const noexcept;
        [[nodiscard]] constexpr inline Bitboard operator^(const Bitboard&) const noexcept;
        [[nodiscard]] constexpr inline Bitboard operator&(const Bitboard&) const noexcept;
        [[nodiscard]] constexpr inline Bitboard operator~() const noexcept;
        [[nodiscard]] constexpr inline Bitboard operator-() const noexcept;
        [[nodiscard]] constexpr inline Bitboard operator>>(const int& rhs) const noexcept;
        [[nodiscard]] constexpr inline Bitboard operator<<(const int& rhs) const noexcept;
        [[nodiscard]] constexpr inline e_boardSquare& LSB_yield(e_boardSquare& cumul) const && noexcept;
        [[nodiscard]] constexpr inline e_boardSquare LSB_yield() const && noexcept;
        constexpr inline e_boardSquare LSB_yield() & noexcept;
        constexpr inline e_boardSquare& LSByield(e_boardSquare& cumul) & noexcept;
        [[nodiscard]] constexpr inline int occupancy() const noexcept;
        std::string draw() const noexcept;
        friend class MagicNumber;
        friend size_t std::hash<Bitboard>::operator() (const Bitboard&) const;
    };

}
#endif /* Bitboard_h_hpp */

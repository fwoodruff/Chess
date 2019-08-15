//
//  MagicNumbers.hpp
//  Chess
//
//  Created by Frederick Benjamin Woodruff on 30/03/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#ifndef MagicNumbers_hpp
#define MagicNumbers_hpp


#include "PiecesAndSquares.hpp"
#include "Bitboard.hpp"
#include <cstdint>

namespace chs {
    struct MagicNumber {
    private:
        const uint64_t magic_;
    public:
        constexpr MagicNumber(uint64_t);
        constexpr uint64_t operator*(const Bitboard&) const;
    };
    constexpr MagicNumber::MagicNumber(uint64_t magic) : magic_(magic) {}
    constexpr uint64_t MagicNumber::operator*(const Bitboard& other) const {
        return magic_*other.repr_;
    }
    namespace detail {
        template<e_pieceType type> constexpr auto magicSize {0};
        template<> constexpr std::array<int,c_maxSquare> magicSize<rook> {
            12, 11, 11, 11, 11, 11, 11, 12,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            12, 11, 11, 11, 11, 11, 11, 12,
        };
        template<> constexpr std::array<int,c_maxSquare> magicSize<bishop> {
            6, 5, 5, 5, 5, 5, 5, 6,
            5, 5, 5, 5, 5, 5, 5, 5,
            5, 5, 7, 7, 7, 7, 5, 5,
            5, 5, 7, 9, 9, 7, 5, 5,
            5, 5, 7, 9, 9, 7, 5, 5,
            5, 5, 7, 7, 7, 7, 5, 5,
            5, 5, 5, 5, 5, 5, 5, 5,
            6, 5, 5, 5, 5, 5, 5, 6,
        };
        template<e_pieceType type> constexpr auto magics = {0};
        template<> constexpr std::array<MagicNumber,c_maxSquare> magics<rook> {
            5368291030714159232ULL,
            306262367921020930ULL,
            72067494208077890ULL,
            72066458900759584ULL,
            144148173561270432ULL,
            216173881692555270ULL,
            2377903498060366080ULL,
            2666131401392390400ULL,
            2392555039752320ULL,
            2603645145337867ULL,
            288371251120984064ULL,
            6922877194200975616ULL,
            37717682950246432ULL,
            4785160504672264ULL,
            7600116479296000ULL,
            38421438683682048ULL,
            35734136569856ULL,
            1211472972710151232ULL,
            150083874164736ULL,
            4684467092192764416ULL,
            36311371792779265ULL,
            1152930305265639488ULL,
            18018796582208008ULL,
            2306979904303972515ULL,
            2341889674370359296ULL,
            369436980682754ULL,
            705895056228516ULL,
            4521196108972064ULL,
            146648535881220100ULL,
            2306406105197334792ULL,
            288256781611307009ULL,
            283469972481ULL,
            5770511908864032ULL,
            21462604434116608ULL,
            2306124759487750144ULL,
            432372021242896640ULL,
            2522596367843460352ULL,
            1297599659824386688ULL,
            2306405963495640152ULL,
            4612952657038934784ULL,
            53327387721728ULL,
            2319354495303434256ULL,
            9112753562255377ULL,
            19703523802480649ULL,
            8800388317200ULL,
            180425477262082072ULL,
            423320839520328ULL,
            81074728703492100ULL,
            7359022529705083136ULL,
            141012804568192ULL,
            2760707121642210432ULL,
            36591851682152960ULL,
            2306968926971592832ULL,
            144679032053826048ULL,
            7584070745484035072ULL,
            450500738880159872ULL,
            775156244021382ULL,
            432416484354970118ULL,
            4665738839025778707ULL,
            5190434888856109313ULL,
            5066618443194378ULL,
            72339627499061253ULL,
            19212905107982340ULL,
            18146316781674ULL
        };
        template<> constexpr std::array<MagicNumber,c_maxSquare> magics<bishop> {
            1143560833500800ULL,
            1178679837392964ULL,
            1134698600333312ULL,
            1130300268692480ULL,
            565217872314914ULL,
            571774101225472ULL,
            290279695336448ULL,
            141289401106432ULL,
            633353628024896ULL,
            598224553510212ULL,
            158881619705860ULL,
            4416040091668ULL,
            4467441401856ULL,
            37396886323209ULL,
            75076096509961ULL,
            70669627818564ULL,
            1196543675859968ULL,
            562967267910656ULL,
            563018977378336ULL,
            422246992625664ULL,
            145290180958368ULL,
            844564785106947ULL,
            602536684818944ULL,
            1161232522682880ULL,
            1231453092381713ULL,
            580688203221058ULL,
            1340304942956938ULL,
            567348134187040ULL,
            281544652505120ULL,
            149671590891524ULL,
            282714108432385ULL,
            283675075936512ULL,
            565289657960448ULL,
            580611129680384ULL,
            1126054542576642ULL,
            70677982118400ULL,
            1425018609238176ULL,
            1134706747781120ULL,
            1152427782963330ULL,
            1139133276000388ULL,
            158366317944834ULL,
            145187108036875ULL,
            35392006934528ULL,
            35322230491140ULL,
            167130066846721ULL,
            725712044564992ULL,
            1127720989753857ULL,
            1200669012795426ULL,
            1416248321654784ULL,
            142941075193857ULL,
            35468136677440ULL,
            142968791238664ULL,
            598203078672384ULL,
            8865366280227ULL,
            1697714689934848ULL,
            1201800602486784ULL,
            1268838710642832ULL,
            36293581999104ULL,
            142945122650112ULL,
            1126451848020496ULL,
            550024380952ULL,
            1478379961321736ULL,
            228835891513888ULL,
            1130301040427264ULL
        };
    }
}

#endif /* MagicNumbers_hpp */

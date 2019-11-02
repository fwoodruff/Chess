//
//  AVXfunctions.c
//  Chess
//
//  Created by Frederick Benjamin Woodruff on 02/09/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#include <immintrin.h>
#include <stdint.h>

int dotpop(int N, const int64_t* const x, const uint64_t* const y) { // include a non-AVX version
    int cumul = 0;
    int n=0;
    for(;;) {
/* 
In caller have:
ifdef __GNUC__
uint64_t* __attribute__((__may_alias__)) p = reinterpret_cast<uint64_t*>(&
*/

#ifdef __AVX512__
        __m512i yv = _mm512_load_si512( (__m512i*) &(y[n]) );
        __m512i ypopv = _mm512_popcnt_epi64(yv);
        __m512i xv = _mm512_load_si512( (__m512i*) &(x[n]) );
        __m512i outpv = _mm512_mul_epi32(ypopv, xv);
        const int loopcount = 8;
#else
        uint64_t ypopvin[4];
        for(int j = 0;j<4;j++)  ypopvin[j] = _mm_popcnt_u64(y[j+n]);
        __m256i ypopv = _mm256_load_si256( (__m256i*) &(ypopvin[0]) );
        __m256i xv = _mm256_load_si256( (__m256i*) &(x[n]) );
        __m256i outpv = _mm256_mul_epi32(ypopv, xv);
        const int loopcount = 4;
#endif
        uint64_t *ptr = (uint64_t*)&outpv;
        for(int j = 0;j<loopcount;j++) {
            cumul += ptr[j];
            if(++n==N) return cumul;
        }
    }
}


int avxdotpop12(const int16_t* const x, const uint64_t* const y) { // include a non-AVX version
    int16_t ypopvin[16] = {0};
    for(int j = 0;j<12;j++)  ypopvin[j] = (int16_t) _mm_popcnt_u64(y[j]);
    __m256i yu = _mm256_load_si256( (__m256i*) ypopvin );
    __m256i xu = _mm256_load_si256( (__m256i*) x );
    __m256i out = _mm256_mullo_epi16(yu, xu);
    out = _mm256_add_epi16(out, _mm256_srli_epi16(out, 8));
    out = _mm256_add_epi16(out, _mm256_srli_epi16(out, 4));
    out = _mm256_add_epi16(out, _mm256_srli_epi16(out, 2));
    int16_t *ptr = (int16_t*)&out;
    return *ptr + *(ptr +1);
}



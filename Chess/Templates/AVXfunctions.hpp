//
//  AVXfunctions.h
//  Chess
//
//  Created by Frederick Benjamin Woodruff on 01/09/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#ifndef AVXfunctions_h
#define AVXfunctions_h

#include <stdio.h>
#include <immintrin.h>
#include <stdint.h>

inline int dotpop(int N, const int64_t* const x, const uint64_t* const y) {
    int cumul = 0;
    int n=0;
    for(;;) {
#ifdef __AVX512__
        __m512i yv = _mm512_load_si512( (__m512i*) &(y[n]) );
        __m512i ypopv = _mm512_popcnt_epi64(yv);
        __m512i xv = _mm512_load_si512( (__m512i*) &(x[n]) );
        __m512i outpv = _mm512_mul_epi32(ypopv, xv);
        constexpr int loopcount = 8;
#else
        uint64_t ypopvin[4];
        for(int j = 0;j<4;j++)  ypopvin[j] = _mm_popcnt_u64(y[j+n]);
        __m256i ypopv = _mm256_load_si256( (__m256i*) &(ypopvin[0]) );
        __m256i xv = _mm256_load_si256( (__m256i*) &(x[n]) );
        __m256i outpv = _mm256_mul_epi32(ypopv, xv);
        constexpr int loopcount = 4;
#endif
        uint64_t *ptr = (uint64_t*)&outpv;
        for(int j = 0;j<loopcount;j++) {
            cumul += ptr[j];
            if(++n==N) return cumul;
        }
    }
}
#endif /* AVXfunctions_h */

//
//  AVXfunctions.h
//  Chess
//
//  Created by Frederick Benjamin Woodruff on 01/09/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#ifndef AVXfunctions_h
#define AVXfunctions_h
#ifdef __cplusplus
extern "C" {
#endif // __cplusplus

    namespace frd {
        int dotpop(int N, const int64_t* const x, const uint64_t* const y);
    }

#ifdef __cplusplus
}
#endif // __cplusplus
#endif /* AVXfunctions_h */

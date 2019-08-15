//
//  Movelist.hpp
//  Chess
//
//  Created by Frederick Benjamin Woodruff on 12/06/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#ifndef Movelist_hpp
#define Movelist_hpp

#include "Bitboard.hpp"
#include "ChessMove.hpp"
#include "NaiveChessPosition.hpp"
#include "PerformMove.hpp"
#include "VectorStack.hpp"
#include <array>
#include <iostream>

namespace chs {
    template <e_moveType move_type>
    void NaiveChessPosition::getMoveList(MoveList& moveList, bool sorted, bool highScoresFirst, e_boardSquare bsq) noexcept {
        populatePieceMoves<rook,move_type>  (moveList, bsq);
        populatePieceMoves<bishop,move_type>(moveList, bsq);
        populatePieceMoves<knight,move_type>(moveList, bsq);
        populatePieceMoves<queen,move_type> (moveList, bsq);
        populatePieceMoves<king,move_type>  (moveList, bsq);
        populatePieceMoves<pawn,move_type>  (moveList, bsq);
        
        
        if(sorted and !moveList.empty()) {
            std::array<int,c_maxMoves> scores;
            for(int i = 0 ; i < moveList.size(); i++) {
                auto _= ConsiderMove<e_moveType::all>(*this,moveList[i]); // moveType::all ?
                scores[i] = pieceHeuristic();
            }
            sortMoves(moveList, scores,0, int(moveList.size())-1, highScoresFirst);
        }
    }
    
    template<e_pieceType type, e_moveType moveType>
    void NaiveChessPosition::populatePieceMoves(MoveList& moveList, e_boardSquare specificSquare) const noexcept {
        if(!pieces[white][king] or !pieces[black][king]) return;
        Bitboard specificBoard;
        if constexpr (moveType==e_moveType::staticExchange) specificBoard = Bitboard(specificSquare);
        
        // pawn start
        if constexpr (type==pawn) {
            const auto pawns_no_prom = pieces[turn][type] & (~Bitboard((turn==white ? e_rank::rank7 : e_rank::rank2)));
            if constexpr (moveType==e_moveType::quiet or moveType == e_moveType::all) {
                Bitboard moveBitboards[2] {0};
                moveBitboards[0] = turn==white? pawns_no_prom>>c_maxFile : pawns_no_prom<<c_maxFile;
                moveBitboards[0] &= ~totalOccupancy;
                moveBitboards[1] = turn==white? (moveBitboards[0]>>c_maxFile) & Bitboard(e_rank::rank4):
                                                (moveBitboards[0]<<c_maxFile) & Bitboard(e_rank::rank5);
                moveBitboards[1] &= ~totalOccupancy;
                e_flagType flagTypes[2] ={normalMove,pawnDouble};
                int rankdown[2][2] = {{c_maxFile,-c_maxFile},{2*c_maxFile,-2*c_maxFile}};
                for(int j = 0; j < 2; j++) {
                    auto moveEnd = a8;
                    if constexpr(moveType == e_moveType::staticExchange) moveBitboards[j] &= specificBoard;
                    while(moveBitboards[j]) {
                        moveBitboards[j].LSByield(moveEnd);
                        moveList.push({moveEnd + rankdown[j][turn], moveEnd, type, flagTypes[j]});
                    }
                }
            }
            if constexpr (moveType==e_moveType::staticExchange or moveType == e_moveType::all
                          or moveType == e_moveType::materialChanging) {
                Bitboard move_bitboards[5] = {0};
                const auto pawns_prom = pieces[turn][type] & Bitboard(turn==white?e_rank::rank7:e_rank::rank2);
                move_bitboards[0] = turn==white? (pawns_no_prom&~Bitboard(e_file::fileH))>>7 :
                                                 (pawns_no_prom&~Bitboard(e_file::fileH))<<9;
                move_bitboards[1] = turn==white? (pawns_no_prom&~Bitboard(e_file::fileA))>>9 :
                                                 (pawns_no_prom&~Bitboard(e_file::fileA))<<7;
                if constexpr (moveType==e_moveType::materialChanging or moveType == e_moveType::all)
                    move_bitboards[2] = turn==white? pawns_prom>>8 & ~totalOccupancy :
                                                     pawns_prom<<8 & ~totalOccupancy ;
                move_bitboards[3] = turn==white? (pawns_prom&~Bitboard(e_file::fileH))>>7:
                                                 (pawns_prom&~Bitboard(e_file::fileH))<<9;
                move_bitboards[4] = turn==white? (pawns_prom&~Bitboard(e_file::fileA))>>9:
                                                 (pawns_prom&~Bitboard(e_file::fileA))<<7;
                int rankdown[5][2] = {{c_maxFile-1,-c_maxFile-1},{c_maxFile+1,-c_maxFile+1},{c_maxFile,-c_maxFile},
                    {c_maxFile-1,-c_maxFile-1},{c_maxFile+1,-c_maxFile+1}};
                for(int j = 0; j < 5; j++) {
                    auto moveEnd = a8;
                    if(j!=2) move_bitboards[j] &= occupancy[~turn];
                    if constexpr(moveType == e_moveType::staticExchange) move_bitboards[j] &= specificBoard;
                    if(j<2) {
                        while(move_bitboards[j]) {
                            move_bitboards[j].LSByield(moveEnd);
                            moveList.push({moveEnd + rankdown[j][turn], moveEnd, type, takingMove});
                        }
                    } else {
                        while(move_bitboards[j]) {
                            int q[5] = {0,0,0,1,1};
                            move_bitboards[j].LSByield(moveEnd);
                            e_flagType flagTypes[2][4] = {{queenPromote,rookPromote,bishopPromote,knightPromote}, {queenPromoteTake,rookPromoteTake,bishopPromoteTake,knightPromoteTake}};
                            for(int i=0;i<4;i++)
                                moveList.push({moveEnd + rankdown[j][turn], moveEnd, type, flagTypes[q[j]][i]});
                        }
                    }
                }
            }
            if constexpr (moveType == e_moveType::all or moveType == e_moveType::materialChanging) {
                
                if(pos_flags.enPassant_!=0) {
                    
                    e_boardSquare ep_start = turn==white? a5 : a4;
                    e_boardSquare ep_end = turn==white? a6 : a3;
                    auto their_pawn = (Bitboard(e_boardSquare(pos_flags.enPassant_-2+ep_start)));
                    Bitboard ep_mask[2] {(their_pawn & ~Bitboard(e_file::fileA)) >>1, (their_pawn & ~Bitboard(e_file::fileH)) <<1};
                    int shift[2] = {-1,1};
                    
                    for(int i=0 ; i<2; i++) if(ep_mask[i]&pieces[turn][pawn]) {
                        moveList.push({e_boardSquare(unsigned(ep_start) + pos_flags.enPassant_-2 +shift[i]),
                            e_boardSquare(unsigned(ep_end) + pos_flags.enPassant_-2), type, enPassant});
                    }
                }
            }
            
            // pawn end
            
            
            
            
            
        } else {
            auto start=a8;
            auto pieceOccupancy = pieces[turn][type];
            while(pieceOccupancy) {
                pieceOccupancy.LSByield(start);
                Bitboard moveBoard;
                if constexpr (type == bishop or type == rook) {
                    Bitboard maskedOccupancy = totalOccupancy & masks<type>[start];
                    auto address = hashPiece<type>(maskedOccupancy, start);
                    moveBoard = sliderDatabase<type>[address];
                } else if constexpr (type==queen) {
                    Bitboard maskedOccupancyB = totalOccupancy & masks<bishop>[start];
                    auto addressB = hashPiece<bishop>(maskedOccupancyB, start);
                    Bitboard maskedOccupancyR = totalOccupancy & masks<rook>[start];
                    auto addressR = hashPiece<rook>(maskedOccupancyR, start);
                    moveBoard = Bitboard(sliderDatabase<bishop>[addressB],sliderDatabase<rook>[addressR]);
                } else { moveBoard = masks<type>[start]; }
                if constexpr (moveType==e_moveType::quiet or moveType == e_moveType::all) {
                    auto quietMoveBoard = moveBoard& ~totalOccupancy;
                    auto end = a8;
                    while(quietMoveBoard) {
                        quietMoveBoard.LSByield(end);
                        moveList.push({start,end,type,normalMove});
                    }
                }
                if constexpr (moveType==e_moveType::materialChanging or
                              moveType == e_moveType::staticExchange or moveType == e_moveType::all) {
                    moveBoard &= occupancy[~turn];
                    auto end = a8;
                    if constexpr(moveType == e_moveType::staticExchange) moveBoard &= specificBoard;
                    while(moveBoard) {
                        moveBoard.LSByield(end);
                        moveList.push({start,end,type,takingMove});
                    }
                }
            }
            if constexpr (type==e_pieceType::king and moveType==e_moveType::all) {
                if(turn==white) {
                    if (
                        pos_flags.whiteKingside_
                        and not (totalOccupancy & Bitboard(f1,g1))
                        and not isUnderAttack(e_colour::white,e1,f1))
                        moveList.push(kingsideCastle<e_colour::white>);
                    if (
                        pos_flags.whiteQueenside_
                        and not (totalOccupancy & Bitboard(b1,c1,d1))
                        and not isUnderAttack(e_colour::white,d1,e1))
                        moveList.push(queensideCastle<e_colour::white>);
                } else {
                    if(
                       pos_flags.blackKingside_
                       and not (totalOccupancy & Bitboard(f8,g8))
                       and not isUnderAttack(e_colour::black,e8,f8)) {
                        moveList.push(kingsideCastle<e_colour::black>);
                    }
                    if (
                        pos_flags.blackQueenside_
                        and not (totalOccupancy & Bitboard(b8,c8,d8))
                        and not isUnderAttack(e_colour::black,d8,e8))
                        moveList.push(queensideCastle<e_colour::black>);
                }
            }
        }
    }
}

#endif /* Movelist_hpp */

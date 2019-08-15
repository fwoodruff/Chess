//
//  ChessPosition.cpp
//  Chess
//
//  Created by Frederick Benjamin Woodruff on 15/06/2019.
//  Copyright © 2019 Frederick Benjamin Woodruff. All rights reserved.
//

#include "ChessPosition.hpp"
#include "TreeSearch.hpp"
#include <vector>
#include <iostream>

namespace chs {
    /*
    ChessPosition ChessPosition::makePosition() noexcept {
        ChessPosition mb;
        mb.NaiveChessPosition::operator=(NaiveChessPosition::makePosition());
        mb.gameState_ = e_gameState::ongoing;
        mb.ruleOfFifty_ =0;
        return mb;
    }
     */
    
    ChessPosition::ChessPosition() noexcept {
        NaiveChessPosition::operator=(NaiveChessPosition::makePosition());
        gameState_ = e_gameState::ongoing;
        ruleOfFifty_ =0;
    }
    
    bool ChessPosition::hasInsufficientMaterial() const noexcept {
        int rp=0;
        for(int i=0;i<2;i++)
            rp += !pieces[i][pawn] and !pieces[i][rook] and
            (pieces[i][bishop].occupancy()+pieces[i][knight].occupancy())<=1;
        return rp>1;
    }
    

    ChessPosition ChessPosition::performMove(Move move,const map& history) const noexcept {
        
        ChessPosition new_position = *this;
        
        if(move==c_resignMove) {
            new_position.gameState_ = new_position.turn == e_colour::white?
            e_gameState::whiteResigned : e_gameState::blackResigned;
            return new_position;
        }
        
        if(move==c_takeDrawMove) {
            if(turn==white) {
                if(new_position.ruleOfFifty_>100) {
                    new_position.gameState_ = e_gameState::whiteClaimedFiftyQuietMoves;
                } else {
                    new_position.gameState_ = e_gameState::whiteClaimedThreefoldRepetition;
                }
            } else {
                if(new_position.ruleOfFifty_>100) {
                    new_position.gameState_ =e_gameState::blackClaimedFiftyQuietMoves;
                } else {
                    new_position.gameState_  = e_gameState::blackClaimedThreefoldRepetition;
                }
            }
            return new_position;
        }
        
        ExecuteMove<e_moveType::all>(new_position,move);
        
        new_position.gameState_ = e_gameState::ongoing;
        auto ml = new_position.makeMoveList(history);
        
        
        
        
        if(new_position.ruleOfFifty_>150) {
            new_position.gameState_ = e_gameState::draw75quietMoves;
        }

        if(history.find(new_position) != history.end())
            if(history.at(new_position)>3)
                new_position.gameState_ = e_gameState::fivefoldRepetition;
        
        
        
        if(new_position.hasInsufficientMaterial()) {
            new_position.gameState_ = e_gameState::insufficientMaterial;
        }
        
        
        
        
        if(ml.empty()) {
            Bitboard tmp = new_position.pieces[new_position.turn][e_pieceType::king];
            new_position.gameState_= new_position.isUnderAttack(new_position.turn, std::move(tmp).LSB_yield())?
            (new_position.turn==white?e_gameState::blackCheckmates:e_gameState::whiteCheckmates) : e_gameState::noMoveStaleMate;
        }
        
        bool advance =
        new_position.pos_flags.blackKingside_ != pos_flags.blackKingside_
        or new_position.pos_flags.blackQueenside_ != pos_flags.blackQueenside_
        or new_position.pos_flags.whiteKingside_ != pos_flags.whiteKingside_
        or new_position.pos_flags.whiteQueenside_ != pos_flags.whiteQueenside_
        or new_position.pieces[e_colour::white][e_pieceType::pawn] != pieces[e_colour::white][e_pieceType::pawn]
        or new_position.pieces[e_colour::black][e_pieceType::pawn] != pieces[e_colour::black][e_pieceType::pawn];
        
        bool take = false;
        for(int i = 0; i < 5; i++) for(int j =0;j<2;j++)
            if (new_position.pieces[j][i].occupancy() != pieces[j][i].occupancy()) take = true;
        
        if(advance or take) new_position.ruleOfFifty_ = 0;
        else new_position.ruleOfFifty_++;
        
        return new_position;
    }
    
    [[nodiscard]] std::vector<Move> ChessPosition::makeMoveList(const ChessBoard::map& history) const {
        
        auto positionCopy = *this;
        if(positionCopy.gameState_ != e_gameState::ongoing) return std::vector<Move>();
        bool draw_claimable = positionCopy.ruleOfFifty_>50;
        Move memory[c_maxMoves];
        MoveList ml_in(memory);
        std::vector<Move> ml_out;
        positionCopy.getMoveList<e_moveType::all>(ml_in, false);

        for(int i=0;i<ml_in.size();i++) {
            auto _= ConsiderMove<e_moveType::all>(positionCopy,ml_in[i]);
            auto kings = positionCopy.pieces[~positionCopy.turn][e_pieceType::king];
            
            if(history.find(positionCopy) != history.end())
                if(history.at(positionCopy)>2)
                    draw_claimable = true;
            
            if(!positionCopy.isUnderAttack(~positionCopy.turn,std::move(kings).LSB_yield())) {
                ml_out.push_back(ml_in[i]);
            }
        }
        if(history.find(positionCopy) != history.end()) {
            if(history.at(positionCopy)>2) {
                draw_claimable = true;
            }
        }
        if(ml_out.size()!=0) {
            if(draw_claimable) {
                ml_out.push_back(c_takeDrawMove);
            }
            ml_out.push_back(c_resignMove);
        }
        return ml_out;
    }
    
    QMove ChessPosition::pickMove(const int depth,const map& history,std::atomic<bool>& stateDidChange) {
        auto movelist = makeMoveList(history);
        if(movelist.empty()) return c_terminalQMove;
        Move best_move;
        Score best_score = turn==white? Score({-100000,false}) : Score({100000,false});
        Move memory[2000];
        //auto memory = std::make_unique<std::array<Move,2000>>();
        
        for(const auto& move : movelist) {
            if(stateDidChange.load(/*std::memory_order_acquire*/)) return c_nullQMove;
            if(move==c_takeDrawMove) {
                if(best_score.value_ < 0 != (turn==white)) {
                    best_move = move;
                    best_score = {0,true};
                }
                continue;
            }
            if(move==c_resignMove) continue;
            auto _= ConsiderMove<e_moveType::all>(*this, move);
            
            //MoveList ml(&((*memory)[0]));
            MoveList ml(memory);
            auto score = alphaBeta<e_moveType::all>(ml,depth, -100'000, 100'000,stateDidChange, turn!=e_colour::white);
            
            if(score.value_ > best_score.value_ != (turn==white)) {
                best_move = move;
                best_score = score;
            }
        }
        if(best_score.complete_) {
            return {best_move,QMove::c_maxQ};
        } else {
            return {best_move,unsigned(depth)};
        }
        
        
        
        //if((best_score < turn==white? -200 : 200) != (turn==white)) best_move = resign;
        //return best_move;
        return c_nullQMove;
    }
    
    QMove ChessPosition::pickMoveDebug(const int depth,const map& history,std::atomic<bool>& stateDidChange) {
        //unsigned quality = depth;
        assert(depth>=0);
        
        auto movelist = makeMoveList(history);
        if(movelist.empty()) return c_terminalQMove;
        Move best_move;
        Score best_score = turn==white? Score({-100000,false}) : Score({100000,false});
        Move memory[2000];
        //auto memory = std::make_unique<std::array<Move,2000>>();
        
        for(const auto& move : movelist) {
            if(stateDidChange.load(std::memory_order_acquire)) return c_nullQMove;
            if(move==c_takeDrawMove) {
                if(best_score.value_ < 0 != (turn==white)) {
                    best_move = move;
                    best_score = {0,true};
                }
                continue;
            }
            if(move==c_resignMove) continue;
            auto _= ConsiderMove<e_moveType::all>(*this, move);
            
            //MoveList ml(&((*memory)[0]));
            MoveList ml(memory);
            auto score = alphaBetaDebug(ml,depth, -100'000, 100'000,stateDidChange, turn==e_colour::white);
            
            if(score.value_ > best_score.value_ != (turn==white)) {
                best_move = move;
                best_score = score;
            }
            // if scores are the same, pick randomly?
        }
        if(best_score.complete_) {
            return {best_move,QMove::c_maxQ};
        } else {
            return {best_move,unsigned(depth)};
        }
    }
    
    
    
    
    QMove ChessPosition::pick_response(int depth, Move move_,
                                      const map& history, std::atomic<bool>& state_did_change) const {
        ChessPosition copy_board = performMove(move_,history);
        return copy_board.pickMove(depth, history, state_did_change);
    }
    
    QMove ChessPosition::pick_response_Debug(int depth, Move move_,
                                            const map& history, std::atomic<bool>& state_did_change) const {
        ChessPosition copy_board = performMove(move_,history);
        return copy_board.pickMoveDebug(depth, history, state_did_change);
    }
    
    
    
    
    
    
    
    /*
    e_pieceType ChessPosition::missing(const ChessPosition& before, const ChessPosition& after) noexcept {
        assert (before.turn != after.turn);
        for(int i=0; i<6; i++)
            if(before.pieces[after.turn][i].occupancy() > after.pieces[after.turn][i].occupancy())
                return e_pieceType(i);
        return king;
    }*/
}


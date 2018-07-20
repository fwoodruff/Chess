/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.game.daemons;

import chessapi.ChessUtil.Colour;
import static chessapi.ChessUtil.Colour.*;
import chessapi.board.BoardPosition;
import chessapi.pieces.Move;

import java.util.List;
import chessapi.board.Position;


/**
 *
 * @author freddiewoodruff
 */
public class AlphaBetaUtil {
    
    private AlphaBetaUtil(){throw new AssertionError();}

    
    public static int alphaBeta(Position node, int depth, Colour player, Matilda book) {
        
        return alphaBeta(node, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, player, book,0); 
    }
    
    private static int alphaBeta(Position node, int depth, int alpha, int beta, Colour player,Matilda book,int siblings) {

        
        if (Thread.currentThread().isInterrupted()) {         
            return 0;
        }
        
        if (depth==0) return node.heuristic()+ EvaluationUtil.mobility(node,siblings);
        
        int v;
        if (player==WHITE){
            v= Integer.MIN_VALUE;
            List<Move> ML = node.moveList(book);
            // what to do if there are no moves?
            int possiblyLegalMoves=ML.size();
            for (Move tempM : ML) {
                /* use this instead if Guava cannot be downloaded.*/
                //Position temp = new BoardPosition(node,tempM);
                Position temp = CacheFactory.BOARDCACHE.makePosition(node, tempM);                
                /**/

                if (temp.isInCheck(WHITE)) {
                    possiblyLegalMoves--; // white cannot play this move since it leaves white in check
                    continue;
                }
                if(possiblyLegalMoves==0){
                    if(node.isInCheck(WHITE)){ // white is in check with no moves: checkmate!
                        return -EvaluationUtil.KINGVALUE;
                    } else { // White is not in check but can't move? Stalemate.
                        return 0; 
                    }
                }

                v = Math.max(v, alphaBeta(temp, depth - 1, alpha, beta, BLACK,book,ML.size()));
                alpha = Math.max(alpha,v);
                if (beta<=alpha) {
                    break;
                }
            }
            return v;
        } else {
            v = Integer.MAX_VALUE;
            List<Move> ML = node.moveList(book);
            int possiblyLegalMoves=ML.size();
            for (Move tempM : ML) {
                Position temp = CacheFactory.BOARDCACHE.makePosition(node, tempM);
                // use this instead if Guava cannot be downloaded.
                //Position temp = new BoardPosition(node,tempM); 
                if (temp.isInCheck(BLACK)) {
                    possiblyLegalMoves--; // white cannot play this move since it leaves white in check
                    continue;
                }
                if(possiblyLegalMoves==0){
                    if(node.isInCheck(BLACK)){ // black is in check with no moves: checkmate!
                        return EvaluationUtil.KINGVALUE;
                    } else { // Black is not in check but can't move? Stalemate.
                        return 0; 
                    }
                }
                v = Math.min(v, alphaBeta(temp, depth - 1, alpha, beta, WHITE,book,ML.size()));
                beta = Math.min(beta,v);
                
                if(beta<=alpha){
                    break;
                }
            }
            return v;
        }
    }
}

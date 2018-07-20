/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.board;

import chessapi.ChessUtil.Colour;
import static chessapi.ChessUtil.Colour.*;
import static chessapi.ChessUtil.*;
import chessapi.pieces.PieceType;
import static chessapi.pieces.PieceType.*;
import chessapi.pieces.Tile;

/**
 *
 * @author freddiewoodruff
 */
public final class UnderAttackUtil {
    private UnderAttackUtil(){throw new AssertionError();}
    
    /**
     * Works out if a player is under attack on a given square.
     * Note that for castling through check, it is necessary to calculate
     * whether a square with no associated piece colour is under attack.
     * @param position
     * @param tile
     * @param player
     * @return 
     */
    
    
    
    public static boolean isUnderAttack(Position position, Tile tile, Colour player) {
        return isUnderAttack(position.getArrangement(), tile.getRank(),tile.getFile(), player);
    }
    
    
    private static boolean isUnderAttack(Arrangement squares, int rank, int file, Colour who){

        
        assert (who == WHITE || who == BLACK) : who;
        
        PieceType KING = null;
        PieceType QUEEN = null;
        PieceType ROOK = null;
        PieceType BISHOP = null;
        PieceType KNIGHT = null;
        PieceType PAWN = null;
        if(who==WHITE) {
            KING = BLACKKING;
            QUEEN = BLACKQUEEN;
            ROOK = BLACKROOK;
            BISHOP = BLACKBISHOP;
            KNIGHT = BLACKKNIGHT;
            PAWN = BLACKPAWN;
        }
        if(who==BLACK) {
            KING = WHITEKING;
            QUEEN = WHITEQUEEN;
            ROOK = WHITEROOK;
            BISHOP = WHITEBISHOP;
            KNIGHT = WHITEKNIGHT;
            PAWN = WHITEPAWN;
        }
        
        if (rank>0) {
            if (squares.getPieceOn(rank-1,file)==KING) 
                return true;
            if (file>0) if (squares.getPieceOn(rank-1,file-1)==PAWN || squares.getPieceOn(rank-1,file-1)==KING)
                return true;
            if(file<FILELENGTH-1) if (squares.getPieceOn(rank-1,file+1)==PAWN || squares.getPieceOn(rank-1,file+1)==KING)
                return true;
        }
        
        if (file>0) if (squares.getPieceOn(rank,file-1)==KING)
            return true;
        
        if (file<FILELENGTH-1) if (squares.getPieceOn(rank,file+1)==KING) 
            return true;
        
        if (rank<RANKLENGTH-1) {
            if (squares.getPieceOn(rank+1,file)==KING) 
                return true;
            if (file>0) if (squares.getPieceOn(rank+1,file-1)==KING) 
                    return true;
            if (file<RANKLENGTH-1) if (squares.getPieceOn(rank+1,file+1)==KING)
                    return true;
        }
        for (int k = rank+1; k < RANKLENGTH; k++) {
            if(squares.getPieceOn(k,file)==NOPIECE) {}
            else if (squares.getPieceOn(k,file)==QUEEN || squares.getPieceOn(k,file)==ROOK)
                return true;
            else break;
        }
        for (int k = rank-1; k >= 0; k--) {
            if (squares.getPieceOn(k,file)==NOPIECE) {}
            else if (squares.getPieceOn(k,file)==QUEEN ||squares.getPieceOn(k,file)==ROOK)
                return true;
            else break;
        }
        for (int k = file-1; k >= 0; k--) {
            if (squares.getPieceOn(rank,k)==NOPIECE) {}
            else if (squares.getPieceOn(rank,k)==QUEEN ||squares.getPieceOn(rank,k)==ROOK)
                return true;
            else break;
        }
        for (int k = file+1; k < FILELENGTH; k++) {
            if (squares.getPieceOn(rank,k)==NOPIECE) {}
            else if (squares.getPieceOn(rank,k)==QUEEN ||squares.getPieceOn(rank,k)==ROOK)
                return true;
            else break;
        }
        int UL = Math.min(file, rank);
        for (int k = 1; k <= UL; k++) {
            if (squares.getPieceOn(rank-k,file-k)==NOPIECE) {}
            else if (squares.getPieceOn(rank-k,file-k)==QUEEN ||squares.getPieceOn(rank-k,file-k)==BISHOP)
                return true;
            else break;
        }
        int UR = Math.min(7-file, rank);
        for (int k = 1; k <= UR; k++) {
            if (squares.getPieceOn(rank-k,file+k)==NOPIECE) {}
            else if (squares.getPieceOn(rank-k,file+k)==QUEEN ||squares.getPieceOn(rank-k,file+k)==BISHOP)
                return true;
            else break;
        }
        int DR = Math.min(7-file, 7-rank);
        for (int k = 1; k <= DR; k++) {
            if (squares.getPieceOn(rank+k,file+k)==NOPIECE) {}
            else if (squares.getPieceOn(rank+k,file+k)==QUEEN ||squares.getPieceOn(rank+k,file+k)==BISHOP)
                return true;
            else break;
        }
        int DL = Math.min(file, 7-rank);
        for (int k = 1; k <= DL; k++) {
            if (squares.getPieceOn(rank+k,file-k)==NOPIECE) {}
            else if (squares.getPieceOn(rank+k,file-k)==QUEEN ||squares.getPieceOn(rank+k,file-k)==BISHOP)
                return true;
            else break;
        }
        
        if(rank<6 && file<7){
            if(squares.getPieceOn(rank+2,file+1)==KNIGHT)
                return true;
        }
        if(rank<7 && file<6){
            if(squares.getPieceOn(rank+1,file+2)==KNIGHT)
                return true;
        }
        if(rank>1 && file>0){
            if(squares.getPieceOn(rank-2,file-1)==KNIGHT){
                return true;
            }
        }
        if(rank>0 && file>1){
            if(squares.getPieceOn(rank-1,file-2)==KNIGHT)
                return true;
        }
        
        if(rank<7 && file>1){
            if(squares.getPieceOn(rank+1,file-2)==KNIGHT)
                return true;
        }
        
        if(rank>1 && file<7){
            if(squares.getPieceOn(rank-2,file+1)==KNIGHT)
                return true;
        }
        
        if(rank<6 && file>0){
            if(squares.getPieceOn(rank+2,file-1)==KNIGHT)
                return true;
        }
        if(rank>0 && file<6){
            if(squares.getPieceOn(rank-1,file+1)==KNIGHT)
                return true;
        }
        return false;
    }
}

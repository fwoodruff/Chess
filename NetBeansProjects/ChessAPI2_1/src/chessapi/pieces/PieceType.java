
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.pieces;


import chessapi.ChessUtil;
import chessapi.ChessUtil.Colour;
import java.util.List;
import chessapi.board.Position;


/**
 *
 * @author freddiewoodruff
 */
public enum PieceType implements Piece {
    
    
    BLACKBISHOP(Bishop.Black) {
        @Override public String toString() {
            return "b";
        }
    },
    WHITEBISHOP(Bishop.White) {
        @Override public String toString() {
            return "B";
        }
    },
    BLACKKNIGHT(Knight.Black) {
        @Override public String toString() {
            return "n";
        }
    },
    BLACKKING(King.Black) {
        @Override public String toString() {
            return "k";
        }
    },
    BLACKQUEEN(Queen.Black) {
        @Override public String toString() {
            return "q";
        }
    },
    BLACKROOK(Rook.Black) {
        @Override public String toString() {
            return "r";
        }
    },
    BLACKPAWN(Pawn.Black) {
        @Override public String toString() {
            return "p";
        }
    },
    WHITEQUEEN(Queen.White) {
        @Override public String toString() {
            return "Q";
        }
    },
    WHITEKING(King.White) {
        @Override public String toString() {
            return "K";
        }
    },
    WHITEKNIGHT(Knight.White) {
        @Override public String toString() {
            return "N";
        }
    },
    WHITEROOK(Rook.White) {
        @Override public String toString() {
            return "R";
        }
    },
    WHITEPAWN(Pawn.White) {
        @Override public String toString() {
            return "P";
        }
    },
    NOPIECE(NoPiece.INSTANCE) {
        @Override public String toString() {
            return " ";
        }
    },;


    private final Piece piece;
    

    PieceType(Piece piece){ this.piece = piece; }
    
    
    @Override
    public List<Move> getMoves(Position p, Tile tile) {
        return piece.getMoves(p,tile);
    }
    
    @Override
    public Colour getColour(){
        return this.piece.getColour();
    }
    
    
}

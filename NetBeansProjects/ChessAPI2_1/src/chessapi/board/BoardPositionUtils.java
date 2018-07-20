/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.board;

import chessapi.ChessUtil;
import static chessapi.ChessUtil.Colour.*;
import static chessapi.ChessUtil.NOENPASSANT;
import chessapi.pieces.Move;
import static chessapi.pieces.Move.MoveFlavour.*;
import static chessapi.pieces.PieceType.*;

/**
 * Tools for constructing a board from a move.
 * @author freddiewoodruff
 */
class BoardPositionUtils {
    private BoardPositionUtils() {throw new AssertionError();}
    /*
    protected static ChessUtil.Colour takenKing(Position positionBeforeMove, Move m) {
        ChessUtil.Colour takenKing= NOCOLOUR;
        if(positionBeforeMove.getKingTaken()==NOCOLOUR) {
            if (positionBeforeMove.getPieceOn(m.toPoint())==BLACKKING) {
                takenKing = BLACK;
            }
            if (positionBeforeMove.getPieceOn(m.toPoint())==WHITEKING) {
                takenKing = WHITE;
            }
        }
        return takenKing;
    }*/
    
    protected static int enpassantsquare(Move move) {
        if(move.flavour()==pawnDouble) {
            assert move.oldFile()==move.newFile();
            return move.oldFile();
        } else {
            return NOENPASSANT;
        }
    }
}

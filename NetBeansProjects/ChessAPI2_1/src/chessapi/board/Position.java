/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.board;

import chessapi.ChessUtil.Colour;
import chessapi.game.daemons.Matilda;
import chessapi.pieces.Move;
import chessapi.pieces.Tile;
import java.util.List;

/**
 *
 * @author freddiewoodruff
 */
public interface Position extends Arrangement, Castlable {


    public List<Move> moveList();
    public List<Move> moveList(Matilda book);
    
    // returns the colour of the first king that has been taken, otherwise NOCOLOUR
    //public Colour getKingTaken();
    public int heuristic();
    
    public boolean isInCheck(Colour ofKing);
    
    public Colour whoseTurn();
    public int getEnPassantSquare();
    public int gameTurnNumber();
    public int turnsSinceLastCaptureOrAdvance();

    public Arrangement getArrangement();

    public Castlable getCastlable();
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.pieces;

import chessapi.ChessUtil.Colour;
import static chessapi.ChessUtil.Colour.*;
import static chessapi.pieces.Move.MoveFlavour.ordinary;
import java.util.LinkedList;
import java.util.List;
import chessapi.board.Position;
/**
 *
 * @author freddiewoodruff
 */
enum Knight implements Piece  {

    Black(BLACK),White(WHITE);
    
    final Colour colour;
    Knight(Colour c) {
        this.colour = c;
    }

    @Override
    public Colour getColour() {
        return colour;
    }

    @Override public String toString() {
        return "Knight"+colour;
    }
    
    private static final int[][] vectors={{2,1},{1,2},{-2,1},{1,-2},{2,-1},{-1,2},{-2,-1},{-1,-2}};



    @Override
    public List<Move> getMoves(Position p, Tile tile) {
        List<Move> moveList = new LinkedList<>();
        
        if (p.whoseTurn()==colour) {
            for (int[] i : vectors) PieceMoveUtility.jumpMove(i[0], i[1],tile,p,moveList,this.getColour(),ordinary);
        }
        
        return moveList;
    }
}
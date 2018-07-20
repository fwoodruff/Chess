/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.pieces;


import chessapi.board.*;
import chessapi.ChessUtil.Colour;
import static chessapi.ChessUtil.Colour.*;
import static chessapi.pieces.PieceMoveUtility.*;
import java.util.*;
import java.util.stream.*;

/**
 *
 * @author freddiewoodruff
 */
enum Queen implements Piece  {
    Black(BLACK),White(WHITE);
    
    final Colour colour;
    Queen(Colour c) {
        this.colour = c;
    }

    @Override
    public Colour getColour() {
        return colour;
    }

    @Override public String toString() {
        return "Queen"+colour;
    }
    
    @Override
    public List<Move> getMoves(Position position, Tile tile) {
        List<Move> ML = new ArrayList<>();
        if (position.whoseTurn()==colour) {
            
            ML.addAll(diagonal(this.getColour(),position,tile));
            ML.addAll(plumb(this.getColour(),position,tile));
        }
        return ML;
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.pieces;



import chessapi.ChessUtil.Colour;
import java.util.*;
import java.util.stream.Stream;
import chessapi.board.Position;

/**
 *
 * @author freddiewoodruff
 */
interface Piece {
    List<Move> getMoves(Position p, Tile tile);
    Colour getColour();

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.pieces;


import chessapi.ChessUtil.Colour;
import java.util.LinkedList;
import java.util.List;
import chessapi.board.Position;

/**
 *
 * @author freddiewoodruff
 */
enum NoPiece implements Piece {
    INSTANCE;
    @Override
    public Colour getColour() {
        return Colour.NOCOLOUR;
    }
    @Override public String toString() {
        return "No Piece";
    }

    @Override
    public List<Move> getMoves(Position p, Tile tile) {
        return new LinkedList<>();
    }
}

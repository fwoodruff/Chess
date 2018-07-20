/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.board;

import chessapi.ChessUtil.Colour;
import chessapi.pieces.Tile;
import chessapi.pieces.PieceType;

import java.util.List;


/**
 *
 * @author freddiewoodruff
 */
public interface Arrangement {

    PieceType getPieceOn(Tile p);
    PieceType getPieceOn(int rank,int file);

    /**
     * Returns a list of where pieces are on the board.
     * @return
     */
    List<PlacedPiece> listPieces();

    /**
     * Returns the tile the player's king is on.
     * @param player
     * @return
     */
    Tile getKingTile(Colour player);
}

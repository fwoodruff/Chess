/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.board;

import static chessapi.ChessUtil.*;
import chessapi.pieces.Tile;
import chessapi.pieces.PieceType;
import static chessapi.pieces.PieceType.*;
import java.util.*;


/**
 *
 * @author freddiewoodruff
 */
class PieceListArrangement implements Arrangement {
     List<PlacedPiece> pieceList;
    
    
    public PieceListArrangement() {
        Arrangement BR = new BoardArrangement();
        pieceList = BR.listPieces();
    }
    
    public PieceListArrangement(List<PlacedPiece> pieceList) {
        this.pieceList = pieceList;
    }

    public List<List<PieceType>> getBoard() {
        List<List<PieceType>> board = new ArrayList<>();
        List<PieceType> row = new ArrayList<>();
        for (int i = 0; i < RANKLENGTH; i++) {
            for (int j = 0; j < FILELENGTH; j++) {
                row.add(NOPIECE);
            }
            board.add(row);
            row.clear();
        }
        
        pieceList.forEach(p ->
            board
                .get(p.getFile())
                .set(p.getRank(),
                    p.getType())
        );
        return board;
    }

    @Override
    public List<PlacedPiece> listPieces() {
        return pieceList;
    }

    @Override
    public PieceType getPieceOn(Tile p) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setTile(PlacedPiece p) {
        pieceList.parallelStream()
                .filter((pp) -> (pp.getTile().equals(p.getTile())))
                .forEachOrdered((pp) -> {
            pieceList.remove(pp);
         });
        pieceList.add(p);
    }

    @Override
    public PieceType getPieceOn(int rank, int file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Tile getKingTile(Colour player) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }




}

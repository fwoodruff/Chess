/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.board;

import chessapi.pieces.Tile;
import chessapi.pieces.Move;
import chessapi.pieces.PieceType;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author freddiewoodruff
 */
public final class PlacedPiece {
    private final PieceType type;
    private final Tile place;
    
    public PlacedPiece(PieceType type, Tile t){
        this.type = type;
        this.place = new Tile(t);
    }
    
    public List<Move> listMoves(Position p){
        return this.type.getMoves(p, place);
    }

    public int getRank() {
        return place.getRank();
    }

    public int getFile() {
        return place.getFile();
    } 
    public PieceType getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        final PlacedPiece other = (PlacedPiece) obj;
        if (this.getRank() != other.getFile()) { return false; }
        if (this.getFile() != other.getFile()) { return false; }
        return this.type == other.type;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.type);
        hash = 53 * hash + this.getRank();
        hash = 53 * hash + this.getRank();
        return hash;
    }
    


    
    @Override public String toString() {
        return type.toString() + " on " + this.getTile();
    }

    Tile getTile() {
        return place;
    }

}

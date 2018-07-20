/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.pieces;

import java.awt.Point;
import static chessapi.GUI.Window.PIXELS;

/**
 * A tile is a square on the board denoted by its rank (8-1) and file (a-g)
 * mapped to 0-7.
 * A new Tile is rejected if it is not on the board.
 * @author freddiewoodruff
 */
public class Tile {
    private final int rank;
    private final int file;
    
    public Tile(int rank,int file) {
        assert (rank>=0 && rank<=7) : rank;
        assert  (file>=0 && file<=7) : file;
            
        this.rank = rank;
        this.file = file;
    }
    
    public Tile(Point point) {
        rank = point.y/PIXELS;
        file = point.x/PIXELS;
    }
    
    public Tile displacement(int rankDisplace,int fileDisplace) {
        int newRank = rank + rankDisplace;
        int newFile = file + fileDisplace;
        assert (newRank>=0 && newRank<=7) : this;
        assert  (newFile>=0 && newFile<=7) : this;
        
        return new Tile(newRank,newFile);
    }

    public Tile(Tile tile) {
        this.rank = tile.getRank();
        this.file = tile.getFile();
    }
    public int getRank() {
        return rank;
    }
    public int getFile() {
        return file;
    }
    


    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + this.rank;
        hash = 41 * hash + this.file;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tile other = (Tile) obj;
        if (this.rank != other.rank) {
            return false;
        }
        if (this.file != other.file) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        StringBuilder tileStringBuilder = new StringBuilder();

        //tileStringBuilder.append("abcdefgh".charAt(this.getFile()));
        //tileStringBuilder.append(8-this.getRank());
        
        tileStringBuilder.append("(");
        tileStringBuilder.append(this.getRank());
        tileStringBuilder.append(",");
        tileStringBuilder.append(this.getFile());
        tileStringBuilder.append(")");
        return tileStringBuilder.toString();
    }
}
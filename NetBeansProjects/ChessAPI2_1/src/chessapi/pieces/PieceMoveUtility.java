/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.pieces;

import chessapi.ChessUtil;
import chessapi.ChessUtil.Colour;
import static chessapi.ChessUtil.Colour.NOCOLOUR;
import chessapi.pieces.Move.MoveFlavour;

import static chessapi.pieces.Move.MoveFlavour.ordinary;
import java.util.*;
import chessapi.board.Position;

/**
 *
 * @author freddiewoodruff
 */
public final class PieceMoveUtility {
    private PieceMoveUtility(){};
    
    protected static void jumpMove(int dispR, int dispF,Tile tile,Position p,List<Move> moveList,Colour c, MoveFlavour f) {
        if ((tile.getRank()+dispR >= 0) && (tile.getRank()+dispR <= 7)
        && (tile.getFile()+dispF >= 0) && (tile.getFile()+dispF <= 7)) {
            Tile toTile = tile.displacement(dispR,dispF);
            
            if(p.getPieceOn(toTile).getColour()!=c)
                moveList.add(new Move(tile,toTile,f));
        }
    }
    
    protected static List<Move> forDirection(Colour pieceColour, Position position,Tile moveFrom,int rDisp, int fDisp,int dist,Move.MoveFlavour f) {
        List<Move> ML = new ArrayList<>();
        for(int k=1; k<=dist;k++) {
            
            Tile moveTo = moveFrom.displacement(k*rDisp, k*fDisp);

             
            ChessUtil.Colour square = position.getPieceOn(moveTo).getColour();
            
            Move m = new Move(moveFrom,moveTo,f);

            if(square==NOCOLOUR) {
                ML.add(m);
            } else if(square==pieceColour) {
                    break;
            } else {
                    ML.add(m);
                    break;
            }
        }
        return ML;
    }
    
    protected static List<Move> diagonal(Colour pieceColour,Position position, Tile tile) {
        int UR = Math.min(tile.getRank(), 7-tile.getFile());
        int UL = Math.min(tile.getRank(), tile.getFile());
        int DR = Math.min(7-tile.getRank(), 7-tile.getFile());
        int DL = Math.min(7-tile.getRank(), tile.getFile());
        List<Move> ML = new ArrayList<>();
        
        ML.addAll(forDirection(pieceColour,position,tile,-1,1,UR,ordinary));
        ML.addAll(forDirection(pieceColour,position,tile,1,-1,DL,ordinary));
        ML.addAll(forDirection(pieceColour,position,tile,1,1,DR,ordinary));
        ML.addAll(forDirection(pieceColour,position,tile,-1,-1,UL,ordinary));
        return ML;
    }
    
    protected static List<Move> plumb(Colour pieceColour,Position position, Tile tile) {
        List<Move> ML = new ArrayList<>();
        ML.addAll(forDirection(pieceColour,position,tile,0,1,7-tile.getFile(),ordinary));
        ML.addAll(forDirection(pieceColour,position,tile,0,-1,tile.getFile(),ordinary));
        ML.addAll(forDirection(pieceColour,position,tile,1,0,7-tile.getRank(),ordinary));
        ML.addAll(forDirection(pieceColour,position,tile,-1,0,tile.getRank(),ordinary));
        return ML;
    }
}

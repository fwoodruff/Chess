/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.pieces;


import chessapi.ChessUtil.Colour;
import static chessapi.ChessUtil.Colour.*;
import static chessapi.ChessUtil.NOENPASSANT;
import static chessapi.ChessUtil.not;
import static chessapi.pieces.Move.MoveFlavour.*;
import static chessapi.pieces.PieceType.*;
import java.util.*;
import chessapi.board.Position;

/**
 *
 * @author freddiewoodruff
 */
enum Pawn implements Piece  {
    Black(BLACK),White(WHITE);
    
    final Colour colour;
    Pawn(Colour c) {
        this.colour = c;
    }

    @Override
    public Colour getColour() {
        return colour;
    }

    @Override public String toString() {
        return "Pawn"+colour;
    }
    


    @Override
    public List<Move> getMoves(Position p, Tile tile) {
        List<Move> moveList = new LinkedList<>();
        if (p.whoseTurn()!=colour) { return moveList; }
        
        assert tile.getRank()!=0;
        assert tile.getRank()!=7;

        int advance = this.getColour() == WHITE? -1:1;
        
        Tile takeLeft=null;
        Tile takeRight=null;
        
        Tile advanceOne = tile.displacement(advance, 0);
        if (tile.getFile() > 0 ) {
            takeLeft = tile.displacement(advance, -1);
        }
        if (tile.getFile() < 7 ) {
            takeRight = tile.displacement(advance, 1);
        }
        if (takeLeft!=null && p.getPieceOn(takeLeft).getColour()==not(this.getColour())) {
            if (takeLeft.getRank()==0 || takeLeft.getRank()==7) {
                moveList.add(new Move(tile,takeLeft,queenPromote));
                moveList.add(new Move(tile,takeLeft,rookPromote));
                moveList.add(new Move(tile,takeLeft,bishopPromote));
                moveList.add(new Move(tile,takeLeft,knightPromote));
            } else {
                moveList.add(new Move(tile,takeLeft,ordinary));
            }
        }
        if (takeRight!=null && p.getPieceOn(takeRight).getColour()==not(this.getColour())) {
            if (takeRight.getRank()==0 || takeRight.getRank()==7) {
                moveList.add(new Move(tile,takeRight,queenPromote));
                moveList.add(new Move(tile,takeRight,rookPromote));
                moveList.add(new Move(tile,takeRight,bishopPromote));
                moveList.add(new Move(tile,takeRight,knightPromote));
            } else {
                moveList.add(new Move(tile,takeRight,ordinary));
            }
        }
        if (p.getPieceOn(advanceOne)==NOPIECE) {
            if (advanceOne.getRank() == 7 || advanceOne.getRank() == 0){
                moveList.add(new Move(tile,advanceOne,queenPromote));
                moveList.add(new Move(tile,advanceOne,rookPromote));
                moveList.add(new Move(tile,advanceOne,bishopPromote));
                moveList.add(new Move(tile,advanceOne,knightPromote));
            } else {
                moveList.add(new Move(tile,advanceOne,ordinary));
            }
            int startingRank= (this.getColour() == WHITE)? 6:1;
            Tile doubleStep = advanceOne.displacement(advance, 0);
            if(p.getPieceOn(doubleStep)==NOPIECE){
                if (tile.getRank()==startingRank) {
                    moveList.add(new Move(tile,doubleStep,pawnDouble));
                }
            }
                    
            
            
        }
        if (p.getEnPassantSquare()!=NOENPASSANT) {
            
            if((tile.getRank()==3 && this.getColour()==WHITE) ||
                   ( tile.getRank()==4 && this.getColour()==BLACK) )
            {
                if (takeLeft!=null){
                    if (takeLeft.getFile() == p.getEnPassantSquare()) {
                        moveList.add(new Move(tile,takeLeft,enPassant));
                    }
                }
                if (takeRight!=null){
                    if (takeRight.getFile() == p.getEnPassantSquare()) {
                        moveList.add(new Move(tile,takeRight,enPassant));
                    }
                }
            }
        }
        return moveList;
    }
}

        
     

    


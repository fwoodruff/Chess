/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.pieces;

import chessapi.board.Position;
import static chessapi.pieces.Move.MoveFlavour.*;
import static chessapi.pieces.PieceType.*;
import java.util.Objects;



/**
 *
 * @author freddiewoodruff
 */
public class Move {


    public enum MoveFlavour {ordinary,pawnDouble,
            queenPromote,knightPromote,bishopPromote,rookPromote,
            queensideCastle,kingsideCastle, enPassant
            ,king}
    
    private final Tile oldPoint;
    private final Tile newPoint;

    private final MoveFlavour flavour;
    
    public int oldRank(){
        return oldPoint.getRank();
    }
    public int oldFile(){
        return oldPoint.getFile();
    }
    public int newRank(){
        return newPoint.getRank();
    }
    public int newFile(){
        return newPoint.getFile();
    }
    public Tile toPoint() {
        return newPoint;
    }
    public Tile fromPoint() {
        return oldPoint;
    }
    
    

    public MoveFlavour flavour() { return flavour; }
    
    // may have to make flavour mutable
    
    public Move(Tile oldPoint, Tile newPoint, MoveFlavour flavour) {
        this.oldPoint = oldPoint;
        this.newPoint = newPoint;
        this.flavour = flavour;
    }
    
    public Move(Tile oldPoint, Tile newPoint) {
        this.oldPoint = oldPoint;
        this.newPoint = newPoint;
        this.flavour = ordinary;
    }
    
    public static Move addFlavour(Move humanMove,Position position) {
                // reflavour move
        PieceType movingPiece = position.getPieceOn(humanMove.fromPoint());
        assert movingPiece.getColour()==position.whoseTurn();
        // is the move a pawn double?
        switch (movingPiece) {
            case BLACKPAWN:
                assert (humanMove.newRank()!=7 || humanMove.flavour!=ordinary);
                if(position.getPieceOn(humanMove.oldRank(),humanMove.newFile())==WHITEPAWN)
                    return new Move(humanMove.oldPoint,humanMove.newPoint,enPassant);
                if(humanMove.oldRank()==1 && humanMove.newRank()==3)
                    return new Move(humanMove.oldPoint,humanMove.newPoint,pawnDouble);
            case WHITEPAWN:
                assert (humanMove.newRank()!=0 || humanMove.flavour!=ordinary);
                if(position.getPieceOn(humanMove.oldRank(),humanMove.newFile())==BLACKPAWN)
                    return new Move(humanMove.oldPoint,humanMove.newPoint,enPassant);
                if(humanMove.oldRank()==6 && humanMove.newRank()==4)
                    return new Move(humanMove.oldPoint,humanMove.newPoint,pawnDouble);
            case BLACKKING:
            case WHITEKING:
                int fileDisp = humanMove.newFile()-humanMove.oldFile();
                switch(fileDisp) {
                    case 2:
                        return new Move(humanMove.oldPoint,humanMove.newPoint,kingsideCastle);
                    case -2:
                        return new Move(humanMove.oldPoint,humanMove.newPoint,queensideCastle);
                    default:
                        return new Move(humanMove.oldPoint,humanMove.newPoint,king);
                }
                
        }
        return humanMove;
    }
    
    
    


    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.oldPoint);
        hash = 71 * hash + Objects.hashCode(this.newPoint);
        switch(this.flavour){
            case queenPromote:
            case knightPromote:
            case bishopPromote:
            case rookPromote:
                hash = 71 * hash + Objects.hashCode(this.flavour);
                
        }
        hash = 71 * hash;
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
        final Move other = (Move) obj;
        if (!Objects.equals(this.oldPoint, other.oldPoint)) {
            return false;
        }
        if (!Objects.equals(this.newPoint, other.newPoint)) {
            return false;
        }
        
        
        if (this.flavour != other.flavour) {
            if(this.flavour==queenPromote || other.flavour==queenPromote) {
                return false;
            }
            if(this.flavour==rookPromote || other.flavour==rookPromote) {
                return false;
            }
            if(this.flavour==bishopPromote || other.flavour==bishopPromote) {
                return false;
            }
            if(this.flavour==knightPromote || other.flavour==knightPromote) {
                return false;
            }
        }
        return true;
    }
    
    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(oldPoint);
        sb.append(",");
        sb.append(newPoint);
        sb.append(",");
        sb.append(flavour);
        sb.append(")");
        return sb.toString();
    }
    

}

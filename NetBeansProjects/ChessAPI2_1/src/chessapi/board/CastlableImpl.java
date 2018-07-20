/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.board;

import chessapi.pieces.Move;
import static chessapi.pieces.PieceType.*;
import chessapi.pieces.Tile;


/**
 *
 * @author freddiewoodruff
 */
class CastlableImpl implements Castlable {
    private final boolean WhiteQueenside;
    private final boolean WhiteKingside;
    private final boolean BlackQueenside;
    private final boolean BlackKingside;

    CastlableImpl(Castlable c) {
        WhiteQueenside = c.getWhiteQueenside();
        WhiteKingside = c.getWhiteKingside();
        BlackQueenside = c.getBlackQueenside();
        BlackKingside = c.getBlackKingside();
    }

    @Override
    public boolean getWhiteQueenside() { return WhiteQueenside; }

    @Override
    public boolean getWhiteKingside() { return WhiteKingside; }

    @Override
    public boolean getBlackQueenside() { return BlackQueenside; }

    @Override
    public boolean getBlackKingside() { return BlackKingside; }

    public CastlableImpl(){
        WhiteQueenside = true;
        WhiteKingside = true;
        BlackQueenside = true;
        BlackKingside = true;
    }
    
    private static final Tile whitekingrook = new Tile(7,7);
    private static final Tile whitequeenrook = new Tile(7,0);
    private static final Tile blackkingrook = new Tile(0,7);
    private static final Tile blackqueenrook = new Tile(0,0);
    
    
    public CastlableImpl(Castlable castlable, Arrangement boardBeforeMove, Move m) {
        boolean whitekingside = castlable.getWhiteKingside();
        boolean whitequeenside = castlable.getWhiteQueenside();
        boolean blackkingside = castlable.getBlackKingside();
        boolean blackqueenside = castlable.getBlackQueenside();
        
        // check if move lands on a rook
        if (boardBeforeMove.getPieceOn(m.toPoint())==WHITEROOK) {
            if (m.toPoint().equals(whitekingrook)){
                whitekingside = false;
            } else if (m.toPoint().equals(whitequeenrook)) {
                whitequeenside = false;
            }
        }
        if (boardBeforeMove.getPieceOn(m.toPoint())==BLACKROOK) {
            if (m.toPoint().equals(blackkingrook)){
                blackkingside = false;
            } else if (m.toPoint().equals(blackqueenrook)) {
                blackqueenside = false;
            }
        }


        // check if move was taken by a king or rook
        switch(boardBeforeMove.getPieceOn(m.fromPoint())) {
            case WHITEKING:
                whitekingside = false;
                whitequeenside = false;
                break;
            case BLACKKING:
                blackkingside = false;
                blackqueenside = false;
                break;
            case WHITEROOK:
                if (m.fromPoint().equals(whitekingrook)){
                    
                    whitekingside = false;
                } else if (m.fromPoint().equals(whitequeenrook)) {
                    
                    whitequeenside = false;
                }
                break;
            case BLACKROOK:
                if (m.fromPoint().equals(blackkingrook)){
                    blackkingside = false;
                } else if (m.fromPoint().equals(blackqueenrook)) {
                    blackqueenside = false;
                }
                break;
            default:
                break;
        }
        this.WhiteKingside = whitekingside;
        this.BlackKingside = blackkingside;
        this.WhiteQueenside = whitequeenside;
        this.BlackQueenside = blackqueenside;
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
        final CastlableImpl other = (CastlableImpl) obj;
        if (this.WhiteQueenside != other.WhiteQueenside) {
            return false;
        }
        if (this.WhiteKingside != other.WhiteKingside) {
            return false;
        }
        if (this.BlackQueenside != other.BlackQueenside) {
            return false;
        }
        if (this.BlackKingside != other.BlackKingside) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.WhiteQueenside ? 1 : 0);
        hash = 29 * hash + (this.WhiteKingside ? 1 : 0);
        hash = 29 * hash + (this.BlackQueenside ? 1 : 0);
        hash = 29 * hash + (this.BlackKingside ? 1 : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("White can queen side castle: ");
        sb.append(WhiteQueenside);
        sb.append("\nWhite can king side castle: ");
        sb.append(WhiteKingside);
        sb.append("\nBlack can queen side castle: ");
        sb.append(BlackQueenside);
        sb.append("\nBlack can king side castle: ");
        sb.append(BlackKingside);
        return sb.toString();
    }
}
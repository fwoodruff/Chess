/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.pieces;

import chessapi.ChessUtil.Colour;
import static chessapi.ChessUtil.Colour.*;
import chessapi.board.UnderAttackUtil;
import static chessapi.pieces.Move.MoveFlavour.*;
import static chessapi.pieces.PieceType.*;
import java.util.LinkedList;
import java.util.List;
import chessapi.board.Position;


/**
 *
 * @author freddiewoodruff
 */
enum King implements Piece  {
    Black(BLACK),White(WHITE);
    final Colour colour;
    King(Colour c) {
        this.colour = c;
    }
    @Override
    public Colour getColour() {
        return colour;
    }
    @Override public String toString() {
        return "King" + colour;
    }
    
    private static final int[][] vectors={{1,0},{0,1},{-1,0},{0,-1},{-1,-1},{-1,1},{1,-1},{1,1}};

    @Override
    public List<Move> getMoves(Position p, Tile tile) {
        List<Move> moveList = new LinkedList<>();
        if (p.whoseTurn()!=colour) { return moveList; }
        for (int[] i : vectors) PieceMoveUtility.jumpMove(i[0], i[1],tile,p,moveList,this.getColour(),king);

        int endRank = this.getColour()==WHITE? 7 : 0;
        
        if (kingsideCastle(p,this.getColour())) { 
            Tile endTile = new Tile(endRank, 6);
            moveList.add(new Move(tile,endTile,kingsideCastle));
        }
        
        if (queensideCastle(p,this.getColour()))  {
            Tile endTile = new Tile(endRank, 2);
            moveList.add(new Move(tile,endTile,queensideCastle));
        }
        
        return moveList;
    }
    
    private static boolean sanityCheck(Position p) {
        if(p.getBlackQueenside()){
            if (p.getPieceOn(0, 0) != BLACKROOK ||
                    p.getPieceOn(0, 4) != BLACKKING) return false; 
        }
        if(p.getWhiteQueenside()){
            if (p.getPieceOn(7, 0) != WHITEROOK ||
                    p.getPieceOn(7, 4) != WHITEKING) return false; 
        }
        if(p.getBlackKingside()){
            if (p.getPieceOn(0, 7) != BLACKROOK ||
                    p.getPieceOn(0, 4) != BLACKKING) return false; 
        }
        if(p.getWhiteKingside()){
            if (p.getPieceOn(7, 7) != WHITEROOK ||
                    p.getPieceOn(7, 4) != WHITEKING) return false; 
        }
        
        
        return true;
    }

    private boolean kingsideCastle(Position p, Colour side) {
        // does not double check whether final square is king check
        
        assert (side == WHITE || side == BLACK): side;
        assert sanityCheck(p):p;
        
        if (!p.getWhiteKingside() && side==WHITE) return false;
        if (!p.getBlackKingside() && side==BLACK) return false;
        
        int endRank = (side==WHITE)? 7 : 0;

        
        if (p.getPieceOn(endRank, 6) != NOPIECE ) return false;
        if (p.getPieceOn(endRank, 5) != NOPIECE ) return false;
        
        if (UnderAttackUtil.isUnderAttack(p, new Tile(endRank,4), side)) return false;
        if (UnderAttackUtil.isUnderAttack(p, new Tile(endRank,5), side)) return false;
        
        return true;
    }
    
    private boolean queensideCastle(Position p, Colour side) {
        assert (side == WHITE || side == BLACK): side;
        assert sanityCheck(p):p;
        
        if (!p.getWhiteQueenside() && side==WHITE) return false;
        if (!p.getBlackQueenside() && side==BLACK) return false;
        
        int endRank = (side==WHITE)? 7 : 0;
        


        
        
        if (p.getPieceOn(endRank, 3) != NOPIECE ) return false;
        if (p.getPieceOn(endRank, 2) != NOPIECE ) return false;
        if (p.getPieceOn(endRank, 1) != NOPIECE ) return false;
        
        if (UnderAttackUtil.isUnderAttack(p, new Tile(endRank,4), side)) return false;
        if (UnderAttackUtil.isUnderAttack(p, new Tile(endRank,3), side)) return false;
        
        return true;
    }
}
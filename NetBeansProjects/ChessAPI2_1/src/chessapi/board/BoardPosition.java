/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.board;


import static chessapi.ChessUtil.*;
import static chessapi.ChessUtil.Colour.*;
import chessapi.game.daemons.EvaluationUtil;
import chessapi.game.daemons.Matilda;
import chessapi.pieces.*;
import static chessapi.pieces.Move.MoveFlavour.pawnDouble;
import static chessapi.pieces.PieceType.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author freddiewoodruff
 */
public final class BoardPosition implements Position {
    // To do: add a builder class

    private final Arrangement pieceList;
    private final Castlable castlable;
    private final int enPassant;
    private final int halfTurnsSinceAdvance;
    private final int gameTurn;
    private final Colour whoseTurn;


    /**
     * Constructs a fresh board.
     */
    public BoardPosition() {

        // board
        pieceList = new BoardArrangement();
        castlable = new CastlableImpl();
        enPassant = NOENPASSANT;
        whoseTurn = WHITE;
        
        // game utils
        halfTurnsSinceAdvance = 0;
        gameTurn = 0;
    }
    
    /**
     * Constructs a board from a previous board and a move.
     * Asserts that the move to be performed is in the list of moves that can be performed.
     * Asserts that the move to be performed does not take a king.
     * @param positionBeforeMove
     * @param move
     */
    public BoardPosition(Position positionBeforeMove, Move move) {
        assert positionBeforeMove.moveList().contains(move);
        assert positionBeforeMove.getPieceOn(move.toPoint())!=BLACKKING;
        assert positionBeforeMove.getPieceOn(move.toPoint())!=WHITEKING;

        if(move.flavour()==pawnDouble) {
            assert move.oldFile()==move.newFile();
            this.enPassant = move.oldFile();
        } else {
            this.enPassant = NOENPASSANT;
        }
        
        this.whoseTurn = not(positionBeforeMove.whoseTurn());
        
        
        this.gameTurn = (positionBeforeMove.whoseTurn()==WHITE)?
                positionBeforeMove.gameTurnNumber():
                positionBeforeMove.gameTurnNumber()+1;
        this.pieceList = new BoardArrangement(positionBeforeMove.getArrangement(),move);
        this.castlable = new CastlableImpl(positionBeforeMove.getCastlable(), positionBeforeMove.getArrangement(),move);
        PieceType typeMoving = positionBeforeMove.getPieceOn(move.fromPoint());
        if (typeMoving == BLACKPAWN || typeMoving== WHITEPAWN ||
            this.getCastlable()!=positionBeforeMove.getCastlable() ||
                positionBeforeMove.getPieceOn(move.toPoint())!=NOPIECE) {
            
            this.halfTurnsSinceAdvance = positionBeforeMove.turnsSinceLastCaptureOrAdvance()+1;
        } else {
            this.halfTurnsSinceAdvance = positionBeforeMove.turnsSinceLastCaptureOrAdvance();
        }
    }

    private volatile List<Move> moveList=null;
    /**
     * Returns the move list for this board
     * including moves that land in check.
     * 
     * @return
     */
    @Override
    public List<Move> moveList() {
        if(moveList==null) {
                moveList = this
                    .listPieces()
                    .stream()
                    .flatMap((piece) -> piece
                            .listMoves(this)
                            .stream())
                    .collect(Collectors.toList());
        }
        return moveList;
    }
    
    /**
     * Returns Matilda's textbook move, otherwise returns Medusa's calculated move.
     * 
     * @param book
     * @return
     */
    @Override
    public List<Move> moveList(Matilda book){
        Move a = book.getBookMove(this);
        if (a==null) {
            return moveList();
        } else{
            List<Move> b = new LinkedList<>();
            b.add(a);
            return b;
        }
    }
    
    private class Heuristic {
        final int heuristic;
        final boolean assigned;
        Heuristic(int heuristic,boolean assigned) {
            this.heuristic=heuristic;
            this.assigned =assigned;
        }
    }
    private volatile Heuristic heuristicCache = new Heuristic(0,false);
    
    @Override
    public int heuristic() {
        if(!heuristicCache.assigned){
            
            
            int heur = EvaluationUtil.heuristic(this);
            heuristicCache = new Heuristic(heur,true);
        }
        return heuristicCache.heuristic;
    }
            
            
            
    private class InCheck {
        final boolean inCheck;
        final boolean assigned;
        InCheck(boolean inCheck,boolean assigned) {
            this.inCheck = inCheck;
            this.assigned = assigned;
        }
    }
    private volatile InCheck whiteIsInCheckCache=new InCheck(false,false);
    private volatile InCheck blackIsInCheckCache=new InCheck(false,false);
    
    /**
     * Determines whether a player is in check.
     * @param player
     * @return
     */
    @Override
    public boolean isInCheck(Colour player) {
        assert (player==WHITE || player==BLACK);
        if(player==WHITE) {
            if(!whiteIsInCheckCache.assigned) {
                Tile kingTile = pieceList.getKingTile(player);
                boolean inCheck = UnderAttackUtil.isUnderAttack(this, kingTile, player);
                whiteIsInCheckCache = new InCheck(inCheck,true);
            }
            return whiteIsInCheckCache.inCheck;
        } else {
            if(!blackIsInCheckCache.assigned) {
                Tile kingTile = pieceList.getKingTile(player);
                boolean inCheck = UnderAttackUtil.isUnderAttack(this, kingTile, player);
                blackIsInCheckCache = new InCheck(inCheck,true);
            }
            return blackIsInCheckCache.inCheck;
        } 
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        
        if (!(obj instanceof Position)) {
            return false;
        }
        final Position other = (Position) obj;
        for(int i=0; i<RANKLENGTH;i++) {
            for(int j=0; j<FILELENGTH;j++) {
                if (other.getPieceOn(i, j) != this.getPieceOn(i, j)) return false;
            }
        }
        if (other.getEnPassantSquare() != this.getEnPassantSquare()) return false;
        if (other.getBlackKingside()!=this.getBlackKingside()) return false;
        if (other.getWhiteKingside()!=this.getWhiteKingside()) return false;
        if (other.getBlackQueenside()!=this.getBlackQueenside()) return false;
        if (other.getWhiteQueenside()!=this.getWhiteQueenside()) return false;
        return true;
    }
    
    /*
    private volatile int hashCode=0;
    @Override public int hashCode() {
        if(this.hashCode==0) {
            int code = 7;
            for (int i=0; i<RANKLENGTH;i++) for (int j=0; j<FILELENGTH;j++) {
                code = 17*code + this.getPieceOn(i, j).hashCode() ;
            }
            code = 17*code + (this.getBlackKingside()? 1:0);
            code = 17*code + (this.getWhiteKingside()? 1:0);
            code = 17*code + (this.getBlackQueenside()? 1:0);
            code = 17*code + (this.getWhiteQueenside()? 1:0);
            code = 17*code + this.getEnPassantSquare();
            hashCode=code;
        }
        return hashCode;
    }*/
    
    private volatile int hashCode=0;
    @Override public int hashCode() {
        if (hashCode==0) {
        
            int code = 7;
            for (int i=0; i<RANKLENGTH;i++) for (int j=0; j<FILELENGTH;j++) {
                code = 17*code + this.getPieceOn(i, j).hashCode() ;
            }
            code = 17*code + (this.getBlackKingside()? 1:0);
            code = 17*code + (this.getWhiteKingside()? 1:0);
            code = 17*code + (this.getBlackQueenside()? 1:0);
            code = 17*code + (this.getWhiteQueenside()? 1:0);
            code = 17*code + this.getEnPassantSquare();
            hashCode = code;
        }

        return hashCode;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.pieceList.toString());
        sb.append("\n");
        sb.append(this.castlable.toString());
        sb.append("\nen passant: ");
        sb.append(this.enPassant);
        return sb.toString();
    }
    
    // trait methods, getters and setters
    @Override
    public Colour whoseTurn() { return whoseTurn; }
    @Override
    public int getEnPassantSquare() { return enPassant;  }
    @Override
    public int gameTurnNumber() { return gameTurn; }
    @Override
    public int turnsSinceLastCaptureOrAdvance() { return halfTurnsSinceAdvance/2; }
    @Override
    public Arrangement getArrangement() { return this.pieceList; }
    @Override
    public List<PlacedPiece> listPieces() { return pieceList.listPieces(); }
    @Override
    public boolean getWhiteQueenside() { return castlable.getWhiteQueenside(); }
    @Override
    public boolean getWhiteKingside() { return castlable.getWhiteKingside(); }
    @Override
    public boolean getBlackQueenside() { return castlable.getBlackQueenside(); }
    @Override
    public boolean getBlackKingside() { return castlable.getBlackKingside(); }
    @Override
    public PieceType getPieceOn(Tile p) { return pieceList.getPieceOn(p); }
    @Override
    public PieceType getPieceOn(int rank, int file) {  return pieceList.getPieceOn(rank, file); }
    @Override
    public Castlable getCastlable() { return this.castlable; }
    /*
    @Override
    public Colour getKingTaken() { return firstKingTaken; }*/
    @Override
    public Tile getKingTile(Colour player) { return pieceList.getKingTile(player); }
}
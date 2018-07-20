/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.game.daemons;


import static chessapi.ChessUtil.Colour.WHITE;
import chessapi.pieces.Move;
import chessapi.board.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


/**
 * Observes game and produces a list of legal moves on request
 * 
 * @author freddiewoodruff
 */
public class Adjudicator {
    private final Map<Position,Integer> seenBoards;
    private final List<Position> history; // so undos can be requested
    private Set<Move> legalMoves;
    
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    
    private Set<Move> getLegalMoves() {        
        //return executor.submit(() -> {
            
            Position current = history.get(history.size() - 1);
            
            Set<Move> newSet = current
                    .listPieces()
                    .stream()
                    .flatMap((piece) -> piece.listMoves(current).stream())
                    .filter(m->legalFilter(m,current))
                    .collect(Collectors.toSet());
            
            if (newSet.isEmpty()) {
                if (current.isInCheck(current.whoseTurn())) {
                    System.out.println("Checkmate");
                    if(current.whoseTurn()==WHITE) {
                        System.out.println("White wins!");
                    } else {
                        System.out.println("Black wins!");
                    }
                } else {
                    System.out.println("Stalemate");
                }
            } else {
            }
            return newSet;
        //}
        //);
    }
    

    
    
    public void shutdown() {
        System.out.println("Adjudicator shutting down...");
        executor.shutdownNow();
        //legalMoves.cancel(true);
        System.out.println("Adjudicator shut down");
    }
    

    public Adjudicator() {
        this.seenBoards = new HashMap<>();
        this.history = new ArrayList<>();
    }

    public void log(Position game) {
        Integer occurrences = seenBoards.get(game);
        if(occurrences==null) {
            seenBoards.put(game, 1);
        } else {
            seenBoards.put(game, occurrences+1);
        }
        history.add(game);
        
        legalMoves = getLegalMoves();
    }
    
    // calculates whether a move produces a legal position (but not whether move was legal to begin with)
    public static boolean legalFilter(Move proposedMove, Position current) {
        BoardPosition temp = new BoardPosition(current,proposedMove);
        return !temp.isInCheck(current.whoseTurn()); // fails if player is in check when its not their turn
    }

    public boolean isMoveLegal(Move attemptedMove){
        
            return legalMoves.contains(attemptedMove);

        //throw new AssertionError();
    }
}




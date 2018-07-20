/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.game.daemons;

import static chessapi.ChessUtil.Colour.*;
import chessapi.board.BoardPosition;

import chessapi.pieces.Move;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import java.util.stream.Collectors;
import static chessapi.game.daemons.Adjudicator.legalFilter;
import chessapi.board.Position;

/**
 *
 * @author freddiewoodruff
 */
public class Medusa {
    private final  Map<Position,List<Future<Move>>> responses;
    
    private final Matilda book;
    
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    
    // must have its own board history
    private Position current;
    

    public Medusa() {
        responses = new HashMap<>();
        book = new Matilda();
    }
    
    
    private List<Position> getLegalBestMoves() {        

        List<Position> positions = current
                .listPieces()
                .stream()
                .flatMap((piece) -> piece.listMoves(current).stream())
                .filter(m->legalFilter(m,current))
                
                .map(move -> new BoardPosition(current,move))
                .collect(Collectors.toList());
        
        //System.out.println(positions);
        positions.forEach((position) -> {
            List<Future<Move>> increasingQualityResponses = new LinkedList<>();
            responses.put(position, increasingQualityResponses);
        });

        for (int depth=0; depth<10; depth++) {
            for (final Position position : positions) {
                
                Future<Move> bestResponse = bestResponseTo(position, depth);

                responses.get(position).add(bestResponse);

            }
        }
        
        
        
        return positions;
        
    }
    
    


    public Move retrieveBestMoveSoFar(Position position) {
        
        
        List<Future<Move>> moves = responses.get(position);
        //System.out.println(moves);
        Move best=null;
        Future<Move> bestMovePresent = moves.get(0);
        
        
        
        
            for(Future<Move> fidelity : moves) {
                if(fidelity.isDone()) {

                    bestMovePresent = fidelity;
                }

            }
        

        try {
            best= bestMovePresent.get();
            
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(Medusa.class.getName()).log(Level.SEVERE, null, ex);
            throw new AssertionError();
        }
        return best;
    }

    public void log(Position game) {
        current = game;
        for(List<Future<Move>> moveList: responses.values()) {
            for (Future<Move> moveFuture : moveList) {
                moveFuture.cancel(true);
            }
        }
        

        getLegalBestMoves();

    }
    
    

    private Future<Move> bestResponseTo(Position position, int depth) {
        
        return executor.submit(() -> {
            Move textBookMove = book.getBookMove(position);
            if(textBookMove!=null) {
                //System.out.println(textBookMove);
                //System.out.println(position);
                return textBookMove;
            }
            
            List<Move> options = position
                    .listPieces()
                    .stream()
                    .flatMap((piece) -> piece.listMoves(position).stream())
                    .filter(m->legalFilter(m,position))
                    .collect(Collectors.toList());
            
            int quality=Integer.MIN_VALUE;
            Move bestMove = null;
            for (Move move : options) {
                
                
                Position temp = new BoardPosition(position,move);
                int score = AlphaBetaUtil.alphaBeta(temp, depth, position.whoseTurn(),book);
                score *= position.whoseTurn()==BLACK? -1:1;
                if(score>quality) {
                    bestMove= move;
                    quality = score;
                }
            }
            
            return bestMove;
        });

    }

    public void shutdown() {
        System.out.println("Shutting down...");
        executor.shutdownNow();
        for(List<Future<Move>> moveList: responses.values()) {
            for (Future<Move> moveFuture : moveList) {
                moveFuture.cancel(true);
            }
        }
        
        System.out.println("Shut down");
        
    }

}

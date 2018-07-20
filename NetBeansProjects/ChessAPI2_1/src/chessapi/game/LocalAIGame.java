/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.game;



import chessapi.game.daemons.Medusa;
import chessapi.game.daemons.Adjudicator;
import chessapi.pieces.Move;
import chessapi.ChessUtil.Colour;
import chessapi.board.*;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.Future;





/**
 *
 * @author freddiewoodruff
 */
public class LocalAIGame implements ChessGame {
    
    
    private Position game;
    private final Adjudicator adjudicator;
    private final Medusa medusa;
    Future<Set<Move>> legalMovesFuture;
    
    /**
     * A local AI game.
     */
    public LocalAIGame() {
        

        this.game = new BoardPosition();
        
        this.adjudicator = new Adjudicator();
        this.medusa = new Medusa();
        
        adjudicator.log(game); // the adjudicator logs the current board state and determines legalMovesFuture.
        medusa.log(game);
        
    }
    
    public LocalAIGame(Position p) {
        this.game = p;
        
        this.adjudicator = new Adjudicator();
        this.medusa = new Medusa();
        
        adjudicator.log(game); // the adjudicator logs the current board state and determines legalMovesFuture.
        medusa.log(game);
    }


    @Override
    public void stopGame() {
        adjudicator.shutdown();
        medusa.shutdown();
        
    }



    @Override
    public EnumSet<State> getState() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }



    @Override
    public MoveReceipt submitUpdate(Move humanMove, Colour whichPerformedMove) {
        Move realMove = Move.addFlavour(humanMove, game);
        
        if (adjudicator.isMoveLegal(humanMove) && game.whoseTurn()==whichPerformedMove) {
                
                game = new BoardPosition(game,realMove);
                
                Move AImove = medusa.retrieveBestMoveSoFar(game);
                
                adjudicator.log(game);
                
                medusa.log(game);
                
                game = new BoardPosition(game,AImove);
                
                adjudicator.log(game);
                medusa.log(game);

                return MoveReceipt.Executed;
                
        }
        
        
        return MoveReceipt.Illegal;
    }



    @Override
    public Position getPosition() {
        return game;
    }

}

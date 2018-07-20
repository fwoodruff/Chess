/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.game;

import chessapi.pieces.Move;
import chessapi.ChessUtil.Colour;

import java.util.EnumSet;
import chessapi.board.Position;

/**
 *
 * @author freddiewoodruff
 */
public interface ChessGame {
    public void stopGame();
    public enum State {
        WhiteTurn,BlackTurn,NoOnesTurn, IllegalState,
        WhiteCanClaimDraw,BlackCanClaimDraw,
        BlackWin,WhiteWin, GameDrawn,
        InsufficientMaterial, ThreefoldRepetition, FiftyTurnsRule,
        WhiteResigns,BlackResigns,
        Stalemate,Checkmate
    }
    public EnumSet<State> getState();
    public Position getPosition();


    
    public enum MoveReceipt { Executed, Illegal }
    
    public MoveReceipt submitUpdate(Move m, Colour whichPerformedMove);
    
    // notify when AI has moved
    
    
}

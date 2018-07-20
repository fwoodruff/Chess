/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.board;

/**
 * Interface that can return whether or not BLACK/WHITE can castle
 * on KINGSIDE/QUEENSIDE.
 * 
 * @author freddiewoodruff
 */
public interface Castlable {

    public boolean getWhiteQueenside();
    public boolean getWhiteKingside();
    public boolean getBlackQueenside();
    public boolean getBlackKingside();

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi;

import static chessapi.ChessUtil.Colour.*;



/**
 *
 * @author freddiewoodruff
 */
public final class ChessUtil {
    private ChessUtil() { throw new AssertionError(); }
    public static final int RANKLENGTH = 8;
    public static final int FILELENGTH = 8;
    public static final int NOENPASSANT = 9;



    public enum Colour { WHITE, BLACK, NOCOLOUR }
    public static Colour not(Colour c) {
        switch(c){
            case WHITE:
                return BLACK;
            case BLACK:
                  return WHITE;
            case NOCOLOUR:
                return NOCOLOUR;
            default:
                throw new AssertionError();
        }
    }
}
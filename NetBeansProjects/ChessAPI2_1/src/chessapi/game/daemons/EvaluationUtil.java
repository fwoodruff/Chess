/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.game.daemons;

import static chessapi.ChessUtil.Colour.*;
import static chessapi.ChessUtil.*;
import static chessapi.pieces.PieceType.*;
import chessapi.board.Position;

/**
 *
 * @author freddiewoodruff
 */
public class EvaluationUtil {

    
    // these values are approximately proportional to the inf,9,5,3,3,1 humans sometimes use
    final static int KINGVALUE =250000;
    private final static int ENDGAMETURN = 60;
    private final static int PAWNMIDGAME = 178;
    private final static int PAWNENDGAME = 258;
    private final static int ROOKMIDGAME = 1270;
    private final static int ROOKENDGAME = 1281;
    private final static int BISHOPMIDGAME = 836;
    private final static int BISHOPENDGAME = 857;
    private final static int KNIGHTMIDGAME = 817;
    private final static int KNIGHTENDGAME = 846;
    private final static int QUEENMIDGAME = 2521;
    private final static int QUEENENDGAME = 2558;
        
    private final static int KNIGHTOUTPOSTED = 80;
    private final static int PAWNDOUBLED = -59;
    private final static int PAWNPAIRED = 33;
    private final static int PAWNADVANCED = 59;
    private final static int QUEENADVANCED = -160;
    private final static int KINGADVANCED = -53;
    private final static int KINGCORNERED = 31;
    private final static int BISHOPDEVELOPED = 40;
    private final static int MOBILITY =28;
    

    private EvaluationUtil(){throw new AssertionError();}
    
    public static int mobility(Position b, int siblings) {
        if (b.whoseTurn()==BLACK) {
            return siblings*MOBILITY;
        } else {
            return -siblings*MOBILITY;
        }
    }
    
    public static int heuristic(Position b){
        
        //if(b.getKingTaken()==BLACK) return KINGVALUE;
        //if(b.getKingTaken()==WHITE) return -KINGVALUE;
        
        int score=0;
        
        
        boolean midGame = (b.gameTurnNumber()<ENDGAMETURN);
        for (int i = 0; i < RANKLENGTH; i++) {
            for (int j = 0; j < FILELENGTH; j++) {
                switch (b.getPieceOn(i, j)){
                    case BLACKPAWN:
                        if(j>0&&j<7){score-=i*PAWNADVANCED;}
                            score-=PAWNMIDGAME;
                            if(b.getPieceOn(i-1,j)==BLACKPAWN){
                                score-=PAWNDOUBLED;
                            }
                            if(j<7){
                                if(b.getPieceOn(i,j+1)==BLACKPAWN){
                                    score-=PAWNPAIRED;
                                }
                            }
                        else score-=PAWNENDGAME;
                        break;
                    case WHITEPAWN:
                        if(j>0&&j<7){score+=(7-i)*PAWNADVANCED;}
                        score+=PAWNMIDGAME;
                        if(b.getPieceOn(i+1,j)==WHITEPAWN){
                            score+=PAWNDOUBLED;
                        }
                        if(j<7){
                            if(b.getPieceOn(i,j+1)==WHITEPAWN){
                                score+=PAWNPAIRED;
                            }
                        }
                        else score+=PAWNENDGAME;
                        break;
                    case BLACKROOK:
                        if (midGame) score-=ROOKMIDGAME;
                        else score-=ROOKENDGAME;
                        break;
                    case WHITEROOK:
                        if (midGame) score+=ROOKMIDGAME;
                        else score+=ROOKENDGAME;
                        break;
                    case BLACKBISHOP:
                        if (midGame){
                            score-=BISHOPMIDGAME;
                            if(i>0){
                                score-=BISHOPDEVELOPED;
                            }
                        }
                        else score-=BISHOPENDGAME;
                        break;
                    case BLACKKNIGHT:
                        if (midGame) {
                            score-=KNIGHTMIDGAME;
                            if (i>2 && i<6 && j>1 && j<6){
                                if(b.getPieceOn(i-1, j+1)==BLACKPAWN||b.getPieceOn(i-1,j-1)==BLACKPAWN){
                                    score-=KNIGHTOUTPOSTED;
                                }
                            }
                        }
                        else score-=KNIGHTENDGAME;
                        
                        
                        break;
                    case WHITEBISHOP:
                        if (midGame){
                            score+=BISHOPMIDGAME;
                            if(i<7){
                                score+=BISHOPDEVELOPED;
                            }
                        }
                        else score+=BISHOPENDGAME;
                        break;
                    case WHITEKNIGHT:
                        if (midGame) {
                            score+=KNIGHTMIDGAME;
                            if (i>1 && i<5 && j>1 && j<6){
                                if(b.getPieceOn(i+1, j+1)==WHITEPAWN||b.getPieceOn(i+1, j-1)==WHITEPAWN){
                                    score+=KNIGHTOUTPOSTED;
                                }
                            }
                        }
                        else score+=KNIGHTENDGAME;
                    case BLACKQUEEN:
                        if (midGame){
                            score-=QUEENMIDGAME;
                            score-=(i)*QUEENADVANCED;
                        }
                        else score-=QUEENENDGAME;
                        break;
                    case WHITEQUEEN:
                        if (midGame){
                            score+=QUEENMIDGAME;
                            score+=(7-i)*QUEENADVANCED;
                        }
                        else score+=QUEENENDGAME;
                        break;
                    case WHITEKING:
                        score+=(7-i)*KINGADVANCED;
                        score+=KINGCORNERED*Math.abs(3-j);
                        break;
                    case BLACKKING:
                        score-=(i)*KINGADVANCED;
                        score-=KINGCORNERED*Math.abs(3-j);
                        break;
                    case NOPIECE:
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        return score;
    }
}
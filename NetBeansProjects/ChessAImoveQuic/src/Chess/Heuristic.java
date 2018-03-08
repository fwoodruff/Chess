
package Chess;

import static Chess.Window.SQX;



public class Heuristic {
    /*
    Essentially part of the Board class but separated for clarity
    This tells us heuristically how strong a board state is.
    */
    static int KingValue =250000;
    
    public static void evaluate(Board b, TreeNode N){
        /*
        Evaluates the board's strength and saves it to the board's evaluation
        not the node evaluation. Note that, as a result of the minimax search,
        a node's evaluation can be different from a board evaluation when a 
        node has evaluated children.
        */
        int score=0;
        boolean thereIsWhiteKing = false;
        boolean thereIsBlackKing = false;
        boolean midGame = (b.turnNumber<15);

        // these values are approximately proportional to the inf,9,5,3,3,1 humans sometimes use
        int PAWNmid = 158;
        int PAWNend = 258;
        int ROOKmid = 1270;
        int ROOKend = 1281;
        int BISHmid = 836;
        int BISHend = 857;
        int KNGTmid = 817;
        int KNGTend = 846;
        int QUEENmid = 2521;
        int QUEENend = 2558;
        
        int KNGToutpost = 120;
        int PAWNdouble = -59;
        int PAWNpaired = 43;
        int PAWNforward = 43;
        int QUEENforward = -91;
        int KINGforward = -53;
        int KINGcornered = 31;
        int mobility =28;
        
        for (int i = 0; i < SQX; i++) {
            for (int j = 0; j < SQX; j++) {
                switch (b.squares[i][j]){
                    case 'p':
                        if (midGame) {
                            if(j>0&&j<7){score-=i*PAWNforward;}
                            score-=PAWNmid;
                            if(b.squares[i-1][j]=='p'){
                                score+=PAWNdouble;
                            }
                            if(j<7){
                                if(b.squares[i][j+1]=='p'){
                                    score-=PAWNpaired;
                                }
                            }
                        }
                        else score-=PAWNend;
                        break;
                    case 'P':
                        if (midGame) {
                            if(j>0&&j<7){score+=(7-i)*PAWNforward;}
                            score+=PAWNmid;
                            if(b.squares[i+1][j]=='p'){
                                score+=PAWNdouble;
                            }
                            if(j<7){
                                if(b.squares[i][j+1]=='p'){
                                    score+=PAWNdouble;
                                }
                            }
                        }
                        else score-=PAWNend;
                        break;
                    case 'r':
                        if (midGame) score-=ROOKmid;
                        else score-=ROOKend;
                        break;
                    case 'R':
                        if (midGame) score+=ROOKmid;
                        else score+=ROOKend;
                        break;
                    case 'b':
                        if (midGame) score-=BISHmid;
                        else score-=BISHend;
                        break;
                    case 'n':
                        if (midGame) {
                            score-=KNGTmid;
                            if (i>1 && i<6 && j>1 && j<6){
                                if(b.squares[i-1][j+1]=='p'||b.squares[i-1][j-1]=='p'){
                                    score-=KNGToutpost;
                                }
                            }
                        }
                        else score-=KNGTend;
                        
                        
                        break;
                    case 'B':
                        if (midGame) score+=BISHmid;
                        else score+=BISHend;
                        break;
                    case 'N':
                        if (midGame) {
                            score+=KNGTmid;
                            if (i>1 && i<6 && j>1 && j<6){
                                if(b.squares[i+1][j+1]=='P'||b.squares[i+1][j-1]=='P'){
                                    score+=KNGToutpost;
                                }
                            }
                        }
                        else score+=KNGTend;
                    case 'q':
                        if (midGame){
                            score-=QUEENmid;
                            score-=(i)*QUEENforward;
                        }
                        else score-=QUEENend;
                        break;
                    case 'Q':
                        if (midGame){
                            score+=QUEENmid;
                            score+=(7-i)*QUEENforward;
                        }
                        else score+=QUEENend;
                        break;
                    case 'K':
                        score+=(7-i)*KINGforward;
                        score+=KINGcornered*Math.abs(3-j);
                        thereIsWhiteKing=true;
                        break;
                    case 'k':
                        score-=(i)*KINGforward;
                        score-=KINGcornered*Math.abs(3-j);
                        thereIsBlackKing=true;
                        break;
                    case ' ':
                        break;
                }
            }
        }
        
        if (!thereIsWhiteKing && !thereIsBlackKing) {
            // if both kings are missing, we care which went missing first
            N.parent.evaluate();
        }
        
        
        if (!thereIsWhiteKing) {
            b.evaluation = -KingValue;
            return;
        }
        if (!thereIsBlackKing) {
            b.evaluation = +KingValue;
            return;
        }
        if (!b.turn) {
            score+=N.countSiblings()*mobility;
            if(N.parent!=null){
                score-=N.parent.countSiblings()*mobility;
            }
        } else {
            score-=N.countSiblings()*mobility;
            if(N.parent!=null){
                score+=N.parent.countSiblings()*mobility;
            }
        }
        b.evaluation = score;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.game.daemons;

import chessapi.ChessUtil;
import static chessapi.ChessUtil.Colour.*;
import static chessapi.ChessUtil.RANKLENGTH;
import chessapi.pieces.*;
import static chessapi.pieces.PieceType.*;
import java.util.*;
import chessapi.board.Position;

/**
 *
 * @author freddiewoodruff
 */
public class OpeningBookUtil {
    
    private OpeningBookUtil(){throw new AssertionError();}
        
    public static Move AlgebraicToMove(String Algebraic, Position b){
        int endRow = b.whoseTurn()==WHITE? 7:0;
        if ("O-O".equals(Algebraic)){
            return new Move(new Tile(endRow,4),new Tile(endRow,6),Move.MoveFlavour.kingsideCastle);
        }
        if ("O-O-O".equals(Algebraic)){
            return new Move(new Tile(endRow,4),new Tile(endRow,2),Move.MoveFlavour.queensideCastle);
        }
        int colfrom = 9;
        int rowfrom = 9;
        int colto = 9;
        int rowto = 9;

        for (int i = Algebraic.length()-1; i >= 0; i--){
            char ch = Algebraic.charAt(i);        
            int a = getColumn(ch);
            if (a!=9){
                if(colto==9){
                    colto = a;
                    continue;
                }
                colfrom = a;
            }
            a = getRow(ch);
            if (a!=9){
                if(rowto==9){
                    rowto = a;
                    continue;
                }
                rowfrom = a;
            }
        }
        List<Move> ML = b.moveList();
        List<Move> candidates = new ArrayList();
        for (Move m : ML) {
            if (m.newRank()==colto && m.newFile()==rowto){
                // have a candidate move
                candidates.add(m);
            }
        }
        
        if(candidates.size()==1){
            return candidates.get(0);
        }
        Iterator itr = candidates.iterator();
        
        while (itr.hasNext()) {
            Move cand = (Move) itr.next();
            boolean isInRow = false;
            boolean isInCol = false;
            if(rowfrom==9){
                for (int i = 0; i < RANKLENGTH; i++) {
                    if (b.getPieceOn(cand.oldRank(),i)==OpeningBookUtil.getPiece(Algebraic,b.whoseTurn())){
                        isInRow =true;
                    }
                }
                if(!isInRow){
                    itr.remove();
                    continue;
                }
            }
            if(colfrom==9){
                for (int i = 0; i < RANKLENGTH; i++) {
                    if (b.getPieceOn(i, cand.oldFile())==OpeningBookUtil.getPiece(Algebraic,b.whoseTurn())){
                        isInCol =true;
                    }
                }
                if(!isInCol){
                    itr.remove();
                    continue;
                }
            }
            PieceType movePiece = b.getPieceOn(cand.fromPoint());
            PieceType algePiece = getPiece(Algebraic,b.whoseTurn());
            if(movePiece!=algePiece){
                itr.remove();
            }
        }
        if(candidates.size()==1){
            return candidates.get(0);
        }
        
        Iterator itr1 = candidates.iterator();
        
        while (itr1.hasNext()) {
            Move cand = (Move) itr1.next();
            if(cand.oldRank()!=colfrom && cand.oldFile()!=rowfrom){
                itr1.remove();
            }
        }
        if(candidates.size()==1){
            return candidates.get(0);
        }
        
        
        
        throw new AssertionError();
    }
    
    private static int getRow(char c){
        switch (c){
            case 'a':
                return 0;
            case 'b':
                return 1;
            case 'c':
                return 2;
            case 'd':
                return 3;
            case 'e':
                return 4;
            case 'f':
                return 5;
            case 'g':
                return 6;
            case 'h':
                return 7;
            default:
                return 9;
        }   
    }
    
    private static int getColumn(char c){
        switch (c){
            case '1':
                return 7;
            case '2':
                return 6;
            case '3':
                return 5;
            case '4':
                return 4;
            case '5':
                return 3;
            case '6':
                return 2;
            case '7':
                return 1;
            case '8':
                return 0;
            default:
                return 9;
        }   
    }
    
    private static PieceType charToPiece(char c) {
        switch(c){
            case 'B':
                return WHITEBISHOP;
            case 'N':
                return WHITEKNIGHT;
            case 'P':
                return WHITEPAWN;
            case 'K':
                return WHITEKING;
            case 'Q':
                return WHITEQUEEN;
            case 'R':
                return WHITEROOK;
            case 'b':
                return BLACKBISHOP;
            case 'n':
                return BLACKKNIGHT;
            case 'p':
                return BLACKPAWN;
            case 'k':
                return BLACKKING;
            case 'q':
                return BLACKQUEEN;
            case 'r':
                return BLACKROOK;
            default:
                return NOPIECE;
        }
    }

    private static PieceType getPiece(String s,ChessUtil.Colour turn){
        char c = s.charAt(0);
        switch(c){
            case 'B':
            case 'N':
            case 'K':
            case 'P':   
            case 'R':
            case 'Q':
                if(turn==WHITE){
                    return charToPiece(c);
                } else{
                    return charToPiece(Character.toLowerCase(c));
                }
                
            default:
                if(turn==WHITE){
                    return WHITEPAWN;
                } else{
                    return BLACKPAWN;
                }
        }
    }
}
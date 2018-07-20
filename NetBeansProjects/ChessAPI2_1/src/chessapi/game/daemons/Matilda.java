/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.game.daemons;

import chessapi.board.BoardPosition;
import chessapi.pieces.Move;
import java.io.*;
import java.net.URL;
import java.util.*;
import chessapi.board.Position;

/**
 * Matilda reads lots of books.
 * @author freddiewoodruff
 */
public final class Matilda {
    HashMap<Position,List<Move>> HM;

    public Matilda() {
        this.HM = new HashMap<>();
        documentToOpeningBook("OpenBook.txt");
    }

    private void addBookMove(Position b, Move move) {
        Object c = HM.get(b);
        if (c == null){
            List<Move> l = new ArrayList();
            l.add(move);
            HM.put(b, l);
        } else {
            List<Move> l = (List<Move>) c;
            l.add(move);
            HM.put(b, l);
        }
    }
    
    public final Move getBookMove(Position b){
        Object O = HM.get(b);
        if (O==null){
            return null;
        } else {
            List<Move> movelist = (List<Move>) O;
            int random = (int )(Math.random() * movelist.size());
            Move item = movelist.get(random);
            return item;
        }
    }
    
    private void openingMoveSequenceToOpeningBookEntries(List<String> algebraicMoves){
        /*
        Reads a list of algebraic opening moves as an opening sequence
        */
        
        
        
        Position b = new BoardPosition();
        
        
        for(String algebraicMove: algebraicMoves){
            Move m = OpeningBookUtil.AlgebraicToMove(algebraicMove, b);
            Move mFlav = Move.addFlavour(m, b);
            addBookMove(b, m);

            b = new BoardPosition(b,m);
            
        }
    }
    
    private void textToBookEntry(String text){
        List<String> algebraicMoves = textToArray(text);
        openingMoveSequenceToOpeningBookEntries(algebraicMoves);
    }
    
    private void documentToOpeningBook(String filename){
        URL openBookUrl = Matilda.class.getResource(filename);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(openBookUrl.openStream()));
            String text;
            while ((text = reader.readLine()) != null) {
                if(!"".equals(text)){
                    if(text.charAt(0)=='1' &&text.charAt(1)=='.'){
                        textToBookEntry(text);
                    }
                }
                
            }
        } catch (FileNotFoundException e) {
            System.err.println("filenotfound");
        } catch (IOException e) {
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {}
        }

    }
    
    private List<String> textToArray(String text){
        List<String> Mls = new ArrayList<>();
        scanInstruction last = scanInstruction.noInstruction;
        String moveString="";
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch(ismove(c)){
                case readMove:
                    moveString+=c;
                    last = scanInstruction.readMove;
                    break;
                case endMove:
                    Mls.add(moveString);
                    moveString="";
                    last = scanInstruction.endMove;
                    break;
                case noInstruction:
                    if(last==scanInstruction.readMove){
                        moveString+=c;
                    }
                    break;
                case endLine:
                    Mls.add(moveString);
                    return Mls;
                    
            }
        }
        return Mls;
    }

    private enum scanInstruction{readMove,endMove,endLine,noInstruction};
    private static scanInstruction ismove(char c){
        switch (c){
            case 'a':
            case 'b':
            case 'c': 
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'K':
            case 'Q':
            case 'R':
            case 'B':
            case 'N':
            case 'P':
            case 'O':
                return scanInstruction.readMove;
            case ' ':
                return scanInstruction.endMove;
            case ')':
                return scanInstruction.endLine;
            default:
                return scanInstruction.noInstruction;
        }
    }
}

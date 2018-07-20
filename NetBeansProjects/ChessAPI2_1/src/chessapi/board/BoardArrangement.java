/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.board;

import static chessapi.ChessUtil.*;
import static chessapi.ChessUtil.Colour.*;
import chessapi.pieces.Move;
import static chessapi.pieces.Move.MoveFlavour.*;
import chessapi.pieces.Tile;
import chessapi.pieces.PieceType;
import static chessapi.pieces.PieceType.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author freddiewoodruff
 */
public class BoardArrangement implements Arrangement {
    
    private final Tile whiteKingTile;
    private final Tile blackKingTile;
    private final List<List<PieceType>> board;
    
    static final PieceType[][] startBoard = {
        {BLACKROOK,BLACKKNIGHT,BLACKBISHOP,BLACKQUEEN,BLACKKING,BLACKBISHOP,BLACKKNIGHT,BLACKROOK},
        {BLACKPAWN,  BLACKPAWN,  BLACKPAWN,BLACKPAWN, BLACKPAWN,  BLACKPAWN,  BLACKPAWN,BLACKPAWN},
        {  NOPIECE,    NOPIECE,    NOPIECE,  NOPIECE,   NOPIECE,    NOPIECE,    NOPIECE,  NOPIECE},
        {  NOPIECE,    NOPIECE,    NOPIECE,  NOPIECE,   NOPIECE,    NOPIECE,    NOPIECE,  NOPIECE},
        {  NOPIECE,    NOPIECE,    NOPIECE,  NOPIECE,   NOPIECE,    NOPIECE,    NOPIECE,  NOPIECE},
        {  NOPIECE,    NOPIECE,    NOPIECE,  NOPIECE,   NOPIECE,    NOPIECE,    NOPIECE,  NOPIECE},
        {WHITEPAWN,  WHITEPAWN,  WHITEPAWN,WHITEPAWN, WHITEPAWN,  WHITEPAWN,  WHITEPAWN,WHITEPAWN},
        {WHITEROOK,WHITEKNIGHT,WHITEBISHOP,WHITEQUEEN,WHITEKING,WHITEBISHOP,WHITEKNIGHT,WHITEROOK},
    };

    public BoardArrangement() {
            whiteKingTile = new Tile(7,4);
            blackKingTile = new Tile(0,4);
        
        board = new ArrayList<>();
        
        for (int i = 0; i < RANKLENGTH; i++) {
            List<PieceType> row = new ArrayList<>();
            board.add(row);
            for (int j = 0; j < FILELENGTH; j++) {
                row.add(startBoard[i][j]);
            }
        }
    }

    @Override
    public List<PlacedPiece> listPieces() {
        List<PlacedPiece> pieceList = new ArrayList<>();
        for (int i=0; i<RANKLENGTH;i++) {
            for (int j=0; j<FILELENGTH;j++) {
                Tile tile = new Tile(i,j);
                PieceType type = this.getPieceOn(tile);
                
                if (type!=NOPIECE) {
                    pieceList.add(new PlacedPiece(type,tile));
                }
            }
        }
        return pieceList;
    }

    public BoardArrangement(Arrangement b, Move m) {
        List<List<PieceType>> pieces = new ArrayList<>();
        
        for (int i = 0; i < RANKLENGTH; i++) {
            List<PieceType> row = new ArrayList<>();
            pieces.add(row);
            for (int j = 0; j < FILELENGTH; j++) {
                row.add(b.getPieceOn(i, j));
            }
        }
        PieceType pieceMoving = b.getPieceOn(m.fromPoint());
        assert (pieceMoving.getColour()== WHITE || pieceMoving.getColour() == BLACK) : this;
        
        pieces.get(m.oldRank()).set(m.oldFile(),NOPIECE);
        pieces.get(m.newRank()).set(m.newFile(),pieceMoving);

        if(pieceMoving==WHITEKING) {
            whiteKingTile = m.toPoint();
            if(m.flavour()==kingsideCastle) {
                pieces.get(m.oldRank()).set(7,NOPIECE);
                pieces.get(m.oldRank()).set(5,WHITEROOK);
            }
            if(m.flavour()==queensideCastle) {
                pieces.get(m.oldRank()).set(0,NOPIECE);
                pieces.get(m.oldRank()).set(3,WHITEROOK);
            }
        } else {
            whiteKingTile = b.getKingTile(WHITE);
        }
        if(pieceMoving==BLACKKING) {
            blackKingTile = m.toPoint();
            if(m.flavour()==kingsideCastle) {
                pieces.get(m.oldRank()).set(7,NOPIECE);
                pieces.get(m.oldRank()).set(5,BLACKROOK);
            }
            if(m.flavour()==queensideCastle) {
                pieces.get(m.oldRank()).set(0,NOPIECE);
                pieces.get(m.oldRank()).set(3,BLACKROOK);
            }
        } else {
            blackKingTile = b.getKingTile(BLACK);
        }        
        board = pieces;
    }

    @Override
    public PieceType getPieceOn(Tile p) {
        return board.get(p.getRank()).get(p.getFile());
    }
    @Override
    public PieceType getPieceOn(int rank, int file) {
        
        return board.get(rank).get(file);
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (List<PieceType> row: board) {
            sb.append("\n+-+-+-+-+-+-+-+-+\n");
            sb.append("|");
            for (PieceType t: row) {
                sb.append(t);
                sb.append("|");
            }
        }
        sb.append("\n+-+-+-+-+-+-+-+-+\n");
        return sb.toString();
    }

    @Override
    public Tile getKingTile(Colour player) {
        assert (player==BLACK || player == WHITE);
        return player==WHITE? whiteKingTile :  blackKingTile;
    }
}

package Chess;

public class Move {
    /*
    Describes a move in terms of start position, end position and a flag.
    */
    
    public int xFrom; // could use short
    public int xTo;
    public int yFrom;
    public int yTo;
    
    public enum moveType{ordinary,
                        pawnDouble,
                        enPassantMT,
                        blackKingsideCastling,
                        blackQueensideCastling,
                        whiteKingsideCastling,
                        whiteQueensideCastling,
                        whiteQueenPromote,
                        whiteRookPromote,
                        whiteBishopPromote,
                        whiteKnightPromote,
                        blackQueenPromote,
                        blackRookPromote,
                        blackBishopPromote,
                        blackKnightPromote,
                        blackKing, //castling consequences
                        blackKingsideRookFirstMove,
                        blackQueensideRookFirstMove,
                        whiteKingsideRookFirstMove,
                        whiteQueensideRookFirstMove,
                        whiteKing
    };
    
    public moveType movType;
    
    public Move(int xFrom, int yFrom,int xTo, int yTo){
        /*
        Creates an ordinary new move.
        */
        this.xFrom = xFrom;
        this.xTo = xTo;
        this.yFrom = yFrom;
        this.yTo = yTo;
        this.movType = moveType.ordinary; // ordinary
    }
    public Move(int xFrom, int yFrom,int xTo, int yTo, moveType fmoveType){
        /*
        Creates a special new move, such as castling or promotion etc.
        */
        this.xFrom = xFrom;
        this.xTo = xTo;
        this.yFrom = yFrom;
        this.yTo = yTo;
        this.movType = fmoveType;
    }
    
    public boolean isSameAs(Move comp){
        /*
        Compares two moves excluding their flags.   
        */     
        return this.xFrom==comp.xFrom &&
               this.xTo==comp.xTo &&
               this.yFrom==comp.yFrom &&
               this.yTo==comp.yTo;
    }
}



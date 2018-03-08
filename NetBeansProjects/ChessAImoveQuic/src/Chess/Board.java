
package Chess;

import Chess.Move.moveType;
import static Chess.TreeNode.NULL;
import static Chess.Window.SQX;

public class Board {
    /*
    Describes a board in full detail, including its heuristic strength and
    available moves.
    Functions for black and white moves are written separately.
    I find this clearer to read since many if(B/W) statements are removed.
    */
    private enum colour{black,white,none};
    char[][] squares;
    boolean turn; //true is white
    // castling and en passant
    boolean WqueensideCastle; // can white castle on the queenside
    boolean BqueensideCastle;
    boolean WkingsideCastle;
    boolean BkingsideCastle;
    int enPassant; // 9=none. 0-7 mean pawn on a-h just moved two
    //int ruleOfFifty; // if 50 turns without a pawn move or a take, call a draw.
    //int ruleOfThree; // if 3 turns identical repeats of a board, draw.
    int evaluation; // evaluates this board in isolation including its mobility
    int turnNumber;
    Move moveToHere; // the move that got us here
    
    public Board() {
        /*
        Initialises a new board.
        */
        squares = new char[][] {{'r','n','b','q','k','b','n','r'},
                                {'p','p','p','p','p','p','p','p'}, // ^y
                                {'o','o','o','o','o','o','o','o'}, // |
                                {'o','o','o','o','o','o','o','o'}, // +-->x
                                {'o','o','o','o','o','o','o','o'},
                                {'o','o','o','o','o','o','o','o'},
                                {'P','P','P','P','P','P','P','P'},
                                {'R','N','B','Q','K','B','N','R'}};
        turn=true;
        WqueensideCastle = true;
        BqueensideCastle = true;
        WkingsideCastle = true;
        BkingsideCastle = true;
        enPassant = 9;
        //ruleOfFifty=0;
        evaluation = NULL;
        turnNumber = 0;
        moveToHere = null;
    }
    
    public void copy(Board oldBoard) {
        /*
        Copies a board
        */
        this.turn=oldBoard.turn;
        this.WqueensideCastle = oldBoard.WqueensideCastle;
        this.BqueensideCastle = oldBoard.BqueensideCastle;
        this.WkingsideCastle = oldBoard.WkingsideCastle;
        this.BkingsideCastle = oldBoard.BkingsideCastle;
        this.enPassant = oldBoard.enPassant;
        //this.ruleOfFifty=oldBoard.ruleOfFifty;
        for (int i = 0; i < SQX; i++) {
            System.arraycopy(oldBoard.squares[i], 0, this.squares[i], 0, SQX);
        }
    }

    public void Print(){
        /*
        Displays a board on the console
        */
        for (int i = 0; i < SQX; i++) {
            for (int j = 0; j < SQX; j++) {
                System.out.print(squares[i][j]);
                System.out.print(",");
            }
            System.out.print("\n");
        }
        System.out.print("\nEvaluation: ");
        System.out.println(evaluation);
        System.out.print("\nBlack can kingside castle: ");
        System.out.print(this.BkingsideCastle);
        System.out.print("\nWhite can kingside castle: ");
        System.out.print(this.WkingsideCastle);
        System.out.print("\nBlack can queenside castle: ");
        System.out.print(this.BqueensideCastle);
        System.out.print("\nWhite can queenside castle: ");
        System.out.println(this.WqueensideCastle);
    }
    
    private void checkIfMoveHasTakenUnmovedRook(Move m){
        /*
        If a rook is taken, it can no longer be used to castle.
        */
        if (squares[m.xTo][m.yTo] == 'r' && m.xTo==0){
            switch (m.yTo){
                case 0:
                    BqueensideCastle = false;
                    break;
                case 7:
                    BkingsideCastle = false;
                    break;
            }
        }
        if (squares[m.xTo][m.yTo] == 'R' && m.xTo==7){
            switch (m.yTo){
                case 0:
                    WqueensideCastle = false;
                    break;
                case 7:
                    WkingsideCastle = false;
                    break;
            }
        }
    }

    public void execute(Move m){
        /*
        Blindly executes a move on a board
        */
        switch (m.movType){
            case ordinary:
                checkIfMoveHasTakenUnmovedRook(m);
                
                squares[m.xTo][m.yTo] = squares[m.xFrom][m.yFrom];
                squares[m.xFrom][m.yFrom] = 'o';
                enPassant=9;
                break;
            case pawnDouble:
                squares[m.xTo][m.yTo] = squares[m.xFrom][m.yFrom];
                squares[m.xFrom][m.yFrom] = 'o';
                enPassant=m.yFrom;
                break;
            case enPassantMT:
                squares[m.xTo][m.yTo] = squares[m.xFrom][m.yFrom];
                squares[m.xFrom][m.yFrom] = 'o';
                squares[m.xFrom][m.yTo] = 'o';
                enPassant=9;
                break;
            case whiteKingsideCastling:
                squares[7][4] = 'o';
                squares[7][5] = 'R';
                squares[7][6] = 'K';
                squares[7][7] = 'o';
                WkingsideCastle = false;
                WqueensideCastle = false;
                enPassant=9;
                break;
            case blackKingsideCastling:
                squares[0][4] = 'o';
                squares[0][5] = 'r';
                squares[0][6] = 'k';
                squares[0][7] = 'o';
                BkingsideCastle = false;
                BqueensideCastle = false;
                enPassant=9;
                break;
            case whiteQueensideCastling:
                squares[7][0] = 'o';
                squares[7][2] = 'K';
                squares[7][3] = 'R';
                squares[7][4] = 'o';
                enPassant=9;
                WkingsideCastle = false;
                WqueensideCastle = false;
                break;
            case blackQueensideCastling:
                squares[0][0] = 'o';
                squares[0][2] = 'k';
                squares[0][3] = 'r';
                squares[0][4] = 'o';
                enPassant=9;
                BkingsideCastle = false;
                BqueensideCastle = false;
                break;
            case blackKingsideRookFirstMove: // or any move from the starting square
                checkIfMoveHasTakenUnmovedRook(m);
                squares[m.xTo][m.yTo] = squares[m.xFrom][m.yFrom];
                squares[0][7] = 'o';
                BkingsideCastle = false;
                enPassant=9;
                break;
            case blackKing:
                checkIfMoveHasTakenUnmovedRook(m);
                squares[m.xTo][m.yTo] = squares[m.xFrom][m.yFrom];
                squares[m.xFrom][m.yFrom] = 'o';
                enPassant=9;
                BkingsideCastle = false;
                BqueensideCastle = false;
                break;
            case whiteKing:
                checkIfMoveHasTakenUnmovedRook(m);
                squares[m.xTo][m.yTo] = squares[m.xFrom][m.yFrom];
                squares[m.xFrom][m.yFrom] = 'o';
                enPassant=9;
                WkingsideCastle = false;
                WqueensideCastle = false;
                break;
            case blackQueensideRookFirstMove:
                checkIfMoveHasTakenUnmovedRook(m);
                squares[m.xTo][m.yTo] = squares[m.xFrom][m.yFrom];
                squares[0][0] = 'o';
                BqueensideCastle = false;
                enPassant=9;
                break;
            case whiteKingsideRookFirstMove: // or any move from the starting square
                checkIfMoveHasTakenUnmovedRook(m);
                squares[m.xTo][m.yTo] = squares[m.xFrom][m.yFrom];
                squares[7][7] = 'o';
                WkingsideCastle = false;
                enPassant=9;
                break;
            case whiteQueensideRookFirstMove:
                checkIfMoveHasTakenUnmovedRook(m);
                squares[m.xTo][m.yTo] = squares[m.xFrom][m.yFrom];
                squares[7][0] = 'o';
                WqueensideCastle = false;
                enPassant=9;
                break;
            case whiteQueenPromote:
                checkIfMoveHasTakenUnmovedRook(m);
                squares[m.xFrom][m.yFrom] = 'o';
                squares[m.xTo][m.yTo] = 'Q';
                enPassant=9;
                break;
            case whiteRookPromote:
                checkIfMoveHasTakenUnmovedRook(m);
                squares[m.xFrom][m.yFrom] = 'o';
                squares[m.xTo][m.yTo] = 'R';
                enPassant=9;
                break;
            case whiteKnightPromote:
                checkIfMoveHasTakenUnmovedRook(m);
                squares[m.xFrom][m.yFrom] = 'o';
                squares[m.xTo][m.yTo] = 'N';
                enPassant=9;
                break;
            case whiteBishopPromote:
                checkIfMoveHasTakenUnmovedRook(m);
                squares[m.xFrom][m.yFrom] = 'o';
                squares[m.xTo][m.yTo] = 'B';
                enPassant=9;
                break;
            case blackQueenPromote:
                checkIfMoveHasTakenUnmovedRook(m);
                squares[m.xFrom][m.yFrom] = 'o';
                squares[m.xTo][m.yTo] = 'q';
                enPassant=9;
                break;
            case blackRookPromote:
                checkIfMoveHasTakenUnmovedRook(m);
                squares[m.xFrom][m.yFrom] = 'o';
                squares[m.xTo][m.yTo] = 'r';
                enPassant=9;
                break;
            case blackKnightPromote:
                checkIfMoveHasTakenUnmovedRook(m);
                squares[m.xFrom][m.yFrom] = 'o';
                squares[m.xTo][m.yTo] = 'n';
                enPassant=9;
                break;
            case blackBishopPromote:
                checkIfMoveHasTakenUnmovedRook(m);
                squares[m.xFrom][m.yFrom] = 'o';
                squares[m.xTo][m.yTo] = 'b';
                enPassant=9;
                break;
        }
        turnNumber++;
        moveToHere=m;
        turn=!turn;
    }

    
    public colour colour(char c){
        /*
        Finds the colour of a square
        */
        switch (c){
            case 'P':
            case 'N':
            case 'B':
            case 'R':
            case 'Q':
            case 'K':
                return colour.white;
            case 'p':
            case 'n':
            case 'b':
            case 'r':
            case 'q':
            case 'k':
                return colour.black;
            default:
                return colour.none;
        }
    }
    
    public void addMove(TreeNode node, Move m){
        /*
        makes a move and sends the new board to a new child of this node. 
        */
        Board temp = new Board();
        temp.copy(this);
        temp.execute(m);
        temp.moveToHere = m;
        node.addChild(temp);
    }
    
    public void findBoards(TreeNode node){
        /*
        Lists all semi-legal moves that can be made. If a move leaves a king in
        check, it will not be picked up at this stage.
        Castling through check is examined rigorously however
        */
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                switch(squares[j][i]){
                    case 'o':
                        break;
                    case 'P':
                        addWhitePawnMoves(node,i,j);
                        break;
                    case 'p':
                        addBlackPawnMoves(node,i,j);
                        break;
                    case 'R':
                        addWhiteRookMoves(node,i,j);
                        break;
                    case 'r':
                        addBlackRookMove(node,i,j);
                        break;
                    case 'N':
                        addWhiteKnightMoves(node,i,j);
                        break;
                    case 'n':
                        addBlackKnightMoves(node,i,j);
                        break;
                    case 'B':
                        addWhiteDiagonalMove(node,i,j);
                        break;
                    case 'b':
                        addBlackDiagonalMove(node,i,j);
                        break;
                    case 'Q':
                        addWhiteQueenMoves(node,i,j);
                        break;
                    case 'q':
                        addBlackQueenMoves(node,i,j);
                        break;
                    case 'K':
                        addWhiteKingMoves(node,i,j);
                        break;
                    case 'k':
                        addBlackKingMoves(node,i,j);
                        break;
                }
            }
        }
    }
    
    private boolean isWhiteUnderAttack(int j, int i){
        /*
        Determines if white is under attack on a specified square. Only used for
        castling through or out of check
        */
        if (j>1) {
            if (squares[j-1][i]=='k') {
                return true;
            }
            if (i>1) {
                switch(squares[j-1][i-1]){
                    case 'p':
                    case 'k':
                        return true;
                    default:
                        break;
                }
            }
            if(i<7){
                switch(squares[j-1][i+1]){
                    case 'p':
                    case 'k':
                        return true;
                    default:
                        break;
                }
            }
        }
        if (i>1) {
            if (squares[j][i-1]=='k') {
                return true;
            }
        }
        if (i<7) {
            if (squares[j][i+1]=='k') {
                return true;
            }
        }
        if (j<7) {
            if (squares[j+1][i]=='k') {
                return true;
            }
            if (i>1) {
                if (squares[j+1][i-1]=='k') {
                    return true;
                }
            }
            if (i<7) {
                if (squares[j+1][i+1]=='k') {
                    return true;
                }
            }
        }
        Up:
        for (int k = j+1; k < 8; k++) {
            switch (squares[k][i]) {
                case 'o':
                    break;
                case 'r':
                case 'q':
                    return true;
                default:
                    break Up;
            }
        }
        Down:
        for (int k = j-1; k >= 0; k--) {
            switch (squares[k][i]) {
                case 'o':
                    break;
                case 'r':
                case 'q':
                    return true;
                default:
                    break Down;
            }
        }
        Left:
        for (int k = i-1; k >= 0; k--) {
            switch (squares[j][k]) {
                case 'o':
                    break;
                case 'r':
                case 'q':
                    return true;
                default:
                    break Left;
            }
        }
        
        Right:
        for (int k = i+1; k < 8; k++) {
            switch (squares[j][k]) {
                case 'o':
                    break;
                case 'r':
                case 'q':
                    return true;
                default:
                    break Right;
            }
        }
        int UL = Math.min(i, j);
        UpLeft:
        for (int k = 0; k < UL; k++) {
            switch (squares[j-k-1][i-k-1]) {
                case 'o':
                    break;
                case 'b':
                case 'q':
                    return true;
                default:
                    break UpLeft;
            }
        }
        int UR = Math.min(7-i, j);
        UpRight:
        for (int k = 0; k < UR; k++) {
            switch (squares[j-k-1][i+k+1]) {
                case 'o':
                    break;
                case 'b':
                case 'q':
                    return true;
                default:
                    break UpRight;
            }
        }
        int DR = Math.min(7-i, 7-j);
        DownRight:
        for (int k = 0; k < DR; k++) {
            switch (squares[j+k+1][i+k+1]) {
                case 'o':
                    break;
                case 'b':
                case 'q':
                    return true;
                default:
                    break DownRight;
            }
        }
        int DL = Math.min(i, 7-j);
        DownLeft:
        for (int k = 0; k < DL; k++) {
            switch (squares[j+k+1][i-k-1]) {
                case 'o':
                    break;
                case 'b':
                case 'q':
                    return true;
                default:
                    break DownLeft;
            }
        }
        if(j<6 && i<7){
            if(squares[j+2][i+1]=='n'){
                return true;
            }
        }
        
        if(j<7 && i<6){
            if(squares[j+1][i+2]=='n'){
                return true;
            }
        }
        
        if(j>1 && i>0){
            if(squares[j-2][i-1]=='n'){
                return true;
            }
        }
        
        if(j>0 && i>1){
            if(squares[j-1][i-2]=='n'){
                return true;
            }
        }
        
        if(j<7 && i>1){
            if(squares[j+1][i-2]=='n'){
                return true;
            }
        }
        
        if(j>1 && i<7){
            if(squares[j-2][i+1]=='n'){
                return true;
            }
        }
        
        if(j<6 && i>0){
            if(squares[j+2][i-1]=='n'){
                return true;
            }
        }
        if(j>0 && i<6){
            if(squares[j-1][i+2]=='n'){
                return true;
            }
        }
        return false;
    }


    private boolean isBlackUnderAttack(int j, int i) {
        /*
        Determines if black is under attack on a specified square. Only used for
        castling through or out of check
        */
        if (j<7) {
            if (squares[j+1][i]=='K') {
                return true;
            }
            if (i>1) {
                switch(squares[j+1][i-1]){
                    case 'P':
                    case 'K':
                        return true;
                    default:
                        break;
                }
                
            }
            if(i<7){
                switch(squares[j+1][i+1]){
                    case 'P':
                    case 'K':
                        return true;
                    default:
                        break;
                }
            }
        }
        if (i>1) {
            if (squares[j][i-1]=='K') {
                return true;
            }
        }
        if (i<7) {
            if (squares[j][i+1]=='K') {
                return true;
            }
        }
        if (j>1) {
            if (squares[j-1][i]=='K') {
                return true;
            }
            if (i>1) {
                if (squares[j-1][i-1]=='K') {
                    return true;
                }
            }
            if (i<7) {
                if (squares[j-1][i+1]=='K') {
                    return true;
                }
            }
        }
        Up:
        for (int k = j+1; k < 8; k++) {
            switch (squares[k][i]) {
                case 'o':
                    break;
                case 'R':
                case 'Q':
                    return true;
                default:
                    break Up;
            }
        }
        
        Down:
        for (int k = j-1; k >= 0; k--) {
            switch (squares[k][i]) {
                case 'o':
                    break;
                case 'R':
                case 'Q':
                    return true;
                default:
                    break Down;
            }
        }
                Left:
        for (int k = i-1; k >= 0; k--) {
            switch (squares[j][k]) {
                case 'o':
                    break;
                case 'R':
                case 'Q':
                    return true;
                default:
                    break Left;
            }
        }
        
        Right:
        for (int k = i+1; k < 8; k++) {
            switch (squares[j][k]) {
                case 'o':
                    break;
                case 'R':
                case 'Q':
                    return true;
                default:
                    break Right;
            }
        }
        
        
        int UL = Math.min(i, j);
        UpLeft:
        for (int k = 0; k < UL; k++) {
            switch (squares[j-k-1][i-k-1]) {
                case 'o':
                    break;
                case 'B':
                case 'Q':
                    return true;
                default:
                    break UpLeft;
            }
        }
        
        int UR = Math.min(7-i, j);
        UpRight:
        for (int k = 0; k < UR; k++) {
            switch (squares[j-k-1][i+k+1]) {
                case 'o':
                    break;
                case 'B':
                case 'Q':
                    return true;
                default:
                    break UpRight;
            }
        }
        
        int DR = Math.min(7-i, 7-j);
        DownRight:
        for (int k = 0; k < DR; k++) {
            switch (squares[j+k+1][i+k+1]) {
                case 'o':
                    break;
                case 'B':
                case 'Q':
                    return true;
                default:
                    break DownRight;
            }
        }
        
        int DL = Math.min(i, 7-j);
        DownLeft:
        for (int k = 0; k < DL; k++) {
            switch (squares[j+k+1][i-k-1]) {
                case 'o':
                    break;
                case 'B':
                case 'Q':
                    return true;
                default:
                    break DownLeft;
            }
        }     
        if(j<6 && i<7){
            if(squares[j+2][i+1]=='N'){
                return true;
            }
        }
        if(j<7 && i<6){
            if(squares[j+1][i+2]=='N'){
                return true;
            }
        }
        if(j>1 && i>0){
            if(squares[j-2][i-1]=='N'){
                return true;
            }
        }
        if(j>0 && i>1){
            if(squares[j-1][i-2]=='N'){
                return true;
            }
        }
        
        if(j<7 && i>1){
            if(squares[j+1][i-2]=='n'){
                return true;
            }
        }
        
        if(j>1 && i<7){
            if(squares[j-2][i+1]=='N'){
                return true;
            }
        }
        
        if(j<6 && i>0){
            if(squares[j+2][i-1]=='N'){
                return true;
            }
        }
        if(j>0 && i<6){
            if(squares[j-1][i+2]=='N'){
                return true;
            }
        }
        
        return false;
    
    }

    public void evaluate(TreeNode NodeWhichCalled){
        /*
        Evaluates the strength of a node. Note that the evaluation heuristic
        is stored in a separate file to make this class a little smaller.
        */
        if (evaluation!=NULL)return;
        Heuristic.evaluate(this,NodeWhichCalled);
    }

    public void addBlackPawnMoves(TreeNode node, int i, int j){
        /*
        Adds black pawn moves to a node's children
        */
        if (!turn){
            if (j<6 && squares[j+1][i]=='o'){
                addMove(node,new Move(j,i,j+1,i, moveType.ordinary));
            }
            if (j==6) {
                if(i<7){
                    if (colour(squares[j-1][i+1])==colour.black) {
                        addMove(node,new Move(j,i,j+1,i+1,moveType.blackBishopPromote));
                        addMove(node,new Move(j,i,j+1,i+1,moveType.blackQueenPromote));
                        addMove(node,new Move(j,i,j+1,i+1,moveType.blackKnightPromote));
                        addMove(node,new Move(j,i,j+1,i+1,moveType.blackRookPromote));
                    }
                }
                if(i>0){
                    if (colour(squares[j-1][i-1])==colour.black){
                        addMove(node,new Move(j,i,j+1,i-1,moveType.blackBishopPromote));
                        addMove(node,new Move(j,i,j+1,i-1,moveType.blackQueenPromote));
                        addMove(node,new Move(j,i,j+1,i-1,moveType.blackKnightPromote));
                        addMove(node,new Move(j,i,j+1,i-1,moveType.blackRookPromote));
                    }
                }
                if(squares[j-1][i]=='o'){
                    addMove(node,new Move(j,i,j+1,i,moveType.blackBishopPromote));
                    addMove(node,new Move(j,i,j+1,i,moveType.blackQueenPromote));
                    addMove(node,new Move(j,i,j+1,i,moveType.blackKnightPromote));
                    addMove(node,new Move(j,i,j+1,i,moveType.blackRookPromote));
                }
            }
            if (j==1 &&squares[j+1][i]=='o' && squares[j+2][i]=='o' ){
                addMove(node,new Move(j,i,j+2,i,moveType.pawnDouble));
            }

            if (j<6 && i<7){
                if(colour(squares[j+1][i+1])==colour.white){
                    addMove(node,new Move(j,i,j+1,i+1));
                }
            }
            if (j<6 && i>0){
                if(colour(squares[j+1][i-1])==colour.white){
                    addMove(node,new Move(j,i,j+1,i-1));
                }
            }
            if (enPassant == i+1 && j==4) {
                addMove(node,new Move(j,i,j+1,i+1,moveType.enPassantMT));
            }
            if (enPassant == i-1 && j==4) {
                addMove(node,new Move(j,i,j+1,i-1,moveType.enPassantMT));
            }
        }
    }
    public void addWhitePawnMoves(TreeNode node, int i, int j){
        /*
        Adds white pawn moves to a node's children
        */
        if (turn){
            if (j>1 && squares[j-1][i]=='o'){addMove(node,new Move(j,i,j-1,i));}
            if (j==6 && squares[j-1][i]=='o' && squares[j-2][i]=='o'){addMove(node,new Move(j,i,j-2,i));}

            if (j==1) {
                if(i<7){
                    if (colour(squares[j-1][i+1])==colour.black) {
                        addMove(node,new Move(j,i,j-1,i+1,moveType.whiteBishopPromote));
                        addMove(node,new Move(j,i,j-1,i+1,moveType.whiteQueenPromote));
                        addMove(node,new Move(j,i,j-1,i+1,moveType.whiteKnightPromote));
                        addMove(node,new Move(j,i,j-1,i+1,moveType.whiteRookPromote));
                    }
                }
                if(i>0){
                    if (colour(squares[j-1][i-1])==colour.black){
                        addMove(node,new Move(j,i,j-1,i-1,moveType.whiteBishopPromote));
                        addMove(node,new Move(j,i,j-1,i-1,moveType.whiteQueenPromote));
                        addMove(node,new Move(j,i,j-1,i-1,moveType.whiteKnightPromote));
                        addMove(node,new Move(j,i,j-1,i-1,moveType.whiteRookPromote));
                    }
                }
                if(squares[j-1][i]=='o'){
                    addMove(node,new Move(j,i,j-1,i,moveType.whiteBishopPromote));
                    addMove(node,new Move(j,i,j-1,i,moveType.whiteQueenPromote));
                    addMove(node,new Move(j,i,j-1,i,moveType.whiteKnightPromote));
                    addMove(node,new Move(j,i,j-1,i,moveType.whiteRookPromote));
                }
            }

            if (j>1) {
                if(i<7){
                    if (colour(squares[j-1][i+1])==colour.black) {
                        addMove(node,new Move(j,i,j-1,i+1));
                    }
                }
                if(i>0){
                    if (colour(squares[j-1][i-1])==colour.black){
                        addMove(node,new Move(j,i,j-1,i-1));
                    }
                }
            }

            if (enPassant == i+1 && j==3) {
                addMove(node,new Move(j,i,j-1,i+1,moveType.enPassantMT));
            }
            if (enPassant == i-1 && j==3) {
                addMove(node,new Move(j,i,j-1,i-1,moveType.enPassantMT));
            }
            
        }
    }
    
    public void addBlackRookMove(TreeNode node, int i, int j){
        /*
        Adds black rook moves to a node's children
        */
        if (i==7 && j==0 && BkingsideCastle) {
            addBlackPlaneMove(node,i,j, moveType.blackKingsideRookFirstMove);
        } else if(i==0 && j==0 && BqueensideCastle) {
            addBlackPlaneMove(node,i,j, moveType.blackQueensideRookFirstMove);
        } else {
            addBlackPlaneMove(node,i,j);
        }
    }
    
    public void addWhiteRookMoves(TreeNode node, int i, int j){
        /*
        Adds white pawn moves to a node's children
        */
        if (i==7 && j==7 && WkingsideCastle) {
            addWhitePlaneMove(node,i,j, moveType.whiteKingsideRookFirstMove);
        } else if(i==0 && j==7 && WqueensideCastle) {
            addWhitePlaneMove(node,i,j, moveType.whiteQueensideRookFirstMove);
        } else {
            addWhitePlaneMove(node,i,j);
        }
    }
    
    public void addBlackQueenMoves(TreeNode node, int i, int j){
        /*
        Adds black queen moves to a node's children
        */
        addBlackPlaneMove(node,i, j);
        addBlackDiagonalMove(node,i, j);
    }
    public void addWhiteQueenMoves(TreeNode node, int i, int j){
        /*
        Adds white queen moves to a node's children
        */
        addWhitePlaneMove(node,i, j);
        addWhiteDiagonalMove(node,i, j);
    }
    public void addBlackPlaneMove(TreeNode node, int i, int j){
        /*
        Adds ordinary black up, down, left right white moves to a node's children for queens
        and rooks.
        */
        addBlackPlaneMove(node,i, j, moveType.ordinary);
    }
    public void addBlackPlaneMove(TreeNode node, int i, int j, moveType MT){
        /*
        Adds black up, down, left, right white moves to a node's children for
        queens and rooks. A moveType flag helps specify that a rook has moved
        forbidding castling if a rook moves back to its castling square.
        */
        if (!turn){
            Down:
            for (int k = 0; k < 7-j; k++) {
                switch (colour(squares[j+k+1][i])) {
                    case none: addMove(node,new Move(j,i,j+k+1,i,MT)); break;
                    case white: addMove(node,new Move(j,i,j+k+1,i,MT)); break Down;
                    default: break Down;
                }
            }
            Up:
            for (int k = 0; k < j; k++) {
                switch (colour(squares[j-k-1][i])) {
                    case none:addMove(node,new Move(j,i,j-k-1,i,MT));break;
                    case white:addMove(node,new Move(j,i,j-k-1,i,MT));break Up;
                    default: break Up;
                }
            }
            Right:
            for (int k = 0; k < 7-i; k++) {
                switch (colour(squares[j][i+k+1])) {
                    case none:addMove(node,new Move(j,i,j,i+k+1,MT));break;
                    case white: addMove(node,new Move(j,i,j,i+k+1,MT));break Right;
                    default:break Right;
                }
            }
            Left:
            for (int k = 0; k < i; k++) {
                switch (colour(squares[j][i-k-1])) {
                    case none:addMove(node,new Move(j,i,j,i-k-1,MT));break;
                    case white:addMove(node,new Move(j,i,j,i-k-1,MT));break Left;
                    default:break Left;
                }
            }
        }
    }
    
    public void addWhitePlaneMove(TreeNode node, int i, int j){
        /*
        Adds ordinary black up, down, left right white moves to a node's children for queens
        and rooks.
        */
        addWhitePlaneMove(node,i,j,moveType.ordinary);
    }
    public void addWhitePlaneMove(TreeNode node, int i, int j, moveType MT){
        /*
        Adds white up, down, left, right white moves to a node's children for
        queens and rooks. A moveType flag helps specify that a rook has moved
        forbidding castling if a rook moves back to its castling square.
        */
        if (turn){
            Down:
            for (int k = 0; k < 7-j; k++) {
                switch (colour(squares[j+k+1][i])) {
                    case none: addMove(node,new Move(j,i,j+k+1,i,MT)); break;
                    case black: addMove(node,new Move(j,i,j+k+1,i,MT)); break Down;
                    default: break Down;
                }
            }
            Up:
            for (int k = 0; k < j; k++) {
                switch (colour(squares[j-k-1][i])) {
                    case none:addMove(node,new Move(j,i,j-k-1,i,MT));break;
                    case black:addMove(node,new Move(j,i,j-k-1,i,MT));break Up;
                    default: break Up;
                }
            }
            Right:
            for (int k = 0; k < 7-i; k++) {
                switch (colour(squares[j][i+k+1])) {
                    case none:addMove(node,new Move(j,i,j,i+k+1,MT));break;
                    case black: addMove(node,new Move(j,i,j,i+k+1,MT));break Right;
                    default:break Right;
                }
            }
            Left:
            for (int k = 0; k < i; k++) {
                switch (colour(squares[j][i-k-1])) {
                    case none:addMove(node,new Move(j,i,j,i-k-1,MT));break;
                    case black:addMove(node,new Move(j,i,j,i-k-1,MT));break Left;
                    default:break Left;
                }
            }
        }
    }
    public void addWhiteDiagonalMove(TreeNode node, int i, int j) {
        /*
        adds diagonal bishop and queen moves
        */
        if (turn){
            int UR = Math.min(j, 7-i);
            int UL = Math.min(j, i);
            int DR = Math.min(7-j, 7-i);
            int DL = Math.min(7-j, i);

            UpRight:
            for (int k = 0; k < UR; k++) {
                switch (colour(squares[j-k-1][i+k+1])) {
                    case none:addMove(node,new Move(j,i,j-k-1,i+k+1));break;
                    case black:addMove(node,new Move(j,i,j-k-1,i+k+1));break UpRight;
                    default:break UpRight;
                }
            }
            DownLeft:
            for (int k = 0; k < DL; k++) {
                colour c = colour(squares[j+k+1][i-k-1]);
                switch (c) {
                    case none:addMove(node,new Move(j,i,j+k+1,i-k-1));break;
                    case black:addMove(node,new Move(j,i,j+k+1,i-k-1));break DownLeft;
                    default:break DownLeft;
                }
            }
            DownRight:
            for (int k = 0; k < DR; k++) {
                switch (colour(squares[j+k+1][i+k+1])) {
                    case none:addMove(node,new Move(j,i,j+k+1,i+k+1));break;
                    case black:addMove(node,new Move(j,i,j+k+1,i+k+1));break DownRight;
                    default:break DownRight;
                }
            }
            UpLeft:
            for (int k = 0; k < UL; k++) {
                colour c = colour(squares[j-k-1][i-k-1]);
                switch (c) {
                    case none:
                        addMove(node,new Move(j,i,j-k-1,i-k-1));
                        break;
                    case black:
                        addMove(node,new Move(j,i,j-k-1,i-k-1));
                        break UpLeft;
                    default:
                        break UpLeft;
                }
            }
        }
    }
    public void addBlackDiagonalMove(TreeNode node, int i, int j) {
        /*
        adds diagonal bishop and queen moves
        */
        if (!turn) {
            int UR = Math.min(j, 7-i);
            int UL = Math.min(j, i);
            int DR = Math.min(7-j, 7-i);
            int DL = Math.min(7-j, i);

            UpRight:
            for (int k = 0; k < UR; k++) {
                switch (colour(squares[j-k-1][i+k+1])) {
                    case none:addMove(node,new Move(j,i,j-k-1,i+k+1));break;
                    case white:addMove(node,new Move(j,i,j-k-1,i+k+1));break UpRight;
                    default:break UpRight;
                }
            }
            DownLeft:
            for (int k = 0; k < DL; k++) {
                colour c = colour(squares[j+k+1][i-k-1]);
                switch (c) {
                    case none:addMove(node,new Move(j,i,j+k+1,i-k-1));break;
                    case white:addMove(node,new Move(j,i,j+k+1,i-k-1));break DownLeft;
                    default:break DownLeft;
                }
            }
            DownRight:
            for (int k = 0; k < DR; k++) {
                switch (colour(squares[j+k+1][i+k+1])) {
                    case none:addMove(node,new Move(j,i,j+k+1,i+k+1));break;
                    case white:addMove(node,new Move(j,i,j+k+1,i+k+1));break DownRight;
                    default:break DownRight;
                }
            }
            UpLeft:
            for (int k = 0; k < UL; k++) {
                colour c = colour(squares[j-k-1][i-k-1]);
                switch (c) {
                    case none:
                        addMove(node,new Move(j,i,j-k-1,i-k-1));
                        break;
                    case white:
                        addMove(node,new Move(j,i,j-k-1,i-k-1));
                        break UpLeft;
                    default:
                        break UpLeft;
                }
            }
        }
    }
    public void addBlackKnightMoves(TreeNode node, int i,int j){
        /*
        adds black knight moves to a node's children
        */
        if (!turn){
            if(i>0 && j>1 && colour(squares[j-2][i-1])!=colour.black){addMove(node,new Move(j,i,j-2,i-1));}
            if(i>1 && j>0 && colour(squares[j-1][i-2])!=colour.black){addMove(node,new Move(j,i,j-1,i-2));}
            if(i<6 && j<7 && colour(squares[j+1][i+2])!=colour.black){addMove(node,new Move(j,i,j+1,i+2));}
            if(i<7 && j<6 && colour(squares[j+2][i+1])!=colour.black){addMove(node,new Move(j,i,j+2,i+1));}
            if(i>0 && j<6 && colour(squares[j+2][i-1])!=colour.black){addMove(node,new Move(j,i,j+2,i-1));}
            if(i>1 && j<7 && colour(squares[j+1][i-2])!=colour.black){addMove(node,new Move(j,i,j+1,i-2));}
            if(i<6 && j>0 && colour(squares[j-1][i+2])!=colour.black){addMove(node,new Move(j,i,j-1,i+2));}
            if(i<7 && j>1 && colour(squares[j-2][i+1])!=colour.black){addMove(node,new Move(j,i,j-2,i+1));}
        }
    }
    public void addWhiteKnightMoves(TreeNode node, int i,int j){
        /*
        adds black knight moves to a node's children
        */
        if (turn){
            if(i>0 && j>1 && colour(squares[j-2][i-1])!=colour.white){addMove(node,new Move(j,i,j-2,i-1));}
            if(i>1 && j>0 && colour(squares[j-1][i-2])!=colour.white){addMove(node,new Move(j,i,j-1,i-2));}
            if(i<6 && j<7 && colour(squares[j+1][i+2])!=colour.white){addMove(node,new Move(j,i,j+1,i+2));}
            if(i<7 && j<6 && colour(squares[j+2][i+1])!=colour.white){addMove(node,new Move(j,i,j+2,i+1));}
            if(i>0 && j<6 && colour(squares[j+2][i-1])!=colour.white){addMove(node,new Move(j,i,j+2,i-1));}
            if(i>1 && j<7 && colour(squares[j+1][i-2])!=colour.white){addMove(node,new Move(j,i,j+1,i-2));}
            if(i<6 && j>0 && colour(squares[j-1][i+2])!=colour.white){addMove(node,new Move(j,i,j-1,i+2));}
            if(i<7 && j>1 && colour(squares[j-2][i+1])!=colour.white){addMove(node,new Move(j,i,j-2,i+1));}
        }
    }
    
    public void addWhiteKingMoves(TreeNode node, int i, int j){
        /*
        adds white king moves to a node's children
        */
        if (turn){
            if(i>0 && colour(squares[j][i-1])!=colour.white){addMove(node,new Move(j,i,j,i-1,moveType.whiteKing));}
            if(i<7 && colour(squares[j][i+1])!=colour.white){addMove(node,new Move(j,i,j,i+1,moveType.whiteKing));}
            if(j>0 && colour(squares[j-1][i])!=colour.white){addMove(node,new Move(j,i,j-1,i,moveType.whiteKing));}
            if(j<7 && colour(squares[j+1][i])!=colour.white){addMove(node,new Move(j,i,j+1,i,moveType.whiteKing));}
            if(i>0 && j>0 && colour(squares[j-1][i-1])!=colour.white){addMove(node,new Move(j,i,j-1,i-1,moveType.whiteKing));}
            if(i>0 && j<7 && colour(squares[j+1][i-1])!=colour.white){addMove(node,new Move(j,i,j+1,i-1,moveType.whiteKing));}
            if(i<7 && j>0 && colour(squares[j-1][i+1])!=colour.white){addMove(node,new Move(j,i,j-1,i+1,moveType.whiteKing));}
            if(i<7 && j<7 && colour(squares[j+1][i+1])!=colour.white){addMove(node,new Move(j,i,j+1,i+1,moveType.whiteKing));}

            if(WkingsideCastle && squares[7][5]=='o' && squares[7][6]=='o') {
                if (!isWhiteUnderAttack(7,5) && !isWhiteUnderAttack(7,4)) {
                    addMove(node,new Move(j,i,j,i+2,moveType.whiteKingsideCastling));
                } else {
                    //System.out.println("white king kingside cannot castle because path is under attack");
                }
               
                
                
            }
            if(WqueensideCastle && squares[7][1]=='o' && squares[7][2]=='o' && squares[7][3]=='o') {
                if (!isWhiteUnderAttack(7,4) && !isWhiteUnderAttack(7,3) && !isWhiteUnderAttack(7,2)) {
                    addMove(node,new Move(j,i,j,i-2,moveType.whiteQueensideCastling));
                } else {
                    //System.out.println("white king cannot queenside castle because path is under attack");
                }
                
                
            } 
        }
    }
    
    public void addBlackKingMoves(TreeNode node,int i, int j){
        /*
        Adds black king moves to a node's children.
        */
        if (!turn){
            if(i>0 && colour(squares[j][i-1])!=colour.black){addMove(node,new Move(j,i,j,i-1,moveType.blackKing));}
            if(i<7 && colour(squares[j][i+1])!=colour.black){addMove(node,new Move(j,i,j,i+1,moveType.blackKing));}
            if(j>0 && colour(squares[j-1][i])!=colour.black){addMove(node,new Move(j,i,j-1,i,moveType.blackKing));}
            if(j<7 && colour(squares[j+1][i])!=colour.black){addMove(node,new Move(j,i,j+1,i,moveType.blackKing));}
            if(i>0 && j>0 && colour(squares[j-1][i-1])!=colour.black){addMove(node,new Move(j,i,j-1,i-1,moveType.blackKing));}
            if(i>0 && j<7 && colour(squares[j+1][i-1])!=colour.black){addMove(node,new Move(j,i,j+1,i-1,moveType.blackKing));}
            if(i<7 && j>0 && colour(squares[j-1][i+1])!=colour.black){addMove(node,new Move(j,i,j-1,i+1,moveType.blackKing));}
            if(i<7 && j<7 && colour(squares[j+1][i+1])!=colour.black){addMove(node,new Move(j,i,j+1,i+1,moveType.blackKing));}

            if(BkingsideCastle && squares[0][5]=='o' && squares[0][6]=='o') {
                if (!isBlackUnderAttack(0,5) && !isBlackUnderAttack(0,4)) {
                    addMove(node,new Move(j,i,j,i+2,moveType.blackKingsideCastling));
                }
                
            }
            if(BqueensideCastle && squares[0][1]=='o' && squares[0][2]=='o' && squares[0][3]=='o') {
                if (!isBlackUnderAttack(0,4) && !isBlackUnderAttack(0,3) && !isBlackUnderAttack(0,2)) {
                } else {
                    addMove(node,new Move(j,i,j,i-2,moveType.blackQueensideCastling));
                }
            }
        }
    }
}
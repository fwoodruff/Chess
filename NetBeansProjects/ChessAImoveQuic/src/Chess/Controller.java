
package Chess;


import static Chess.Window.PIX;
import static Chess.Window.SQX;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    /*
    Contains high level instructions for when the UI is interacted with
    */

    Sprite selectedPiece=null;
    Sprite thisPiece=null;
    Game game;
    
    public static List<Sprite> Pieces = new ArrayList<>();
    
    public Controller() {
        /*
        Starts a game on open
        */
        this.game = new Game();
    }
    
    public void drawBoard(){
        /*
        builds the sprite objects to be displayed on the board
        */
        Pieces.clear();
        for (int i = 0; i < SQX; i++) {
            for (int j = 0; j < SQX; j++) {
                if (game.current.data.squares[j][i]!='o'){
                    new Sprite(i*PIX, j*PIX,game.current.data.squares[j][i]);
                }
            }
        }
    }
    
    public void selectPiece(Point mousePoint){
        /*
        Selects the piece at the location the mouse has been clicked.
        */
        for (int i = 0; i < Pieces.size(); i++) {
            Sprite thisPiece = Pieces.get(i);
            if (thisPiece.contains(mousePoint)) {
                selectedPiece = thisPiece;
                
            }
        }
    }
    
    public void dragPiece(Point fromPoint, Point mousePoint) {
        /*
        Moves the selected piece about in response to mouse being dragged
        */
        if (selectedPiece!=null){
                    selectedPiece.x= selectedPiece.xState+ (int)(-fromPoint.getX()+mousePoint.getX());
                    selectedPiece.y= selectedPiece.yState+ (int)(-fromPoint.getY()+mousePoint.getY());
                }
    }

    public void releasePiece(Point fromPoint, Point mousePoint){
        /*
        Executes move when the piece has been released on a square
        */
        selectedPiece = null;
        Move m = new Move(fromPoint.y/PIX, fromPoint.x/PIX, mousePoint.y/PIX, mousePoint.x/PIX);
        game.MakeMove(m);
        drawBoard();
    }
}

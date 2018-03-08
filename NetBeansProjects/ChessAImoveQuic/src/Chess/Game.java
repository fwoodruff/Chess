
package Chess;

import static Chess.Move.moveType.whiteBishopPromote;
import static Chess.Move.moveType.whiteKnightPromote;
import static Chess.Move.moveType.whiteQueenPromote;
import static Chess.Move.moveType.whiteRookPromote;

public class Game {
    /*
    Controls the players and game rules logic.
    */
    TreeNode GameTree;
    TreeNode current;
    
    
    public Game(){
        /*
        Makes a new game.
        */
        GameTree = new TreeNode();
        current = GameTree;
        Medusa.runMedusa(current);
    }
    
    public void MakeMove(Move m){
        /*
        Determines if a move is legal and makes the move.
        */
        boolean didPopUp = false;
        Popup popup = null;
        for (TreeNode temp : current.children) {
            if(m.isSameAs(temp.data.moveToHere)){
                Move.moveType var = temp.data.moveToHere.movType;
                if (var==whiteQueenPromote|| var==whiteBishopPromote||var==whiteRookPromote||var==whiteKnightPromote) {
                    if (!didPopUp) {
                        popup = new Popup();
                        didPopUp=true;
                    }
                    switch(popup.n){
                        case 0:
                            if(temp.data.moveToHere.movType!=whiteQueenPromote) continue;
                            else break;
                        case 1:
                            if(temp.data.moveToHere.movType!=whiteRookPromote) continue;
                            else break;
                        case 2:
                            if(temp.data.moveToHere.movType!=whiteKnightPromote) continue;
                            else break;
                        case 3:
                            if(temp.data.moveToHere.movType!=whiteBishopPromote) continue;
                            else break;
                            
                    }
                }
                if(temp.children.isEmpty()){
                    current = temp;
                    System.out.println("Game Over");
                    return;
                }
                current.pruneSiblingsOf(temp);
                TreeNode temp1 = temp.children.getFirst();
                temp.pruneSiblingsOf(temp1);
                current = temp1;
                Medusa.runMedusa(current);
            }
        }
    }
}
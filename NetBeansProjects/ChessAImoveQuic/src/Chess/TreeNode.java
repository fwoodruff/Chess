
package Chess;

import static Chess.Heuristic.KingValue;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class TreeNode {
    /*
    Each TreeNode object is a node in a tree. They point to their parent and child nodes
    and also to their associated board object
    */
    static int NULL = -4040404; // evaluation cannot give this value but still hacky

    Board data;
    TreeNode parent;
    LinkedList<TreeNode> children;
    boolean terminal;
    int nodeEvaluation;
    boolean isCurrent;
    boolean finalised; // node has been checked for all scenarios

    public TreeNode(){
        /*
        Makes a new TreeNode
        */
        data = new Board();
        parent=null;
        children = new LinkedList<>();
        terminal = false;
        finalised = false;
        nodeEvaluation = NULL;
    }

    void addChild(Board b){
        /*
        Adds a child to the tree
        */
        TreeNode temp = new TreeNode();
        temp.data = b;
        temp.parent = this;
        this.children.add(temp);
    } 

    void sortChildren(boolean reverse){
        /*
        Sorts a node's children by its evaluation to increase the number of
        alpha-beta cutoffs in a minimax search
        */
        if (reverse) {
            Collections.sort(this.children, (TreeNode o2, TreeNode o1) ->
                Double.compare(o1.nodeEvaluation, o2.nodeEvaluation));
        } else {
            Collections.sort(this.children, (TreeNode o1, TreeNode o2) ->
                Double.compare(o1.nodeEvaluation, o2.nodeEvaluation));
        }
    }

    void makeChildren(){
        /*
        Makes all of this node's children nodes and finds their associated
        board states
        */
        if ((this.children.isEmpty() && !this.terminal)|| this.data==null) { // last two conditions are dodgy
            data.findBoards(this);
        }
    }

    void pruneSiblingsOf(TreeNode onlyChild){
        /*
        When a move is played, only one child of the current board is relevant.
        */
        TreeNode child = onlyChild;
        this.children.clear();
        this.children.add(child);
        System.gc();
    }

    public int evaluate(){
        /*
        Evaluates the heuristic strength of the node
        */
        this.data.evaluate(this);
        nodeEvaluation = this.data.evaluation;
        return nodeEvaluation;
    }

    int countSiblings(){
        /*
        Counts the number of siblings a node has for its evaluation
        */
        if (this.parent == null) return 0;
        return this.parent.children.size();
    }

    boolean prepareNode(){
        /*
        Prepares a node for the minimax search so that the legal black responses
        can be ranked
        */
        
        this.makeChildren();
        this.evaluate();

        Iterator itr = this.children.iterator();
        OUTER:
        while(itr.hasNext()) {
            TreeNode temp = (TreeNode) itr.next();
            temp.evaluate();
            if (temp.nodeEvaluation==KingValue ||temp.nodeEvaluation==-KingValue ) {
                // Kings can never be taken so this should never occur
                System.out.println("Error");
                return false;
            }

            temp.makeChildren();
            Iterator itr1 = temp.children.iterator();
            MIDDLE:
            while(itr1.hasNext()) {
                TreeNode temp1 = (TreeNode) itr1.next();
                temp1.evaluate();
                if (temp1.nodeEvaluation==KingValue ||temp1.nodeEvaluation==-KingValue ) {
                    itr.remove();
                    continue OUTER;
                }
                // for each white move:
                temp1.makeChildren();
                for (TreeNode temp2 : temp1.children) {
                    temp2.evaluate();
                    if (temp2.nodeEvaluation==KingValue ||temp2.nodeEvaluation==-KingValue ) {
                        itr1.remove();
                        continue MIDDLE;
                    }
                }
                temp1.sortChildren(this.data.turn);
                temp1.nodeEvaluation=temp1.children.getFirst().nodeEvaluation;

            }
            if (temp.children.isEmpty()) {
                return false;
            }
            temp.sortChildren(!this.data.turn);
            temp.nodeEvaluation=temp.children.getFirst().nodeEvaluation;
        }
        this.sortChildren(this.data.turn);
        if (this.children.isEmpty()) {
            System.out.println("Checkmate");
            return false;
        }
        this.nodeEvaluation=this.children.getFirst().nodeEvaluation;
        return true;
    }
}

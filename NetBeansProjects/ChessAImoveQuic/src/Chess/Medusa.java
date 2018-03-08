package Chess;

import java.util.Iterator;

public class Medusa implements Runnable {
    /*
    A runnable tree searching algorithm. It is called to make new branches
    whenever a branch is cut off hence the name Medusa.
    */
    static int DEPTH = 6;
    static int ALPHANULL = -1000000;
    static int BETANULL  =  1000000;
    static Thread t=null;
    static TreeNode current = null;
    
    static public void runMedusa(TreeNode currentNode) {
        /*
        Shuts off any old search on now defunct branches. Starts a search on
        new branches.
        */
        if(t!=null) {
            t.interrupt();
            try {t.join();
            }catch(InterruptedException ex){
                System.out.println("couldn't ");
            }
        }
        Medusa.current=currentNode;
        t = new Thread(new Medusa());
        t.start();
    }

    @Override
    public void run() {
        /*
        Runs the AI when a thread is started.
        */
        doTheAI(DEPTH);
    }
    public void doTheAI(int maxDepth){
        /*
        Sorts the branches so that the best moves at each depth are looked at
        first. This increases the number of alpha beta cutoffs.
        */
        current.children.clear();
        
        current.prepareNode();
        
        for (int i = 0; i < maxDepth-1; i++) {
            System.out.print("Depth: ");
            System.out.println(i);
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            for (TreeNode temp : current.children) {
                if (temp.children.isEmpty()) {
                    continue; // unnecessary
                }
                
                for(TreeNode temp1 : temp.children){// for each black move
                    temp1.nodeEvaluation = alphaBeta(temp1 ,i, ALPHANULL, BETANULL,true);
                }
                temp.sortChildren(!current.data.turn);
                temp.nodeEvaluation=temp.children.getFirst().nodeEvaluation;
            }
            
            current.sortChildren(current.data.turn);
        }
        System.out.println("Medusa is ready.");
    }
    
    int alphaBeta(TreeNode node, int depth, int alpha, int beta, boolean whitePlayer) {
        /*
        This is the alpha beta pruned recursive minimax algorithm.
        It builds branches as and when they need to be built. To save memory,
        boards are discarded after they are finished with.
        
        */
        if (Thread.currentThread().isInterrupted()) {
            node.children.clear();
            return 0;
        }
        if (depth==0 || node.terminal){
            node.evaluate();
            return node.evaluate();
        }
        int v;
        if (whitePlayer){
            v= ALPHANULL;
            node.makeChildren();
            node.children.forEach((_item) -> {
                node.evaluate();
            });
            node.sortChildren(true);
            

            Iterator itr = node.children.iterator();
            while(itr.hasNext()) {
                TreeNode temp = (TreeNode) itr.next();
                if(Thread.currentThread().isInterrupted()){
                    break;
                }
                
                v = Math.max(v, alphaBeta(temp, depth - 1, alpha, beta, false));
                
                temp.nodeEvaluation=v;
                alpha = Math.max(alpha,v);
                itr.remove();
                if (beta<=alpha) {
                    node.children.clear();
                    break;
                }
            }
            return v;
        } else {
            v = BETANULL;
            node.makeChildren();
            
            node.children.forEach((_item) -> {
                node.evaluate();
            });
            
            node.sortChildren(false);
            Iterator itr = node.children.iterator();
            while(itr.hasNext()) {
                TreeNode temp = (TreeNode) itr.next();
                if(Thread.currentThread().isInterrupted()){
                    break;
                }
                v = Math.min(v, alphaBeta(temp, depth - 1, alpha, beta, true));
                temp.nodeEvaluation=v;
                beta = Math.min(beta,v);
                itr.remove();
                if(beta<=alpha){
                    node.children.clear();
                    break;
                }
            }
            return v;
        }
    }
}



package Chess;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Popup {
    /*
    A pop up window object for pawn promotions.
    */
    
    Object[] options = {"Queen",
        "Rook", "Knight","Bishop"};
    
    int n;
    
    public Popup(){
    /*
    Asks the user which piece they want their pawn promoted to.
    */
        JFrame frame = new JFrame();
        frame.setTitle("Chess");
        n = JOptionPane.showOptionDialog(frame, "Promote piece?","Choose a mode",
                            JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,options, options[0]);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(false);
    }
}

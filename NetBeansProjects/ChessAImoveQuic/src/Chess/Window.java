
package Chess;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;

public class Window{
    static int PIX =67;
    static int SQX =8;
    
    public Window() {
        /*
        Opens a window to display the board
        */
        JFrame frame = new JFrame();
        frame.setTitle("Chess");
        frame.setResizable(false);
        frame.setMinimumSize(new Dimension(PIX*SQX,PIX*SQX+22));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Controller controller = new Controller();
        UI view = new UI(controller); // show the contents
        frame.add(view, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}

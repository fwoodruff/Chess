/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessapi.GUI;

/**
 *
 * @author freddiewoodruff
 */


import static chessapi.ChessUtil.*;
import chessapi.game.LocalAIGame;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;

public class Window{
    public static int PIXELS =67; // per square
    
    
    public Window() {
        /*
        Opens a window to display the board
        */
        JFrame frame = new JFrame();
        frame.setTitle("Chess");
        frame.setResizable(false);
        frame.setMinimumSize(new Dimension(PIXELS*RANKLENGTH,PIXELS*FILELENGTH+22));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        UI view = new UI(new LocalAIGame()); // show the contents
        frame.add(view, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}


package Chess;

import static Chess.Window.PIX;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;


public final class UI extends JComponent {
    /*
    Contains mouse listeners and draw to window instructions
    */
    Sprite selectedPiece=null;
    Game game;
    public static List<Sprite> Pieces = new ArrayList<>();
    
    
    
    Controller controller;
    Point mousePoint;
    
    public UI(Controller controller){
        /*
        Listens for mouse clicks
        */
        this.controller = controller;
        this.game = controller.game;
        controller.drawBoard();
        
        addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent event){
                mousePoint = event.getPoint();
                controller.selectPiece(mousePoint);
                selectedPiece = controller.selectedPiece;
            }
        });
        addMouseMotionListener(new MouseMotionAdapter(){
            @Override
            public void mouseDragged(MouseEvent event){
                Point dragMousePoint = event.getPoint();
                controller.dragPiece(mousePoint, dragMousePoint);  
                repaint();
        }});
        addMouseListener(new MouseAdapter(){
            @Override
            public void mouseReleased(MouseEvent event){
                controller.releasePiece(mousePoint,event.getPoint());
                repaint();
                selectedPiece = null;
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        /*
        Draws the board and its pieces onto the window
        */
        Graphics2D g2 = (Graphics2D) g;

        boolean checkerboard = false;
        
        Color myWhite = new Color(250,253,253);
        Color myBlack = new Color(77,80,80);
        for (int i = 0; i < 8; i++) {
            checkerboard=!checkerboard;
            for (int j = 0; j < 8; j++){
                Rectangle rect = new Rectangle(i*PIX,j*PIX,PIX,PIX);
                checkerboard=!checkerboard;
                if (checkerboard) { g2.setColor(myBlack); }
                else { g2.setColor(myWhite);}
                g2.fill(rect);
            }
        }

        for (int i = 0; i < Controller.Pieces.size(); i++) { // draw all particles
            Sprite thisPiece = Controller.Pieces.get(i);
            if(thisPiece!=selectedPiece) {
                thisPiece.draw(g2);
            }
        }
        if (selectedPiece!=null){
            selectedPiece.draw(g2);
        }
    }
}

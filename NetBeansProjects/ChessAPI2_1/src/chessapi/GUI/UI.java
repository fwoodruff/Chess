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

import chessapi.GUI.SpriteImages.Sprite;
import static chessapi.ChessUtil.Colour.WHITE;

import static chessapi.GUI.Window.PIXELS;
import chessapi.game.ChessGame;
import chessapi.pieces.Move;
import chessapi.pieces.Tile;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JComponent;


public final class UI extends JComponent {
    /*
    Contains mouse listeners and draw to window instructions
    */
    Sprite selectedPiece=null;
    Sprite thisPiece=null;
    final ChessGame game;
    public static Set<Sprite> Sprites;

    
    
    Point mousePoint;
    
    public UI(ChessGame game){
        /*
        Listens for mouse clicks
        */
        
        this.game = game;
        Sprites = game.getPosition().listPieces()
                                    .stream()
                                    .map(piece -> new Sprite(piece))
                                    .collect(Collectors.toSet());
        
        

        
        addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent event){
                mousePoint = event.getPoint();
                Sprites.stream()
                    .filter(sprite -> sprite.contains(mousePoint))
                    .forEachOrdered(sprite ->  {
                            selectedPiece = sprite;
                            
                    });
            }
            @Override
            public void mouseReleased(MouseEvent event){
                selectedPiece = null;
                Move move = new Move(new Tile(mousePoint), new Tile(event.getPoint()));
                game.submitUpdate(move, WHITE);
                Sprites = game.getPosition().listPieces()
                                    .stream()
                                    .map(piece -> new Sprite(piece))
                                    .collect(Collectors.toSet());
                repaint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter(){
            @Override
            public void mouseDragged(MouseEvent event){
                Point dragMousePoint = event.getPoint();
                if (selectedPiece!=null){
                    selectedPiece.x= selectedPiece.xState+ (int)(-mousePoint.getX()+dragMousePoint.getX());
                    selectedPiece.y= selectedPiece.yState+ (int)(-mousePoint.getY()+dragMousePoint.getY());
                }
                repaint();
        }});
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
                Rectangle rect = new Rectangle(i*PIXELS,j*PIXELS,PIXELS,PIXELS);
                checkerboard=!checkerboard;
                if (checkerboard) { g2.setColor(myBlack); }
                else { g2.setColor(myWhite);}
                g2.fill(rect);
            }
        }
        Sprites .stream()
                .filter(piece -> (piece!=selectedPiece))
                .forEach(piece -> piece.draw(g2));
        if (selectedPiece!=null) selectedPiece.draw(g2);
    }
}

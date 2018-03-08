package Chess;

import static Chess.Window.PIX;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public final class Sprite {
    /*
    A class for objects that are shown in the window.
    */
    private BufferedImage img;
    int xState; // current board state
    int yState;
    boolean selected;
    
    int x; // mouse drag
    int y;
    
    public Sprite(int x, int y,char type){
        /*
        Makes a new sprite of a specified type at a specified pixel location.
        */
        img = null;
        try { img = ImageIO.read(getClass().getResource(type(type)));
        } catch (IOException ex) {}
        this.xState=x;
        this.x=x;
        this.yState=y;
        this.y=y;
        Controller.Pieces.add(this);
    }
    
    public void draw(Graphics g) {
        /*
        Draws a piece
        */
        g.drawImage(img,x,y, null);
    }
    
    public boolean contains(Point2D p){
        /*
        Checks if a point contains a piece.
        */
        return x<= p.getX() && (x + PIX) >= p.getX() && y<= p.getY() && (y+PIX) >= p.getY();
    }

    public String type(char typ){
        /*
        takes a piece type (char) and returns the image file
        */
        switch (typ) {
            case 'P': return "pieces/whitepawn.png";
            case 'p': return "pieces/blackpawn.png";
            case 'K': return "pieces/whiteking.png";
            case 'k': return "pieces/blackking.png";
            case 'B': return "pieces/whitebishop.png";
            case 'b': return "pieces/blackbishop.png";
            case 'N': return "pieces/whiteknight.png";
            case 'n': return "pieces/blackknight.png";
            case 'R': return "pieces/whiterook.png";
            case 'r': return "pieces/blackrook.png";
            case 'Q': return "pieces/whitequeen.png";
            case 'q': return "pieces/blackqueen.png";
            default:  return null;
        }
    }
}
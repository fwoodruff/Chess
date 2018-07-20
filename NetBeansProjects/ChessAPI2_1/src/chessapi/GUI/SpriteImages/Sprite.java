package chessapi.GUI.SpriteImages;


import static chessapi.ChessUtil.FILELENGTH;
import static chessapi.ChessUtil.RANKLENGTH;
import static chessapi.GUI.Window.PIXELS;
import chessapi.board.PlacedPiece;
import chessapi.pieces.PieceType;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public final class Sprite {
    /*
    A class for objects that are shown in the window.
    */
    private BufferedImage img;
    public int xState; // current board state
    public int yState;
    public boolean selected;
    
    public int x; // mouse drag
    public int y;

    public Sprite(PlacedPiece piece) {
        this.xState=piece.getFile()*PIXELS;
        this.x=piece.getFile()*PIXELS;
        this.yState=piece.getRank()*PIXELS;
        this.y=piece.getRank()*PIXELS;
        img = null;
        try {
            
            Thread.currentThread().getContextClassLoader();
            URL piecepic = Sprite.class.getResource(type(piece.getType()));
            img = ImageIO.read(piecepic);
        } catch (IOException ex) {System.err.println("File not found");}
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
        return x<= p.getX() && (x + PIXELS) >= p.getX() && y<= p.getY() && (y+PIXELS) >= p.getY();
    }

    public String type(PieceType typ){
        /*
        takes a piece type (char) and returns the image file
        */
        //String path = "/GUI/SpriteImages/";
        switch (typ) {
            case WHITEPAWN: return "WhitePawn.png";
            case BLACKPAWN: return "BlackPawn.png";
            case WHITEKING: return "WhiteKing.png";
            case BLACKKING: return "BlackKing.png";
            case WHITEBISHOP: return "WhiteBishop.png";
            case BLACKBISHOP: return "BlackBishop.png";
            case WHITEKNIGHT: return "WhiteKnight.png";
            case BLACKKNIGHT: return "BlackKnight.png";
            case WHITEROOK: return "WhiteRook.png";
            case BLACKROOK: return "BlackRook.png";
            case WHITEQUEEN: return "WhiteQueen.png";
            case BLACKQUEEN: return "BlackQueen.png";
            default:  return null;
        }
    }
}
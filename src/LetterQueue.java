import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 * Fronta pre správy
 * 
 * každý odoslaný list sa zaradí do fronty, tam chvíľu pobudne a keď sa dostane
 * na začiatok, doručí sa
 * 
 * neposielajú sa priamo správy, ale listy
 */
public class LetterQueue implements Drawable {
    ArrayList<Letter> list = new ArrayList<Letter>();
    //premenne pre vykreslovanie
    Canvas canvas;
    int width, height;
    
    public void setCanvas(Canvas canvas) {this.canvas = canvas;}
    
    public void setPosition(int width, int height) {
    	this.width = width; this.height = height;
    }

    void processNewLetter(Letter letter) {
        list.add(letter);
    }

    void deliverFirstLetter() {
        // TODO
    }

    public void draw(Graphics g) {
        // TODO
    	g.setColor(new Color(255, 255, 255));
    	g.fillRect(0, 0, width, height);
    }
}

class Letter {
    Edge edge;
    String content;
    
    public Letter(Edge edge, String content) {
        this.edge = edge;
        this.content = content;
    }

    public Letter(Vertex v, Message message) {
        this(v.edges.get(message.port), message.content);
    }
    
}
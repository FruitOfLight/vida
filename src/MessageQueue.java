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
public class MessageQueue implements Drawable {
    ArrayList<Message> list = new ArrayList<Message>();
    //premenne pre vykreslovanie
    Canvas canvas;
    int x, y, width, height;
    
    public void setCanvas(Canvas canvas) {this.canvas = canvas;}
    
    public void setPosition(int x, int y, int width, int height) {
    	this.x = x; this.y = y;
    	this.width = width; this.height = height;
    }

    void processNewMessage(Message message) {
        // TODO
    }

    void deliverFirstLetter() {
        // TODO
    }

    public void draw(Graphics g) {
        // TODO
    	g.setColor(new Color(255, 255, 255));
    	g.fillRect(x, y, width, height);
    }
}
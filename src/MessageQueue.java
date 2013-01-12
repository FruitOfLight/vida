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
    public static MessageQueue getInstance() { return instance; }
    private static MessageQueue instance = new MessageQueue(); 
    
    private MessageQueue(){
        
    }
    
    ArrayList<Message> list = new ArrayList<Message>();
    //premenne pre vykreslovanie
    Canvas canvas;
    int width, height;
    
    public void setCanvas(Canvas canvas) {this.canvas = canvas;}
    
    public void setPosition(int width, int height) {
    	this.width = width; this.height = height;
    }

    void processNewMessage(Message message) {
        // TODO
        // nahodna zmena
    }

    void deliverFirstMessage() {
        // TODO
    }

    public void draw(Graphics g) {
        // TODO
    	g.setColor(new Color(255, 255, 255));
    	g.fillRect(0, 0, width, height);
    	g.setColor(new Color(0, 0, 0));
    	g.drawRect(0, 0, width-1, height-1);
    }
}
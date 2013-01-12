import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
    
    Model model;
    Timer timer;
    static class TimerEvent extends TimerTask{
        public void run(){
            if (getInstance().model==null) return;
            if (!getInstance().model.running) return;
            getInstance().deliverFirstMessage();
            getInstance().timer.schedule(new TimerEvent(), 500);
        }
    }
    
    private MessageQueue(){
        timer = new Timer();
        canvas = new Canvas(this);
    }
    
    ArrayList<Message> list = new ArrayList<Message>();
    //premenne pre vykreslovanie
    Canvas canvas;
    int width, height;
    
    public void setCanvas(Canvas canvas) {this.canvas = canvas;}
    
    public void setPosition(int width, int height) {
    	this.width = width; this.height = height;
    }
 
    void pushMessage(Message message) {
        list.add(message);
        canvas.repaint();
    }

    void deliverFirstMessage() {
        if (list.size()<=0) return;
        Message message = list.get(0);
        list.remove(0);
        if (message.edge.to.program==null || message.edge.to.program.running==false){
            System.err.println("Recipient doesn't exist\n  message was delayed\n");
            // TODO pozor, aby sa nemenilo poradie na hrane
            list.add(message);
            list.remove(0);
            return;
        }
        message.edge.to.recieve(message);
        canvas.repaint();
    }

    public void draw(Graphics g) {
        this.width = canvas.getWidth();
        this.height = canvas.getHeight();
        // TODO
    	g.setColor(new Color(255, 255, 255));
    	g.fillRect(0, 0, width, height);
    	g.setColor(new Color(0, 0, 0));
    	g.drawRect(0, 0, width-1, height-1);
    	for(int i=0; i<list.size(); i++) {
    		g.drawString(((Integer)list.get(i).edge.from.getID()).toString(),20+i*25,20);
    	}
    }
}
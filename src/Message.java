import java.awt.Color;
import java.awt.Graphics;

class Message {
    int fromPort;
    int toPort;
    Edge edge;
    String content;

    public Message(int port, String content) {
        this.fromPort = port;
        this.content = content;
    }
    
    public void queueDraw(Graphics g, int position) {
    	g.drawString(((Integer)edge.from.getID()).toString(),20+position*25,20);
    }
    
    public void messageDraw(Graphics g, double receivness) {
    	g.setColor(new Color(255, 0, 0));
    	double vx = edge.to.getX()-edge.from.getX(), vy = edge.to.getY()-edge.from.getY();
    	vx*=receivness; vy*=receivness;
    	g.fillRect(edge.from.getX()+(int)Math.round(vx), edge.from.getY()+(int)Math.round(vy), 5, 5);
    }

}

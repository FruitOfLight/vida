import java.awt.Color;
import java.awt.Graphics;

class Message {
    int fromPort;
    int toPort;
    Edge edge;
    String content;
    int x,y;

    public Message(int port, String content) {
        this.fromPort = port;
        this.content = content;
    }
    
    public void setEdge(Edge edge) {
    	this.edge = edge;
    	x = edge.from.getX();
    	y = edge.from.getY();
    }
    
    public void queueDraw(Graphics g, int position) {
    	g.drawString(((Integer)edge.to.getID()).toString(),20+position*25,20);
    }
    
    public void messageDraw(Graphics g, double receivness) {
    	g.setColor(new Color(255, 0, 0));
    	double vx = edge.to.getX()-x, vy = edge.to.getY()-y;
    	vx*=receivness; vy*=receivness;
    	x = x+(int)Math.round(vx);
    	y = y+(int)Math.round(vy);
    	g.fillRect(x, y, 5, 5);
    	g.drawString(((Integer)edge.to.getID()).toString(), x, y);
    }

}

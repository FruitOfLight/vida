import java.awt.Color;
import java.awt.Graphics;

class Message {
    int fromPort;
    int toPort;
    Edge edge;
    String content;
    int x, y;

    public Message(int port, String content) {
        this.fromPort = port;
        this.content = content;
        dead = false;
        position = speed = 0.0;
    }

    public void setEdge(Edge edge) {
        this.edge = edge;
        x = edge.from.getX();
        y = edge.from.getY();
    }

    public void queueDraw(Graphics g, int position) {
        g.drawString(((Integer) edge.to.getID()).toString(),
                20 + position * 25, 20);
    }

    public void messageDraw(Graphics g) {
        g.setColor(new Color(255, 0, 0));
        int x = (int) Math.round(edge.from.getX() * (1.0 - position)
                + edge.to.getX() * (position));
        int y = (int) Math.round(edge.from.getY() * (1.0 - position)
                + edge.to.getY() * (position));
        g.fillRect(x - 3, y - 3, 6, 6);
        g.drawString(((Integer) edge.to.getID()).toString(), x, y);
    }

    public void messageDrawOld(Graphics g, int receivness) {
        g.setColor(new Color(255, 0, 0));
        double vx = edge.to.getX() - x, vy = edge.to.getY() - y;
        vx /= (double) receivness;
        vy /= (double) receivness;
        x = x + (int) Math.round(vx);
        y = y + (int) Math.round(vy);
        int x1 = x, y1 = y;
        for (int dx = -2; dx <= 2; dx++)
            for (int dy = -2; dy <= 2; dy++) {
                if (edge.howNear(x, y) > edge.howNear(x1 + dx, y1 + dy)) {
                    x = x1 + dx;
                    y = y1 + dy;
                }
            }
        g.fillRect(x - 3, y - 3, 6, 6);
        g.drawString(((Integer) edge.to.getID()).toString(), x, y);
    }

    double position, speed;
    long expectedRecieve;
    boolean dead;

    public void setRecieveness(long time) {
        if (time < 0) {
            expectedRecieve = 0;
            dead = true;
            return;
        }
        expectedRecieve = time;
    }

    public void step(long time) {
        //System.err.println("time " + time + " expected " + expectedTime);
        double expectedTime = (expectedRecieve -  System.currentTimeMillis())*0.001;
        double expectedSpeed;
        if (expectedTime < 1e-3) {
            expectedSpeed = 1;
        } else {
            expectedSpeed = (1.0 - position) / expectedTime;
        }

        speed = expectedSpeed;
        //speed += (expectedSpeed-speed)*(0.01);
        position += speed * time * 0.001;
        if (dead && position >= 1.0) {
            MessageQueue.getInstance().deadlist.remove(this);
            position = 1.0;
            edge.to.receive(this);
        }
    }

}

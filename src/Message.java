import java.awt.Color;
import java.awt.Graphics;

class Message {
    int fromPort;
    int toPort;
    Edge edge;
    String content;
    MessageState state;
    double ePosition, eSpeed;
    double qX, qY, qSpeed;
    double qSize;
    long expectedRecieve;

    public Message(int port, String content) {
        this.fromPort = port;
        this.content = content;
        state = MessageState.born;
        ePosition = eSpeed = 0.0;
        qSpeed = 0.0;

    }

    public void setEdge(Edge edge) {
        this.edge = edge;
    }

    public void queueDraw(Graphics g, double position, double size) {

        g.setColor(new Color(200, 255, 200));
        Canvas.realFillRect(g, position + 1, 5, size - 2, size);
        g.setColor(new Color(0, 0, 0));
        Canvas.realDrawRect(g, position + 1, 5, size - 2, size);
        if (size > 18) {
            g.drawString(Canvas.shorten(g, ((Integer) edge.to.getID()).toString(), (int) size - 5,
                    Preference.begin), (int) (5 + position), 20);
            if (size > 30) {
                g.drawString(Canvas.shorten(g, content, (int) size - 5, Preference.begin),
                        (int) (5 + position), 32);
                if (size > 40)
                    g.drawString(Canvas.shorten(g, content, (int) size - 5, Preference.end),
                            (int) (5 + position), 44);
            }
        }
    }

    public void edgeDraw(Graphics g) {
        g.setColor(new Color(255, 0, 0));
        int x = (int) Math.round(edge.from.getX() * (1.0 - ePosition) + edge.to.getX()
                * (ePosition));
        int y = (int) Math.round(edge.from.getY() * (1.0 - ePosition) + edge.to.getY()
                * (ePosition));
        int xPoints[] = new int[3], yPoints[] = new int[3];
        xPoints[0] = x;
        yPoints[0] = y;
        double ux = edge.from.getY() - edge.to.getY(), uy = edge.to.getX() - edge.from.getX();
        // Tu sa da nastavovat velkost trojuholnika
        double k = 12.0 / Math.sqrt(ux * ux + uy * uy);
        double vx = ux * k;
        double vy = uy * k;
        xPoints[1] = (int) Math.round(x + vx + vy / 2.0);
        yPoints[1] = (int) Math.round(y + vy - vx / 2.0);
        xPoints[2] = (int) Math.round(x + vx - vy / 2.0);
        yPoints[2] = (int) Math.round(y + vy + vx / 2.0);
        g.fillPolygon(xPoints, yPoints, 3);
        // g.drawString(((Integer) edge.to.getID()).toString(), x, y);
    }

    public void setRecieveness(long time) {
        if (time < 0) {
            expectedRecieve = 0;
            state = MessageState.dead;
            return;
        }
        expectedRecieve = time;
    }

    public void edgeStep(long time) {
        // System.err.println("time " + time + " expected " + expectedTime);
        double expectedTime = (expectedRecieve - System.currentTimeMillis()) * 0.001;
        double expectedSpeed;
        if (expectedTime < 1e-2) {
            expectedSpeed = 1;
        } else {
            expectedSpeed = (1.0 - ePosition) / expectedTime;
        }
        if (expectedSpeed > 1.0)
            expectedSpeed = 1.0;

        eSpeed = expectedSpeed;
        // speed += (expectedSpeed-speed)*(0.01);
        ePosition += eSpeed * time * 0.001;
        if (state == MessageState.dead && ePosition >= 1.0) {
            MessageQueue.getInstance().deadList.remove(this);
            ePosition = 1.0;
            edge.to.receive(this);
        }
    }

    public void queueStep(long time) {

    }
}

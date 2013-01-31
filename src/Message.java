import java.awt.Color;
import java.awt.Graphics;

class Message {
    int fromPort;
    int toPort;
    Edge edge;
    String rawContent;
    MessageState state;
    double ePosition, eSpeed;
    Color gColor;

    public Message(int port, String content) {
        this.fromPort = port;
        this.rawContent = content;
        gColor = Color.red;
        processContent();
    }

    void processContent() {
        int pos = 0;
        while ((pos = rawContent.indexOf("$", pos)) != -1) {
            pos++;
            if (rawContent.charAt(pos) == 'C') {
                try {
                    gColor = new Color(Integer.parseInt(rawContent.substring(pos + 1, pos + 7), 16));
                } catch (Exception e) {
                    gColor = Color.black;
                }
            }
        }
    }

    public void setEdge(Edge edge) {
        this.edge = edge;
    }

    public void queueDraw(Graphics g, double offset, double zoom) {
        double rX = offset + (qX - qSize) * zoom;
        double rY = CONST.queueHeight - (qY + qSize) * zoom;
        double rW = qSize * zoom - 2;
        double rH = qSize * zoom - 2;

        //System.err.println(" " + rX + " " + rY + " " + rW + " " + rH);

        g.setColor(new Color(200, 255, 200));
        Canvas.realFillRect(g, rX, rY, rW, rH);
        if (qX < qSize) {
            g.setColor(new Color(255, 50, 40));
            Canvas.realFillRect(g, rX, rY, offset - rX, rH);
        }
        g.setColor(new Color(0, 0, 0));
        Canvas.realDrawRect(g, rX, rY, rW, rH);

        drawInfo(g, (int) rX, (int) rY, (int) rW, (int) rH);

        g.setColor(gColor);
        Canvas.realFillRect(g, rX, rY - 1, rW, 3);
    }

    public void drawInfo(Graphics g, int rX, int rY, int rW, int rH) {
        // tuto to mozno nie je uplne najrychljeise

        String[] ids = Canvas.shortenWrap(g, ((Integer) edge.from.getID()).toString() + ">"
                + ((Integer) edge.to.getID()).toString(), rW - 2, ">");
        for (int i = 0; i < ids.length
                && g.getFontMetrics().getHeight() * (i + 1) < rH + g.getFontMetrics().getLeading(); ++i)
            g.drawString(ids[i], (rX) + 1, rY + g.getFontMetrics().getHeight() * (i + 1)
                    - g.getFontMetrics().getLeading() - g.getFontMetrics().getDescent());
        String[] contents = Canvas.multiGet(g, rawContent, rW - 2);
        for (int i = 0; i < contents.length
                && g.getFontMetrics().getHeight() * (i + ids.length + 1) < rH
                        + g.getFontMetrics().getLeading(); ++i)
            g.drawString(contents[i], (rX) + 1, rY + g.getFontMetrics().getHeight()
                    * (i + ids.length + 1) - g.getFontMetrics().getLeading()
                    - g.getFontMetrics().getDescent());
    }

    public void edgeDraw(Graphics g, double offsetx, double offsety, double zoom) {
        g.setColor(gColor);
        double x = (edge.from.getX() * (1.0 - ePosition) + edge.to.getX() * (ePosition));
        double y = (edge.from.getY() * (1.0 - ePosition) + edge.to.getY() * (ePosition));
        int rX = (int) (offsetx + x * zoom);
        int rY = (int) (offsety + y * zoom);
        // Tu sa da nastavovat velkost trojuholnika
        int rR = (int) (12.0 * zoom);
        int xPoints[] = new int[3], yPoints[] = new int[3];
        xPoints[0] = rX;
        yPoints[0] = rY;
        double ux = edge.from.getY() - edge.to.getY(), uy = edge.to.getX() - edge.from.getX();

        double k = rR / Math.sqrt(ux * ux + uy * uy);
        double vx = ux * k;
        double vy = uy * k;
        xPoints[1] = (int) Math.round(rX + vx + vy / 2.0);
        yPoints[1] = (int) Math.round(rY + vy - vx / 2.0);
        xPoints[2] = (int) Math.round(rX + vx - vy / 2.0);
        yPoints[2] = (int) Math.round(rY + vy + vx / 2.0);
        g.fillPolygon(xPoints, yPoints, 3);
        // g.drawString(((Integer) edge.to.getID()).toString(), x, y);
    }

    public void edgeStep(long time) {
        double expectedTime = (1.0 - qSize) / vspeed + qX
                / (hspeed * MessageQueue.getInstance().getRealSendSpeed());
        if (state == MessageState.dead)
            expectedTime = 0;

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
        if (ePosition >= 1.0)
            ePosition = 1.0;
        if (state == MessageState.dead) {
            MessageQueue.getInstance().deadList.remove(this);
            edge.to.receive(this);
        }
    }

    double qX, qY, qSpeed;
    double qSize;
    static final double vspeed = 50;
    static final double hspeed = 50;

    public void born(int index) {
        state = MessageState.born;
        ePosition = eSpeed = 0.0;
        qSpeed = 0.0;
        qY = 0.0;
        qX = index + 2;
        qSize = 0.0;
    }

    public void queueStep(long time, int index) {
        if (state == MessageState.born) {
            qSize += vspeed * time * 0.001 * MessageQueue.getInstance().getRealSendSpeed();
            if (qSize > 1.0) {
                qSize = 1.0;
                state = MessageState.main;
            }
        }
        if (state == MessageState.main) {
            moveForward(time, index);
            if (qX < 0.0) {
                MessageQueue.getInstance().mainList.remove(index);
                MessageQueue.getInstance().deadList.add(this);
                state = MessageState.dead;
            }
        }
        if (state == MessageState.sleep) {
            qY = (CONST.queueHeight / MessageQueue.getInstance().zoom) - qSize;
        } else {
            qY = 0.0;
        }
        if (state == MessageState.dead) {

        }
    }

    void moveForward(long time, int index) {
        Message prev = (index > 0) ? MessageQueue.getInstance().mainList.get(index - 1) : null;
        double shift = hspeed * time * 0.001 * MessageQueue.getInstance().getRealSendSpeed();
        if (prev == null || prev.qX + qSize < qX) {
            if (MessageQueue.getInstance().model.running == RunState.running) {
                qX -= shift;
                qY = 0.0;
            }
        } else {
            if (prev.qX + qSize - qX < shift) {
                qX = prev.qX + qSize;
            } else {
                qX += shift;
                qY = 0.1;
            }
        }

    }

    public boolean isOnPoint(double x, double y) {
        if (x < qX - qSize || x > qX)
            return false;
        if (y > qY + qSize || y < qY)
            return false;
        System.out.println("on point");
        return true;
    }

    public void onClick() {
        if (state == MessageState.sleep) {
            state = MessageState.main;
            return;
        }
        if (state == MessageState.main) {
            state = MessageState.sleep;
            return;
        }
    }

}

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

class Message {
    static double getRandomMessageSpeed() {
        return GUI.random.nextDouble() * 0.45 + 0.05;
    }

    int fromPort;
    int toPort;
    Edge edge;
    String rawContent;
    double ePosition, eSpeed, defSpeed;
    DeliverState state;
    Color gColor;

    public Message(int port, String content) {
        state = DeliverState.born;
        this.fromPort = port;
        this.rawContent = content;
        gColor = Color.red;
        defSpeed = getRandomMessageSpeed();
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

    public void drawInfo(Graphics g, int rX, int rY, int rW, int rH) {
        // tuto to mozno nie je uplne najrychljeise

        String[] ids = Canvas.shortenWrap(g, ((Integer) edge.from.getID()).toString() + ">"
                + ((Integer) edge.to.getID()).toString(), rW - 2, ">");
        for (int i = 0; i < ids.length
                && g.getFontMetrics().getHeight() * (i + 1) < rH + g.getFontMetrics().getLeading(); ++i) {
            g.drawString(ids[i], rX + 1, rY + g.getFontMetrics().getHeight() * (i + 1)
                    - g.getFontMetrics().getLeading() - g.getFontMetrics().getDescent());
        }
        String[] contents = Canvas.multiGet(g, rawContent, rW - 2);
        for (int i = 0; i < contents.length
                && g.getFontMetrics().getHeight() * (i + ids.length + 1) < rH
                        + g.getFontMetrics().getLeading(); ++i) {
            g.drawString(contents[i], rX + 1, rY + g.getFontMetrics().getHeight()
                    * (i + ids.length + 1) - g.getFontMetrics().getLeading()
                    - g.getFontMetrics().getDescent());
        }
    }

    public void edgeDraw(Graphics2D g) {
        if (state == DeliverState.dead)
            return;
        g.setColor(gColor);
        double x = edge.from.getX() * (1.0 - ePosition) + edge.to.getX() * ePosition;
        double y = edge.from.getY() * (1.0 - ePosition) + edge.to.getY() * ePosition;
        int rX = (int) (x);
        int rY = (int) (y);
        // Tu sa da nastavovat velkost trojuholnika
        int rR = (int) (12.0);
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
        if (state == DeliverState.dead)
            return;
        ePosition += GUI.model.getSendSpeed() * eSpeed * time * 0.001;
        if (ePosition >= 1.0) {
            ePosition = 1.0;
            recieve();
        }
        if (ePosition <= 0.0) {
            ePosition = 0.0;
        }
    }

    public void recieve() {
        if (state == DeliverState.dead)
            return;
        state = DeliverState.dead;
        edge.to.receive(this);
    }

}

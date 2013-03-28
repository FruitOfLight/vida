import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

class Message {
    static double getRandomMessageMass() {
        return GUI.random.nextDouble() * 9 + 1;
    }

    int fromPort;
    int toPort;
    Edge edge;
    String rawContent;
    double position, mass, force, defDist;
    Message prevM, nextM;

    DeliverState state;
    Color gColor;

    public Message(int port, String content) {
        state = DeliverState.born;
        this.fromPort = port;
        this.rawContent = content;
        gColor = Color.red;
        mass = getRandomMessageMass();
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
        if (state == DeliverState.inbox)
            return;
        g.setColor(gColor);
        double x = edge.from.getX() * (1.0 - position) + edge.to.getX() * position;
        double y = edge.from.getY() * (1.0 - position) + edge.to.getY() * position;
        // Tu sa da nastavovat velkost trojuholnika
        double rR = 12.0;
        double ux = edge.from.getY() - edge.to.getY(), uy = edge.to.getX() - edge.from.getX();

        double k = rR / Math.sqrt(ux * ux + uy * uy);
        double vx = ux * k;
        double vy = uy * k;
        Path2D polygon = new Path2D.Double();
        polygon.moveTo(x, y);
        polygon.lineTo(x + vx + vy * 0.5, y + vy - vx * 0.5);
        polygon.lineTo(x + vx - vy * 0.5, y + vy + vx * 0.5);
        polygon.lineTo(x, y);
        g.fill(polygon);
        // g.drawString(((Integer) edge.to.getID()).toString(), x, y);
    }

    public void measure(long time) {
        if (GUI.model.running == RunState.running)
            force = 1;
        else
            force = 0;

        if ((prevM != null) && (prevM.position - position < defDist)) {
            if (prevM.position - position < defDist * 0.1) {
                force -= Math.pow(2 * 0.9, 2);
            } else {
                force -= Math.pow(2 * (defDist - prevM.position + position) / defDist, 2);
            }
        }
        if ((nextM != null) && (position - nextM.position < defDist)) {
            if (position - nextM.position < defDist * 0.1) {
                force += Math.pow(0.5 * 0.9, 2);
            } else {
                force += Math.pow(0.5 * (defDist - position + nextM.position) / defDist, 2);
            }
        }
    }

    public void move(long time) {
        if (state == DeliverState.inbox)
            return;
        double speed = GUI.model.getSendSpeed() * time * 0.001 * force / mass;
        position += speed;
        if (position >= 1.0) {
            position = 1.0;
            if (GUI.model.running == RunState.running)
                state = DeliverState.inbox;
        }
        if (position <= 0.0) {
            position = 0.0;
        }
    }
    /*public void edgeStep(long time) {
        if (state == DeliverState.inbox)
            return;
        position += GUI.model.getSendSpeed() * eSpeed * time * 0.001;
        if (position >= 1.0) {
            position = 1.0;
            state = DeliverState.inbox;
        }
        if (position <= 0.0) {
            position = 0.0;
        }
    }*/
}

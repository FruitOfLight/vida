import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

class Message {
    static double getRandomMessageFactor() {
        return GUI.random.nextDouble() * 0.45 + 0.05;
    }

    int fromPort;
    int toPort;
    Edge edge;
    String rawContent;
    double position, factor, force, defDist;
    Message prevM, nextM;
    int selected;
    String info;

    DeliverState state;
    Color gColor;

    public Message(int port, String content) {
        selected = 0;
        state = DeliverState.born;
        this.fromPort = port;
        this.rawContent = content;
        gColor = Color.red;
        factor = getRandomMessageFactor();
        processContent();
        int from = 0, to;
        while ((from = content.indexOf("{", from)) != -1) {
            from++;
            to = content.indexOf("}", from);
            if (to != -1)
                info = content.substring(from, to);
        }
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

    public void zoomDraw(Graphics2D g) {
        double indent = 25;
        Path2D polygon = new Path2D.Double();
        polygon.moveTo(indent, indent);
        polygon.lineTo(CONST.zoomWindowWidth - indent, indent);
        polygon.lineTo(CONST.zoomWindowWidth / 2.0, CONST.zoomWindowHeight - indent);
        polygon.lineTo(indent, indent);
        g.setColor(gColor);
        g.fill(polygon);
        g.setColor(new Color(255, 255, 255));
        int fontSize = 14;
        g.setFont(new Font(null, Font.PLAIN, fontSize));
        double height = indent + fontSize;
        int position = 0;
        String[] parts = rawContent.split(" ");
        while (true) {
            if (height > CONST.zoomWindowHeight - indent - 2 * fontSize)
                break;
            if (position >= parts.length)
                break;
            double begin = indent
                    + (height - indent)
                    * ((CONST.zoomWindowWidth / 2 - indent) / (double) (CONST.zoomWindowHeight - 2 * indent));
            double end = CONST.zoomWindowHeight
                    - indent
                    - (height - indent)
                    * ((CONST.zoomWindowWidth / 2 - indent) / (double) (CONST.zoomWindowHeight - 2 * indent));
            String caption = "";
            while (position != parts.length
                    && g.getFontMetrics().stringWidth(caption + " " + parts[position]) + begin < end) {
                caption += " " + parts[position];
                position++;
            }
            g.drawString(caption, (float) begin, (float) height);
            height += fontSize;
        }
    }

    public void edgeDraw(Graphics2D g) {
        if (state == DeliverState.delivered)
            return;
        if (GUI.controls.get("v_bubble-messages").isActive()) {
            bubbleDraw(g);
        } else {
            g.setColor(gColor);
            double x = edge.from.getX() * (1.0 - position) + edge.to.getX() * position;
            double y = edge.from.getY() * (1.0 - position) + edge.to.getY() * position;
            // Tu sa da nastavovat velkost trojuholnika
            double rR;
            if (selected != 5)
                rR = 12.0 + selected * 5.0;
            else
                rR = 17.0;
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
        }
    }

    public void bubbleDraw(Graphics2D g) {
        double x = edge.from.getX() * (1.0 - position) + edge.to.getX() * position;
        double y = edge.from.getY() * (1.0 - position) + edge.to.getY() * position;

        double fx = edge.from.getX() - edge.to.getX();
        double fy = edge.from.getY() - edge.to.getY();
        //float alpha = 0.8 * (isOnPoint(GUI.graph.mousex, GUI.graph.mousey) ? 0.5f : 1.0f);
        //if (alpha < 0.01)
        //    return;
        //Composite originalComposite = g.getComposite();
        //if (alpha < 0.99) {
        //    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        //}
        AffineTransform at = g.getTransform();
        double dx, dy, dw, dh, dr = 4, desc = g.getFontMetrics().getDescent();
        dw = g.getFontMetrics().stringWidth(info) + dr;
        dh = g.getFontMetrics().getAscent() + g.getFontMetrics().getDescent();
        dx = x - dw / 2;
        dy = y - dh;
        g.translate(x, y);
        g.rotate(Math.atan2(fy, fx));
        g.scale(1.0 / Math.sqrt(GUI.graph.canvas.zoom), 1.0 / Math.sqrt(GUI.graph.canvas.zoom));
        g.translate(-x, -y);

        Color c = new Color(255, 180, 100);
        g.setColor(c);
        g.fill(new RoundRectangle2D.Double(dx, dy, dw, dh, dr, dr));
        g.setColor(Canvas.contrastColor(c, Constrast.borderbw));
        g.draw(new RoundRectangle2D.Double(dx, dy, dw, dh, dr, dr));
        g.setColor(Canvas.contrastColor(c, Constrast.textbw));
        g.drawString(info, (float) (dx + dr / 2), (float) (y - desc));

        g.setTransform(at);
        /*if (alpha < 0.99) {
            g.setComposite(originalComposite);
        }*/
    }

    double side(double x, double y, double x1, double y1, double x2, double y2) {
        x1 -= x;
        y1 -= y;
        x2 -= x;
        y2 -= y;
        return Math.signum(x1 * y2 - x2 * y1);
    }

    public boolean isOnPoint(double mx, double my) {
        if (state == DeliverState.delivered)
            return false;
        double x = edge.from.getX() * (1.0 - position) + edge.to.getX() * position;
        double y = edge.from.getY() * (1.0 - position) + edge.to.getY() * position;
        // Tu sa da nastavovat velkost trojuholnika
        double rR = 12.0;
        double ux = edge.from.getY() - edge.to.getY(), uy = edge.to.getX() - edge.from.getX();

        double k = rR / Math.sqrt(ux * ux + uy * uy);
        double vx = ux * k;
        double vy = uy * k;
        double side1 = side(x, y, x + vx + vy * 0.5, y + vy - vx * 0.5, mx, my);
        double side2 = side(x + vx + vy * 0.5, y + vy - vx * 0.5, x + vx - vy * 0.5, y + vy + vx
                * 0.5, mx, my);
        double side3 = side(x + vx - vy * 0.5, y + vy + vx * 0.5, x, y, mx, my);
        if (side1 * side2 > 0 && side2 * side3 > 0 && side2 * side1 > 0)
            return true;
        return false;
    }

    public void measure(long time) {
        if (GUI.model.running == RunState.running)
            force = 1;
        else
            force = 0;

        if ((prevM != null) && (prevM.position - position < defDist)) {
            force -= Math.pow(2 * (defDist - prevM.position + position) / defDist, 2);
        }
        if ((nextM != null) && (position - nextM.position < defDist)) {
            force += Math.pow(1.0 * (defDist - position + nextM.position) / defDist, 2);
        }
        double turbo = 1.0;
        switch (edge.getSpeed()) {
        case -2:
            turbo = 0;
            break;
        case -1:
            turbo = 0.3;
            break;
        case 1:
            turbo = 3.0;
            break;
        case 2:
            turbo = 50.0;
            break;
        }
        force *= turbo * factor;
        GUI.model.listenSpeed(force);

        if (selected > 0 && selected != 5) {
            double x = edge.from.getX() * (1.0 - position) + edge.to.getX() * position;
            double y = edge.from.getY() * (1.0 - position) + edge.to.getY() * position;
            double x1 = GUI.graph.listener.xlast - x;
            double y1 = GUI.graph.listener.ylast - y;
            double x2 = (edge.to.getX() - edge.from.getX());
            double y2 = (edge.to.getY() - edge.from.getY());
            double prod = (x1 * x2 + y1 * y2) / Math.sqrt(x2 * x2 + y2 * y2);
            force += Math.min(100.0, Math.max(prod / 10, -100.0)) / factor;
        }

    }

    public void move(long time) {
        if (state == DeliverState.delivered)
            return;

        double speed = GUI.model.getSpeedBalance() * GUI.model.getSendSpeed() * time * 0.001
                * force;
        position += speed;
        if (position >= 1.0) {
            position = 1.0;
            if (GUI.model.running == RunState.running
                    && (prevM == null || prevM.state == DeliverState.inbox))
                state = DeliverState.inbox;
        }
        if (position <= 0.0) {
            position = 0.0;
        }
    }

    void setSpeed(int speed) {
        switch (speed) {
        case -2:
            factor = 0;
            break;
        case -1:
            factor = 0.5;
            break;
        case 0:
            factor = 1.2;
            break;
        case 1:
            factor = 2.5;
            break;
        case 2:
            factor = 5.0;
            break;

        }
    }

}

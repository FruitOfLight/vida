import java.awt.Color;
import java.awt.Font;
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
    int selected;
    InformationBubble informationBubble;

    DeliverState state;
    Color gColor;

    public Message(int port, String content) {
        selected = 0;
        state = DeliverState.born;
        this.fromPort = port;
        this.rawContent = content;
        gColor = Color.red;
        mass = getRandomMessageMass();
        processContent();
        informationBubble = new InformationBubble(0.0, 0.0);
        int from = 0, to;
        while ((from = content.indexOf("{", from)) != -1) {
            from++;
            to = content.indexOf("}", from);
            if (to != -1)
                informationBubble.addInformation(content.substring(from, to), -1);
        }
        //informationBubble.addInformation(rawContent, -1);
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
        g.setColor(gColor);
        double x = edge.from.getX() * (1.0 - position) + edge.to.getX() * position;
        double y = edge.from.getY() * (1.0 - position) + edge.to.getY() * position;
        informationBubble.setX(x);
        informationBubble.setY(y);
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
        informationBubble.setTransparency(0.8f);
        informationBubble.draw(g);
        // g.drawString(((Integer) edge.to.getID()).toString(), x, y);
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
        if (selected > 0 && selected != 5) {
            double x = edge.from.getX() * (1.0 - position) + edge.to.getX() * position;
            double y = edge.from.getY() * (1.0 - position) + edge.to.getY() * position;
            double x1 = GUI.graph.listener.xlast - x;
            double y1 = GUI.graph.listener.ylast - y;
            double x2 = (edge.to.getX() - edge.from.getX());
            double y2 = (edge.to.getY() - edge.from.getY());
            double prod = (x1 * x2 + y1 * y2) / Math.sqrt(x2 * x2 + y2 * y2);
            force = Math.min(100.0, Math.max(prod / 10, -100.0)) * mass;
            //System.out.println("force " + force + " " + position);
        }

        if ((prevM != null) && (prevM.position - position < defDist)) {
            force -= Math.pow(2 * (defDist - prevM.position + position) / defDist, 2);
        }
        if ((nextM != null) && (position - nextM.position < defDist)) {
            force += Math.pow(1.0 * (defDist - position + nextM.position) / defDist, 2);
        }
    }

    public void move(long time) {
        if (state == DeliverState.delivered)
            return;
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

        double speed = turbo * GUI.model.getSendSpeed() * time * 0.001 * force / mass;
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

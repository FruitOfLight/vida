import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Line2D;

class GraphListener implements MouseListener, MouseMotionListener, MouseWheelListener {
    private Graph graph;

    public Vertex begin = null;
    double xlast, ylast;
    double oldOffX, oldOffY;
    int preClickX, preClickY;

    public GraphListener(Graph graph) {
        this.graph = graph;
        xlast = 0;
        ylast = 0;
    }

    public void draw(Graphics2D g) {
        // vykresli polhranu
        if (GUI.model.running != RunState.stopped)
            return;
        if (begin != null && !GUI.gkl.isPressed(CONST.deleteKey)
                && !GUI.gkl.isPressed(CONST.moveKey)) {
            g.setColor(new Color(0, 0, 0));
            g.draw(new Line2D.Double(begin.getX(), begin.getY(), xlast, ylast));
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouse) {
        if (GUI.gkl.isPressed(CONST.deleteKey)) {
            if (GUI.model.running != RunState.stopped) {
                return;
            }
            // TODO dovolit, ale opravit graf
            if (GUI.model.settings.getGraphType() != GraphType.any) {
                return;
            }
            graph.deleteWithMouse(mouse);
            GUI.gRepaint();
            return;
        }
        if (!GUI.gkl.isPressed(CONST.deleteKey) && !GUI.gkl.isPressed(CONST.moveKey)) {
            graph.clickWithMouse(mouse);
        }
    }

    @Override
    public void mousePressed(MouseEvent mouse) {
        begin = graph.getVertex(mouseGetX(mouse), mouseGetY(mouse));
        xlast = mouseGetX(mouse);
        ylast = mouseGetY(mouse);
        preClickX = mouse.getX();
        preClickY = mouse.getY();
        oldOffX = graph.canvas.offX;
        oldOffY = graph.canvas.offY;
    }

    @Override
    public void mouseReleased(MouseEvent mouse) {
        if (GUI.model.running == RunState.stopped) {
            graph.createEdge(begin, graph.getVertex(mouseGetX(mouse), mouseGetY(mouse)));
        }
        GUI.gRepaint();
        begin = null;
        xlast = 0;
        ylast = 0;
    }

    @Override
    public void mouseDragged(MouseEvent mouse) {
        if (begin == null) {
            graph.canvas.offX = oldOffX + (mouse.getX() - preClickX);
            graph.canvas.offY = oldOffY + (mouse.getY() - preClickY);
            GUI.gRepaint();
            return;
        }
        if (GUI.gkl.isPressed(CONST.moveKey)) {
            for (Vertex vertex : graph.vertices) {
                if (!vertex.equals(begin)
                        && vertex
                                .isNearPoint(mouseGetX(mouse), mouseGetY(mouse), begin.getRadius())) {
                    return;
                }
            }
            begin.move(mouseGetX(mouse), mouseGetY(mouse));
            GUI.gRepaint();

        } else {
            if (GUI.model.running != RunState.stopped) {
                return;
            }
            xlast = mouseGetX(mouse);
            ylast = mouseGetY(mouse);
            GUI.gRepaint();
        }
    }

    @Override
    public void mouseEntered(MouseEvent mouse) {
    }

    @Override
    public void mouseExited(MouseEvent mouse) {
    }

    @Override
    public void mouseMoved(MouseEvent mouse) {
        for (Vertex vertex : graph.vertices) {
            if (vertex.isNearPoint(mouseGetX(mouse), mouseGetY(mouse), graph.canvas.zoom)) {
                if (vertex.informationPanel.getTransparency())
                    continue;
                vertex.informationPanel.setTransparency(true);
                GUI.gRepaint();
            } else {
                if (!vertex.informationPanel.getTransparency())
                    continue;
                vertex.informationPanel.setTransparency(false);
                GUI.gRepaint();
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int ticks = e.getWheelRotation();
        double scale = 1 - ticks * 0.1;

        graph.canvas.offX = (graph.canvas.offX - e.getX()) * scale + e.getX();
        graph.canvas.offY = (graph.canvas.offY - e.getY()) * scale + e.getY();
        graph.canvas.zoom *= scale;
        preClickX = e.getX();
        preClickY = e.getY();
        oldOffX = graph.canvas.offX;
        oldOffY = graph.canvas.offY;

        GUI.gRepaint();

    }

    double mouseGetX(MouseEvent mouse) {
        return (mouse.getX() - graph.canvas.offX) / graph.canvas.zoom;
    }

    double mouseGetY(MouseEvent mouse) {
        return (mouse.getY() - graph.canvas.offY) / graph.canvas.zoom;
    }

}

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
    private Controls controls;

    public Vertex begin = null;
    double xlast, ylast;
    double oldOffX, oldOffY;
    int preClickX, preClickY;

    Message selectedMessage;
    boolean noClick;

    public GraphListener(Graph graph) {
        this.graph = graph;
        xlast = 0;
        ylast = 0;
    }

    public void setControls(Controls c) {
        controls = c;
    }

    public void draw(Graphics2D g) {
        Tool tool = controls.getTool();
        // vykresli polhranu
        if (begin != null && GUI.model.running == RunState.stopped
                && tool.compatible(ToolType.create) && tool.compatible(ToolTarget.edge)) {
            g.setColor(new Color(0, 0, 0));
            g.draw(new Line2D.Double(begin.getX(), begin.getY(), xlast, ylast));
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouse) {
        try {
            if (noClick) {
                noClick = false;
                return;
            }
            Tool tool = controls.getTool();
            tool.print(System.out);
            if (tool.compatible(ToolType.select)) {
                if (graph.selectWithMouse(mouse))
                    return;
            }
            if (tool.compatible(ToolType.create) && tool.compatible(ToolTarget.vertex)) {
                if (graph.createVertex(mouseGetX(mouse), mouseGetY(mouse), graph.getNewVertexID()))
                    return;
            }
            if (tool.compatible(ToolType.delete)) {
                if (graph.deleteWithMouse(mouse))
                    return;
            }
            if (GUI.model.running == RunState.running) {
                GUI.controls.onClick("p_pause");
            } else if (GUI.model.running == RunState.paused) {
                GUI.controls.onClick("p_start");
            }
        } finally {
            GUI.gRepaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent mouse) {
        try {
            Tool tool = controls.getTool();
            tool.print(System.out);
            noClick = false;
            begin = graph.getVertex(mouseGetX(mouse), mouseGetY(mouse));
            xlast = mouseGetX(mouse);
            ylast = mouseGetY(mouse);
            preClickX = mouse.getX();
            preClickY = mouse.getY();
            oldOffX = graph.canvas.offX;
            oldOffY = graph.canvas.offY;
            if (selectedMessage != null) {
                selectedMessage.selected = 0;
            }

            if (tool.compatible(ToolTarget.message)) {
                selectedMessage = graph.getMessage(mouseGetX(mouse), mouseGetY(mouse));
                if (selectedMessage != null) {
                    GUI.zoomWindow.drawMessage(selectedMessage);
                    GUI.zoomWindow.canvas.setVisible(true);
                }
                if (selectedMessage != null) {
                    selectedMessage.selected = 1;
                }
            }
        } finally {
            GUI.gRepaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouse) {
        Tool tool = controls.getTool();
        tool.print(System.out);
        if (selectedMessage != null) {
            selectedMessage.selected = 0;
            selectedMessage = null;
            noClick = true;
        }
        if (tool.compatible(ToolType.create) && tool.compatible(ToolTarget.edge)
                && GUI.model.running == RunState.stopped) {
            graph.createEdge(begin, graph.getVertex(mouseGetX(mouse), mouseGetY(mouse)));
        }
        begin = null;
        xlast = 0;
        ylast = 0;
        GUI.gRepaint();
    }

    @Override
    public void mouseDragged(MouseEvent mouse) {
        Tool tool = controls.getTool();
        if (begin == null && selectedMessage == null) {
            graph.canvas.offX = oldOffX + (mouse.getX() - preClickX);
            graph.canvas.offY = oldOffY + (mouse.getY() - preClickY);
            GUI.gRepaint();
            return;
        }
        if (tool.type == ToolType.move) {
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
        Tool tool = controls.getTool();
        //spravy
        if (tool.compatible(ToolTarget.message)) {
            if (selectedMessage != null) {
                selectedMessage.selected = 0;
            }
            selectedMessage = graph.getMessage(mouseGetX(mouse), mouseGetY(mouse));
            if (selectedMessage != null) {
                selectedMessage.selected = 5;
            }
        }
        //vrcholy
        for (Vertex vertex : graph.vertices) {
            if (vertex.isNearPoint(mouseGetX(mouse), mouseGetY(mouse), graph.canvas.zoom)) {
                if (vertex.informationBubble.getTransparency())
                    continue;
                vertex.informationBubble.setTransparency(true);
            } else {
                if (!vertex.informationBubble.getTransparency())
                    continue;
                vertex.informationBubble.setTransparency(false);
            }
        }
        GUI.gRepaint();
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

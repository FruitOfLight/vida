import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Fronta pre správy
 * 
 */
public class MessageQueue implements Drawable {
    private static MessageQueue instance = new MessageQueue();

    public static MessageQueue getInstance() {
        return instance;
    }

    Canvas canvas;
    int width, height;
    Model model;
    Timer timer;

    private MessageQueue() {
        setCanvas(new Canvas(this));
        setSendSpeed(1.2);
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
        QueueListener listener = new QueueListener();
        this.canvas.addMouseListener(listener);
        this.canvas.addMouseMotionListener(listener);
        this.canvas.addMouseWheelListener(listener);
    }

    public void setPosition(int width, int height) {
        this.width = width;
        this.height = height;
    }

    private double sendSpeed;
    double zoom = 50.0;
    double offset = 50;
    static final double deadWidth = 1.0;

    public void setSendSpeed(double value) {
        sendSpeed = value;

    }

    public double getSendSpeed() {
        return sendSpeed;
    }

    public double getRealSendSpeed() {
        return sendSpeed / zoom;
    }

    private final LinkedList<Message> bornList = new LinkedList<Message>();
    ArrayList<Message> mainList = new ArrayList<Message>();
    ArrayList<Message> deadList = new ArrayList<Message>();

    static class StepEvent extends TimerTask {
        static long time = 0;

        @Override
        public void run() {
            if (getInstance().model == null || getInstance().model.running == RunState.stopped) {
                return;
            }
            long prevTime = time;
            time = System.currentTimeMillis();
            long delay = time - prevTime;

            // Spravy vo fronte
            MessageQueue.getInstance().step(delay);

            if (getInstance().model.running == RunState.running) {
                // Spravy v grafe
                for (Message message : getInstance().mainList) {
                    message.edgeStep(delay);
                }
                // toto by nemal byt foreach, lebo sa zoznam meni pocas behu
                for (int i = 0; i < getInstance().deadList.size(); ++i) {
                    getInstance().deadList.get(i).edgeStep(delay);
                }
            }

            getInstance().canvas.repaint();
            getInstance().model.graph.canvas.repaint();
            getInstance().timer.schedule(new StepEvent(), 30);
        }
    }

    synchronized void pushMessage(Message message) {
        bornList.addLast(message);
    }

    void queueMessage(Message message) {
        int index = 0;
        // TODO zrychlit
        for (int i = 0; i < mainList.size(); ++i) {
            if (mainList.get(i).edge == message.edge) {
                index = i;
            }
        }
        if (index < mainList.size()) {
            index = GUI.random.nextInt(mainList.size() - index) + index + 1;
        }
        mainList.add(index, message);
        message.born(index);
    }

    boolean swapMessages(int i, int j) {
        if (mainList.get(i).edge == mainList.get(j).edge) {
            return false;
        }
        Collections.swap(mainList, i, j);
        return true;
    }

    // zobudi frontu - pozor! pouziva sa aj pri zobudeni z pauzy, nie len pri
    // prvom starte
    void start() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        StepEvent.time = System.currentTimeMillis();
        MessageQueue.getInstance().timer.schedule(new MessageQueue.StepEvent(), 0);
        canvas.repaint();
    }

    void clear() {
        bornList.clear();
        mainList.clear();
        deadList.clear();
        canvas.repaint();
    }

    private synchronized void bornMessages() {
        for (Message message : bornList) {
            queueMessage(message);
        }
        bornList.clear();
    }

    public void step(long time) {
        bornMessages();
        bucketReduce();

        double expectedSize = 50.0;
        int messageCount = mainList.size() + deadList.size();
        if (messageCount * expectedSize > width) {
            expectedSize = width / messageCount;
        }
        zoom += (expectedSize - zoom) * (expectedSize < zoom ? 0.001 : 0.0004) * time;

        for (int i = 0; i < mainList.size(); ++i) {
            mainList.get(i).queueStep(time, i);
        }

    }

    private ArrayList<ArrayList<Message>> buckets;

    public void bucketReduce() {
        double maximalPosition = 0.0;
        for (Message m : mainList) {
            maximalPosition = Math.max(maximalPosition, m.qX);
        }
        buckets = new ArrayList<ArrayList<Message>>();
        for (int i = 0; i < (int) maximalPosition + 3; ++i) {
            buckets.add(new ArrayList<Message>());
        }
        for (Message m : mainList) {
            getBucket(m.qX).add(m);
        }
    }

    public ArrayList<Message> getBucket(double qx) {
        return buckets.get((int) qx + 1);
    }

    @Override
    public void draw(Graphics g) {
        offset = deadWidth * zoom;
        this.width = canvas.getWidth();
        this.height = canvas.getHeight();
        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, width, height);
        g.setColor(new Color(0, 0, 0));
        g.drawRect(0, 0, width - 1, height - 1);

        g.fillRect((int) offset - 1, CONST.queueHeight - 20, 2, 20);

        /* for (int i = 0; i < deadList.size(); i++) {
         * deadList.get(i).queueDraw(g, double offset, double zoom); } */
        for (int i = 0; i < mainList.size(); i++) {
            mainList.get(i).queueDraw(g, offset, zoom);
        }
    }

    Message getMessage(double x, double y) {
        // zive maju prednost
        for (Message message : mainList) {
            if (message.state == MessageState.main && message.isOnPoint(x, y)) {
                return message;
            }
        }
        for (Message message : mainList) {
            if (message.isOnPoint(x, y)) {
                return message;
            }
        }
        return null;
    }

    class QueueListener implements MouseListener, MouseMotionListener, MouseWheelListener {

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            int ticks = e.getWheelRotation();
            double scale = 1 - ticks * 0.05;
            zoom *= scale;
            canvas.repaint();
        }

        @Override
        public void mouseDragged(MouseEvent mouse) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseMoved(MouseEvent mouse) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseClicked(MouseEvent mouse) {
            Message message = getMessage(mouseGetX(mouse), mouseGetY(mouse));
            if (message == null) {
                return;
            }
            message.onClick();
        }

        @Override
        public void mouseEntered(MouseEvent mouse) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseExited(MouseEvent mouse) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mousePressed(MouseEvent mouse) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseReleased(MouseEvent mouse) {
            // TODO Auto-generated method stub

        }
    }

    double mouseGetX(MouseEvent mouse) {
        return (mouse.getX() - offset) / zoom;
    }

    double mouseGetY(MouseEvent mouse) {
        return (CONST.queueHeight - mouse.getY()) / zoom;
    }
}
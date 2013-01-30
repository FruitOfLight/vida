import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Fronta pre správy
 * 
 * každý odoslaný list sa zaradí do fronty, tam chvíľu pobudne a keď sa dostane
 * na začiatok, doručí sa
 * 
 * neposielajú sa priamo správy, ale listy
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
        canvas = new Canvas(this);
        setSendSpeed(1.2);
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public void setPosition(int width, int height) {
        this.width = width;
        this.height = height;
    }

    private double sendSpeed;

    public void setSendSpeed(double value) {
        sendSpeed = value;

    }

    public double getSendSpeed() {
        return sendSpeed;
    }

    public double getRealSendSpeed() {
        return sendSpeed * zoom;
    }

    private LinkedList<Message> bornList = new LinkedList<Message>();
    ArrayList<Message> mainList = new ArrayList<Message>();
    ArrayList<Message> deadList = new ArrayList<Message>();

    static class StepEvent extends TimerTask {
        static long time = 0;

        public void run() {
            if (getInstance().model == null || getInstance().model.running == RunState.stopped)
                return;
            long prevTime = time;
            time = System.currentTimeMillis();
            long delay = time - prevTime;

            // Spravy vo fronte
            MessageQueue.getInstance().step(delay);

            if (getInstance().model.running == RunState.running) {
                // Spravy v grafe
                for (Message message : getInstance().mainList)
                    message.edgeStep(delay);
                // toto by nemal byt foreach, lebo sa zoznam meni pocas behu
                for (int i = 0; i < getInstance().deadList.size(); ++i)
                    getInstance().deadList.get(i).edgeStep(delay);
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
        for (int i = 0; i < mainList.size(); ++i)
            if (mainList.get(i).edge == message.edge)
                index = i;
        if (index < mainList.size())
            index = GUI.random.nextInt(mainList.size() - index) + index + 1;
        mainList.add(index, message);
        message.born(index);
    }

    // zobudi frontu - pozor! pouziva sa aj pri zobudeni z pauzy, nie len pri
    // prvom starte
    void start() {
        if (timer != null)
            timer.cancel();
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

    double zoom = 50.0;
    static final double deadWidth = 1.0;

    private synchronized void bornMessages() {
        for (Message message : bornList) {
            queueMessage(message);
        }
        bornList.clear();
    }

    public void step(long time) {
        bornMessages();

        double expectedSize = 50.0;
        int messageCount = mainList.size() + deadList.size();
        if (messageCount * expectedSize > width)
            expectedSize = width / messageCount;
        zoom += (expectedSize - zoom) * ((expectedSize < zoom) ? 0.001 : 0.0004) * time;

        for (int i = 0; i < mainList.size(); ++i) {
            mainList.get(i).queueStep(time, i);
        }

    }

    public void draw(Graphics g) {
        this.width = canvas.getWidth();
        this.height = canvas.getHeight();
        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, width, height);
        g.setColor(new Color(0, 0, 0));
        g.drawRect(0, 0, width - 1, height - 1);

        g.fillRect((int) (deadWidth * zoom) - 1, CONST.queueHeight - 20, 2, 20);

        /* for (int i = 0; i < deadList.size(); i++) {
         * deadList.get(i).queueDraw(g, double offset, double zoom); } */
        for (int i = 0; i < mainList.size(); i++) {
            mainList.get(i).queueDraw(g, deadWidth, zoom);
        }

    }
}
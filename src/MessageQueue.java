import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
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
    public static MessageQueue getInstance() {
        return instance;
    }

    private static MessageQueue instance = new MessageQueue();

    Model model;
    Timer timer;

    private long sendInterval;
    long nextSend = 0;
    private double sendSpeed;

    public void setSendSpeed(double value) {
        sendSpeed = value;
        sendInterval = (int) (1000.0 / sendSpeed);
    }

    public double getSendSpeed() {
        return sendSpeed;
    }

    static class QueueEvent extends TimerTask {
        public void run() {
            if (getInstance().model == null || getInstance().model.running != RunState.running)
                return;
            getInstance().deliverFirstMessage();
            getInstance().timer.schedule(new QueueEvent(), getInstance().sendInterval);
        }
    }

    static class StepEvent extends TimerTask {
        static long time = 0;

        public void run() {
            if (getInstance().model == null || getInstance().model.running != RunState.running)
                return;
            long prevTime = time;
            time = System.currentTimeMillis();
            long delay = time - prevTime;

            // Spravy vo fronte
            MessageQueue.getInstance().step(delay);

            // Spravy v grafe
            for (Message message : getInstance().list)
                message.step(delay);
            // toto by nemal byt foreach, lebo sa zoznam meni pocas behu
            for (int i = 0; i < getInstance().deadlist.size(); ++i)
                getInstance().deadlist.get(i).step(delay);

            getInstance().canvas.repaint();
            getInstance().model.graph.canvas.repaint();
            getInstance().timer.schedule(new StepEvent(), 10);
        }
    }

    private MessageQueue() {
        canvas = new Canvas(this);
        setSendSpeed(1.2);
    }

    ArrayList<Message> list = new ArrayList<Message>();
    ArrayList<Message> deadlist = new ArrayList<Message>();
    // premenne pre vykreslovanie
    Canvas canvas;
    int width, height;

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public void setPosition(int width, int height) {
        this.width = width;
        this.height = height;
    }

    void pushMessage(Message message) {
        list.add(message);
        refreshRecieveness();
        canvas.repaint();
    }

    void deliverFirstMessage() {
        if (list.size() <= 0)
            return;
        Message message = list.get(0);
        list.remove(0);
        if (message.edge.to.program == null || message.edge.to.program.running == false) {
            System.err.println("Recipient doesn't exist\n  message was delayed\n");
            // TODO pozor, aby sa nemenilo poradie na hrane
            list.add(message);
            list.remove(0);
            return;
        }
        deadlist.add(message);
        // message.edge.to.receive(message);
        nextSend = System.currentTimeMillis() + sendInterval;
        refreshRecieveness();
        canvas.repaint();
    }

    void refreshRecieveness() {
        for (int i = 0; i < list.size(); i++)
            list.get(i).setRecieveness(nextSend + i * sendInterval);
        for (int i = 0; i < deadlist.size(); i++)
            deadlist.get(i).setRecieveness(-1);
    }

    // zobudi frontu - pozor! pouziva sa aj pri zobudeni z pauzy, nie len pri
    // prvom starte
    void start() {
        if (timer!=null) timer.cancel();
        timer = new Timer();
        StepEvent.time = System.currentTimeMillis();
        nextSend = System.currentTimeMillis() + sendInterval;
        refreshRecieveness();
        MessageQueue.getInstance().timer.schedule(new MessageQueue.QueueEvent(), sendInterval);
        MessageQueue.getInstance().timer.schedule(new MessageQueue.StepEvent(), 0);
        canvas.repaint();
        
    }

    void clear() {
        list.clear();
        deadlist.clear();
        canvas.repaint();
    }

    double size = 50;
    double expectedSize = 50;

    public void step(long time) {
        expectedSize = 50.0;
        int messageCount = list.size() + deadlist.size() + 1;
        if (messageCount * expectedSize > width)
            expectedSize = width / messageCount;
        size += (expectedSize - size) * ((expectedSize < size) ? 0.001 : 0.0001) * time;
    }

    public void draw(Graphics g) {
        this.width = canvas.getWidth();
        this.height = canvas.getHeight();
        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, width, height);
        g.setColor(new Color(0, 0, 0));
        g.drawRect(0, 0, width - 1, height - 1);
        
        for (int i = 0; i < deadlist.size(); i++) {
            deadlist.get(i).queueDraw(g, 5 + (size * i), size);
        }
        int deadsize = deadlist.size();
        for (int i = 0; i < list.size(); i++) {
            list.get(i).queueDraw(g, 5 + (size * i+deadsize), size);
        }
        g.fillRect((int) (size * deadlist.size()), CONST.queueHeight - 10, 10, 5);
    }
}
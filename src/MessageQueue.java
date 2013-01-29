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

    private long sendInterval;
    private long nextSend = 0;
    private double sendSpeed;

    public void setSendSpeed(double value) {
        sendSpeed = value;
        sendInterval = (int) (1000.0 / sendSpeed);
        refreshRecieveness();
    }

    public double getSendSpeed() {
        return sendSpeed;
    }

    ArrayList<Message> sleepList = new ArrayList<Message>();
    ArrayList<Message> bornList = new ArrayList<Message>();
    ArrayList<Message> mainList = new ArrayList<Message>();
    ArrayList<Message> deadList = new ArrayList<Message>();

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
            for (Message message : getInstance().mainList)
                message.edgeStep(delay);
            // toto by nemal byt foreach, lebo sa zoznam meni pocas behu
            for (int i = 0; i < getInstance().deadList.size(); ++i)
                getInstance().deadList.get(i).edgeStep(delay);

            getInstance().canvas.repaint();
            getInstance().model.graph.canvas.repaint();
            getInstance().timer.schedule(new StepEvent(), 30);
        }
    }

    void pushMessage(Message message) {
        mainList.add(message);
        refreshRecieveness();
        canvas.repaint();
    }

    void deliverFirstMessage() {
        if (mainList.size() <= 0)
            return;
        Message message = mainList.get(0);
        mainList.remove(0);
        if (message.edge.to.program == null || message.edge.to.program.running == false) {
            System.err.println("Recipient doesn't exist\n  message was delayed\n");
            // TODO pozor, aby sa nemenilo poradie na hrane
            mainList.add(message);
            mainList.remove(0);
            return;
        }
        deadList.add(message);
        // message.edge.to.receive(message);
        nextSend = System.currentTimeMillis() + sendInterval;
        refreshRecieveness();
        canvas.repaint();
    }

    void refreshRecieveness() {
        for (int i = 0; i < mainList.size(); i++)
            mainList.get(i).setRecieveness(nextSend + i * sendInterval);
        for (int i = 0; i < deadList.size(); i++)
            deadList.get(i).setRecieveness(-1);
    }

    // zobudi frontu - pozor! pouziva sa aj pri zobudeni z pauzy, nie len pri
    // prvom starte
    void start() {
        if (timer != null)
            timer.cancel();
        timer = new Timer();
        StepEvent.time = System.currentTimeMillis();
        nextSend = System.currentTimeMillis() + sendInterval;
        refreshRecieveness();
        MessageQueue.getInstance().timer.schedule(new MessageQueue.QueueEvent(), sendInterval);
        MessageQueue.getInstance().timer.schedule(new MessageQueue.StepEvent(), 0);
        canvas.repaint();
    }

    void clear() {
        mainList.clear();
        deadList.clear();
        canvas.repaint();
    }

    double size = 50;
    double expectedSize = 50;

    public void step(long time) {
        expectedSize = 50.0;
        int messageCount = mainList.size() + deadList.size() + 1;
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
        try {
            for (int i = 0; i < deadList.size(); i++) {
                deadList.get(i).queueDraw(g, 5 + (size * i), size);
            }
            int deadsize = deadList.size();
            for (int i = 0; i < mainList.size(); i++) {
                mainList.get(i).queueDraw(g, 5 + (size * i + deadsize), size);
            }
        } catch (Exception e) {
            e.printStackTrace();
            draw(g);
        }
        g.fillRect((int) (size * deadList.size()), CONST.queueHeight - 10, 10, 5);
    }
}
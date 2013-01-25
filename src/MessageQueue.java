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
    
    long sendInterval = 1000;
    long nextSend = 0;

    static class QueueEvent extends TimerTask {
        public void run() {
            if (getInstance().model == null)
                return;
            if (!getInstance().model.running)
                return;
            getInstance().deliverFirstMessage();            
            getInstance().timer.schedule(new QueueEvent(), getInstance().sendInterval);
        }
    }

    static class MessageDrawEvent extends TimerTask {
        static long time = 0;

        public void run() {
            if (getInstance().model == null)
                return;
            if (!getInstance().model.running)
                return;
            long prevTime = time;
            time = System.currentTimeMillis();
            long delay = time - prevTime;
            for (Message message : getInstance().list)
                message.step(delay);
            // toto by nemal byt foreach, lebo sa zoznam meni pocas behu
            for (int i = 0; i < getInstance().deadlist.size(); ++i)
                getInstance().deadlist.get(i).step(delay);

            getInstance().model.graph.canvas.repaint();
            getInstance().timer.schedule(new MessageDrawEvent(), 10);
        }
    }

    private MessageQueue() {
        timer = new Timer();
        canvas = new Canvas(this);
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
        if (message.edge.to.program == null
                || message.edge.to.program.running == false) {
            System.err
                    .println("Recipient doesn't exist\n  message was delayed\n");
            // TODO pozor, aby sa nemenilo poradie na hrane
            list.add(message);
            list.remove(0);
            return;
        }
        deadlist.add(message);
        //message.edge.to.receive(message);
        nextSend = System.currentTimeMillis()+sendInterval;
        refreshRecieveness();
        canvas.repaint();
    }

    void refreshRecieveness() {
        for (int i = 0; i < list.size(); i++)
            list.get(i).setRecieveness(nextSend + i*sendInterval);
        for (int i = 0; i < deadlist.size(); i++)
            deadlist.get(i).setRecieveness(-1);
    }

    void start() {
        MessageDrawEvent.time = System.currentTimeMillis();
        nextSend = System.currentTimeMillis()+sendInterval;
        MessageQueue.getInstance().timer.schedule(
                new MessageQueue.QueueEvent(), sendInterval);
        MessageQueue.getInstance().timer.schedule(
                new MessageQueue.MessageDrawEvent(), 1);
    }

    void clear() {
        list.clear();
        canvas.repaint();
    }

    public void draw(Graphics g) {
        this.width = canvas.getWidth();
        this.height = canvas.getHeight();
        // TODO
        g.setColor(new Color(0, 0, 0));
        g.drawRect(0, 0, width - 1, height - 1);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).queueDraw(g, i);
        }
    }
}
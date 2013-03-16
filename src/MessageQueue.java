import java.awt.Graphics2D;
import java.util.LinkedList;

/**
 * Fronta pre správy
 * 
 */
public class MessageQueue implements Drawable {
    private LinkedList<Message> list;
    private LinkedList<Message> bornlist;
    public static int messageCount = 0;

    MessageQueue() {
        list = new LinkedList<Message>();
        bornlist = new LinkedList<Message>();
    }

    synchronized public void step(long time) {
        bornMessages();
        while (list.size() > 0 && list.peek().state == DeliverState.dead) {
            list.pop();
            messageCount--;
        }
        for (Message message : list)
            message.edgeStep(time);
    }

    @Override
    synchronized public void draw(Graphics2D g) {
        for (Message message : list)
            message.edgeDraw(g);
    }

    public void clear() {
        list.clear();
        bornlist.clear();
        messageCount = 0;
    }

    synchronized public void pushMessage(Message message) {
        bornlist.add(message);
        message.state = DeliverState.alive;
        messageCount++;
    }

    public void bornMessages() {
        while (bornlist.size() > 0)
            list.add(bornlist.pop());
    }

}

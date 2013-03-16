import java.awt.Graphics2D;
import java.util.LinkedList;

/**
 * Fronta pre správy
 * 
 */
public class EdgeQueue implements Drawable {
    private LinkedList<Message> list;
    private LinkedList<Message> bornlist;

    EdgeQueue() {
        list = new LinkedList<Message>();
        bornlist = new LinkedList<Message>();
    }

    synchronized public void step(long time) {
        bornMessages();
        while (list.size() > 0 && list.peek().state == DeliverState.dead)
            list.pop();
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
    }

    synchronized public void pushMessage(Message message) {
        bornlist.add(message);
        message.state = DeliverState.alive;
    }

    public void bornMessages() {
        while (bornlist.size() > 0)
            list.add(bornlist.pop());
    }

}

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

    public void step(long time) {
        updateMessages();
        if (list.size() > 0) {
            double defdist = 1.0 / list.size();
            Message prevMessage = null;
            for (Message message : list) {
                message.defDist = defdist;
                message.prevM = prevMessage;
                if (prevMessage != null)
                    prevMessage.nextM = message;
                prevMessage = message;
            }
            prevMessage.nextM = null;

            for (Message message : list) {
                message.measure(time);
            }
            for (Message message : list) {
                message.move(time);
            }

            for (Message message : list) {
                if (message.state == DeliverState.delivered)
                    continue;
                if (message.state == DeliverState.inbox) {
                    message.edge.to.receive(message);
                } else {
                    break;
                }
            }
        }
    }

    @Override
    synchronized public void draw(Graphics2D g) {
        for (Message message : list)
            message.edgeDraw(g);
    }

    synchronized public void clear() {
        list.clear();
        bornlist.clear();
    }

    public void pushMessage(Message message) {
        bornlist.add(message);
        message.state = DeliverState.alive;
        messageCount++;
    }

    synchronized public void updateMessages() {
        while (bornlist.size() > 0)
            list.add(bornlist.pop());
        while (list.size() > 0 && list.peek().state == DeliverState.delivered) {
            list.pop();
            messageCount--;
        }
    }

}

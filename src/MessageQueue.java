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
            Message prevMessage = null;
            double defdist = 0.3 / list.size();
            for (Message message : list) {
                double p = Math.pow(0.5, time * 0.001);
                message.eSpeed = message.eSpeed * (1.0 - p) + message.defSpeed * p;
                if (prevMessage != null) {
                    double q = prevMessage.ePosition - message.ePosition;
                    if (q < 0) {
                        message.eSpeed = 0.0;
                    } else if (q < defdist) {
                        message.eSpeed /= 1 + (defdist - q);
                    }
                }
                message.edgeStep(time);
                prevMessage = message;
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

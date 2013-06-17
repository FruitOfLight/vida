package graph;

import java.awt.Graphics2D;
import java.util.LinkedList;

import ui.Drawable;
import enums.DeliverState;

/**
 * Fronta pre správy, kazda hrana ma vlastnu stara sa hlavne o dve veci: aby sa spravy nepredbiehali
 * aby sa to nezrubalo pri viacnasobnom pristupovani
 */
public class MessageQueue implements Drawable {
    private LinkedList<Message> list;
    private LinkedList<Message> bornlist;
    public static int messageCount = 0;

    MessageQueue() {
        list = new LinkedList<Message>();
        bornlist = new LinkedList<Message>();
    }

    // vsetkym spravam necha predpocitat pohyb
    public void measure(long time) {
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
        }
    }

    // vsetky spravy necha pohnut sa
    public void move(long time) {
        if (list.size() > 0) {
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

    // vycisti sa, zmaze so seba vsetky spravy
    synchronized public void clear() {
        list.clear();
        bornlist.clear();
    }

    // prida novu spravu
    public void pushMessage(Message message) {
        bornlist.add(message);
        message.state = DeliverState.alive;
        messageCount++;
    }

    // preklopi spravy s cerstvo narodenych do zivych
    synchronized public void updateMessages() {
        while (bornlist.size() > 0)
            list.add(bornlist.pop());
        while (list.size() > 0 && list.peek().state == DeliverState.delivered) {
            list.pop();
            messageCount--;
        }
    }

    public LinkedList<Message> getMessages() {
        return list;
    }
}

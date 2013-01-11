import java.util.ArrayList;

/**
 * Fronta pre správy
 * 
 * každý odoslaný list sa zaradí do fronty, tam chvíľu pobudne a keď sa dostane
 * na začiatok, doručí sa
 * 
 * neposielajú sa priamo správy, ale listy
 */
public class LetterQueue {
    ArrayList<Letter> list = new ArrayList<Letter>();

    void processNewLetter(Letter letter) {
        list.add(letter);
    }

    void deliverFirstLetter() {
        // TODO
    }

    void draw(Canvas canvas) {
        // TODO
    }
}

class Letter {
    Edge edge;
    String content;

    public Letter(Edge edge, String content) {
        this.edge = edge;
        this.content = content;
    }

    public Letter(Vertex v, Message message) {
        this(v.edges.get(message.port), message.content);
    }
}
import java.awt.Graphics2D;
import java.io.PrintStream;

public class BFSAlgorithm implements Algorithm {

    Bubble generalInfo;

    public BFSAlgorithm() {
        this.defaultSettings();
    }

    public void defaultSettings() {
        generalInfo = new Bubble(10, 10);
        generalInfo.setLockedPosition(true);
        generalInfo.position = BubblePosition.SE;
        generalInfo.setMaxWidth(300);
        old = false;
        recieve = false;
    }

    public void print(PrintStream out) {
        out.println("BFS");
    }

    public void startAlgorithm() {
        generalInfo.addInformation(
                "At the begining of this algorithm, one process know new gossip."
                        + "He wants to share gossip with everyone else. That means,"
                        + "he send message to each neighbor.", -2);
        GUI.model.pause();
    }

    public void finishAlgorithm(Vertex v) {
        generalInfo.addInformation(
                "All processes known the new gossip. Total number of send messages is"
                        + GUI.model.overallMessageCount, -1);
        generalInfo.addInformation("Press 'R' to continue.", -1);
    }

    boolean old, recieve;

    public void recieveUpdate(Vertex vertex, String message) {
        if (message.contains("recieve") && !recieve) {
            GUI.globalTimer.schedule(new Model.AuraEvent(vertex, 7), 0);
            String[] values = message.split(":");
            generalInfo
                    .addInformation(
                            "Process with id "
                                    + values[1]
                                    + " recieve new gossip. He wants to spread it, so he send gossip to each neighbor"
                                    + " except one, who send him this gossip.", -2);
            recieve = true;
            GUI.model.pause();
        }
        if (message.contains("old") && !old) {
            GUI.globalTimer.schedule(new Model.AuraEvent(vertex, 7), 0);
            String[] values = message.split(":");
            generalInfo
                    .addInformation(
                            "Process "
                                    + values[1]
                                    + " recieve gossip. But he seen this gossip before."
                                    + " He ignore this message, because he send this gossip to his neighbor already.",
                            -2);
            old = true;
            GUI.model.pause();
        }
        GUI.controls.refresh();
        GUI.gRepaint();
    }

    public void draw(Graphics2D g) {
        generalInfo.draw(g);
    }

}

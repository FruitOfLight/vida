import java.awt.Graphics2D;
import java.util.ArrayList;

public class CliqueLEAlgorithm implements Algorithm {

    public InformationBubble generalInfo, levelInfo;
    public ArrayList<Integer> levelCounter;

    public CliqueLEAlgorithm() {
        generalInfo = new InformationBubble(10, 10, true);
        levelInfo = new InformationBubble(10, 200, true);
        levelCounter = new ArrayList<Integer>();
    }

    String[] start = { "When algorithm begins, all processes will try",
            "to capture all other processes. Each process has own ID",
            "and current level. Process is stronger than other, if it has bigger level",
            "or when levels are equal, bigger ID." };

    public void startAlgorithm() {
        for (int i = 0; i < GUI.graph.vertices.size(); i++)
            levelCounter.add(0);
        levelCounter.set(0, GUI.graph.vertices.size());
        for (int i = 0; i < start.length; i++)
            generalInfo.addInformation(start[i], -2);
        GUI.model.pause();
    }

    public void recieveMessage() {
    }

    public void draw(Graphics2D g) {
        generalInfo.draw(g);
        if (GUI.model.running != RunState.stopped) {
            levelInfo.informations = new ArrayList<InformationBubble.Information>();
            levelInfo.addInformation("Number of process on", -1);
            for (int i = 0; i < GUI.graph.vertices.size(); i++) {
                levelInfo.addInformation("level " + ((Integer) i).toString() + ": "
                        + levelCounter.get(i).toString(), -1);
            }
        }
        levelInfo.draw(g);
    }
}
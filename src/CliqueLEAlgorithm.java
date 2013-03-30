import java.awt.Graphics2D;
import java.util.ArrayList;

public class CliqueLEAlgorithm implements Algorithm {

    public InformationBubble generalInfo, levelInfo;
    public ArrayList<Integer> levelCounter;

    public CliqueLEAlgorithm() {
        generalInfo = new InformationBubble(10, 10, true);
        levelInfo = new InformationBubble(10, 200, true);
        levelCounter = new ArrayList<Integer>();
        captureActive = false;
        captureCapture = false;
        helpWin = false;
        helpDefeat = false;
        accept = false;
        defeat = false;
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

    boolean captureActive;
    boolean captureCapture;
    boolean helpWin;
    boolean helpDefeat;
    boolean accept;
    boolean defeat;

    public void recieveUpdate(Vertex vertex, String s) {
        System.out.println(s);
        if (s.contains("level")) {
            int level = Integer.parseInt(s.substring(6).trim());
            levelCounter.set(level, levelCounter.get(level) + 1);
        }
        if (s.contains("capture-active") && !captureActive) {
            GUI.globalTimer.schedule(new Model.AuraEvent(vertex, 7), 0);
            String values[] = s.split(":");
            generalInfo.addInformation("Process with level " + values[3] + " and id " + values[4]
                    + " is trying", -2);
            generalInfo.addInformation("to capture active process with level " + values[1]
                    + " and id " + values[2] + ".", -2);
            generalInfo.addInformation(
                    "Defender is defeated so it sends acceptance message to attacker.", -2);
            captureActive = true;
            GUI.model.pause();
        }
        if (s.contains("capture-capture") && !captureCapture) {
            GUI.globalTimer.schedule(new Model.AuraEvent(vertex, 7), 0);
            String values[] = s.split(":");
            generalInfo.addInformation("Process with id " + values[3]
                    + " is attacked by process with level " + values[1] + " and id " + values[2]
                    + ".", -2);
            generalInfo.addInformation(
                    "But this process is already captured by another process with id " + values[4]
                            + ".", -2);
            generalInfo.addInformation("So it sends message to its leader for help.", -2);
            captureCapture = true;
            GUI.model.pause();
        }
        if (s.contains("help-win") && !helpWin) {
            GUI.globalTimer.schedule(new Model.AuraEvent(vertex, 7), 0);
            String values[] = s.split(":");
            generalInfo.addInformation("Process with level " + values[1] + " and id " + values[2]
                    + " recieve help message from its subordinate.", -2);
            generalInfo.addInformation("Subordinate is attacked by process with level " + values[3]
                    + " and id " + values[4] + ".", -2);
            generalInfo.addInformation("However, process " + values[2]
                    + " is stronger, so it sends message to subordinate, that it can ignore "
                    + values[4] + ".", -2);
            helpWin = true;
            GUI.model.pause();
        }
        if (s.contains("help-defeat") && !helpDefeat) {
            GUI.globalTimer.schedule(new Model.AuraEvent(vertex, 7), 0);
            String values[] = s.split(":");
            generalInfo.addInformation("Process with level " + values[1] + " and id " + values[2]
                    + " recieve help message from its subordinate.", -2);
            generalInfo.addInformation("Subordinate is attacked by process with level " + values[3]
                    + " and id " + values[4] + ".", -2);
            generalInfo
                    .addInformation(
                            "However, process "
                                    + values[2]
                                    + " is weaker, so it is killed and it sends message to subordinate to surrender.",
                            -2);
            helpDefeat = true;
            GUI.model.pause();
        }
        if (s.contains("accept") && !accept) {
            GUI.globalTimer.schedule(new Model.AuraEvent(vertex, 7), 0);
            String values[] = s.split(":");
            generalInfo.addInformation("Process " + values[1]
                    + " won battle, it gain new subordinate and go to level " + values[2], -2);
            accept = true;
            GUI.model.pause();
        }
        if (s.contains("Defeat") && !defeat) {
            GUI.globalTimer.schedule(new Model.AuraEvent(vertex, 7), 0);
            String values[] = s.split(":");
            generalInfo
                    .addInformation("Leader of process " + values[1]
                            + " is defeated. New leader for this process is process " + values[2]
                            + ".", -2);
            defeat = true;
            GUI.model.pause();
        }
        GUI.gRepaint();
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
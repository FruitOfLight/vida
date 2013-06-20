package algorithm;

import enums.RunState;
import graph.Bubble;
import graph.Vertex;

import java.util.ArrayList;

import model.Player;
import ui.GUI;

public class CliqueLEObserver extends Observer {

    Bubble levelInfo, lastLevelInfo;
    public ArrayList<Integer> levelCounter;

    public CliqueLEObserver(Player player) {
        super(player);
    }

    @Override
    public void inicializeAllBubbles() {
        super.inicializeAllBubbles();
        levelInfo = inicializeBubble(new Bubble(10, 200), 150);
        lastLevelInfo = inicializeBubble(new Bubble(10, 200), 150);
        levelCounter = new ArrayList<Integer>();
    }

    @Override
    public void onStart() {
        for (int i = 0; i < player.graph.vertices.size(); i++)
            levelCounter.add(0);
        levelCounter.set(0, player.graph.vertices.size());
        generalInfo
                .addInformation(
                        "When algorithm begins, all processes will try to capture all other processes. Each process has own ID and current level. Process is stronger than other, if it has bigger level or when levels are equal, bigger ID.",
                        -2);
        player.pause();
    }

    @Override
    public void onFinish() {
        Vertex leader = null;
        for (Vertex v : player.graph.vertices)
            if (v.getVariable("leader").equals("yes"))
                leader = v;
        if (leader == null)
            return;
        generalInfo
                .addInformation(
                        "Leader is process with ID "
                                + leader.getID()
                                + ". Total number of send messages is "
                                + player.model.overallMessageCount
                                + ". Algorithm should send O(N logN) messages, where N is number of vertices."
                                + "If we want verify effectivity of our algorithm notice this thing. On each level L,"
                                + "the number of active processes on that level is at most N/(L+1), because"
                                + " each vertex needs own unique set of L subordinates. And for each vertex, we"
                                + "need only constant number of messages to get to another level, or get killed."
                                + "This means, that total number of send message is equal (in O-notation) to sum N/(L+1) for L from 1 to"
                                + +player.graph.vertices.size()
                                + ". So thats N times harmonic number. Whats in roughly logN. So complexity is really O(N logN). "
                                + "Press key 'R' to finish algorithm.", -1);
        levelInfo.transparency = 0f;
        lastLevelInfo.addInformation(
                "Number of process / maximal possible number of process on level:", -1);
        for (int i = 0; i < levelCounter.size(); i++) {
            lastLevelInfo.addInformation(
                    i + ": " + levelCounter.get(i) + " / "
                            + (int) Math.floor(levelCounter.size() / (double) (i + 1)), -1);
        }
    }

    @Override
    public void onUpdate(Vertex vertex, String s) {
        if (s.contains("level")) {
            int level = Integer.parseInt(s.substring(6).trim());
            levelCounter.set(level, levelCounter.get(level) + 1);
        }
    }

    @Override
    public void onEvent(Vertex vertex, String s) {
        System.out.println(s);
        if (matchNotification(s, "capture-active")) {
            GUI.globalTimer.schedule(new Player.AuraEvent(vertex, 7), 0);
            String values[] = s.split(":");
            generalInfo.addInformation("Process with level " + values[3] + " and id " + values[4]
                    + " is trying to capture active process with level " + values[1] + " and id "
                    + values[2]
                    + ". Defender is defeated so it sends acceptance message to attacker.", -2);
            player.pause();
        }
        if (matchNotification(s, "capture-capture")) {
            GUI.globalTimer.schedule(new Player.AuraEvent(vertex, 7), 0);
            String values[] = s.split(":");
            generalInfo.addInformation("Process with id " + values[3]
                    + " is attacked by process with level " + values[1] + " and id " + values[2]
                    + ". But this process is already captured by another process with id "
                    + values[4] + ". So it sends message to its leader for help.", -2);
            player.pause();
        }
        if (matchNotification(s, "help-win")) {
            GUI.globalTimer.schedule(new Player.AuraEvent(vertex, 7), 0);
            String values[] = s.split(":");
            generalInfo
                    .addInformation(
                            "Process with level "
                                    + values[1]
                                    + " and id "
                                    + values[2]
                                    + " recieve help message from its subordinate. Subordinate is attacked by process with level "
                                    + values[3]
                                    + " and id "
                                    + values[4]
                                    + ". However, process "
                                    + values[2]
                                    + " is stronger, so it sends message to subordinate, that it can ignore "
                                    + values[4] + ".", -2);
            player.pause();
        }
        if (matchNotification(s, "help-defeat")) {
            GUI.globalTimer.schedule(new Player.AuraEvent(vertex, 7), 0);
            String values[] = s.split(":");
            generalInfo
                    .addInformation(
                            "Process with level "
                                    + values[1]
                                    + " and id "
                                    + values[2]
                                    + " recieve help message from its subordinate. Subordinate is attacked by process with level "
                                    + values[3]
                                    + " and id "
                                    + values[4]
                                    + ". However, process "
                                    + values[2]
                                    + " is weaker, so it is killed and it sends message to subordinate to surrender.",
                            -2);
            player.pause();
        }
        if (matchNotification(s, "accept")) {
            GUI.globalTimer.schedule(new Player.AuraEvent(vertex, 7), 0);
            String values[] = s.split(":");
            generalInfo.addInformation("Process " + values[1]
                    + " won battle, it gain new subordinate and go to level " + values[2], -2);
            player.pause();
        }
        if (matchNotification(s, "Defeat")) {
            GUI.globalTimer.schedule(new Player.AuraEvent(vertex, 7), 0);
            String values[] = s.split(":");
            generalInfo
                    .addInformation("Leader of process " + values[1]
                            + " is defeated. New leader for this process is process " + values[2]
                            + ".", -2);
            player.pause();
        }
        //GUI.gRepaint();
    }

    @Override
    public void step(long time) {
        if (player.state != RunState.stopped) {
            levelInfo.defaultSettings();
            levelInfo.addInformation("Number of process on", -1);
            for (int i = 0; i < player.graph.vertices.size(); i++) {
                levelInfo.addInformation("level " + ((Integer) i).toString() + ": "
                        + levelCounter.get(i).toString(), -1);
            }
        }
    }

}
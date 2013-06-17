package algorithms;

import enums.BubblePosition;
import enums.RunState;
import graph.Bubble;
import graph.Vertex;

import java.awt.Graphics2D;
import java.io.PrintStream;
import java.util.ArrayList;

import ui.GUI;

public class CliqueLEAlgorithm implements Algorithm {

    public Bubble generalInfo, levelInfo, lastLevelInfo;
    Player player;
    public ArrayList<Integer> levelCounter;

    public CliqueLEAlgorithm() {
        player = GUI.player;
        this.defaultSettings();
        //GUI.model.setPath(getPath());
    }

    public String getPath() {
        return "./algorithms/leaderElectionCliqueNlogN.cpp";
    }

    public void print(PrintStream out) {
        out.println("LECNlogN");
    }

    public void defaultSettings() {
        generalInfo = new Bubble(10, 10);
        generalInfo.setLockedPosition(true);
        generalInfo.setMaxWidth(300);
        generalInfo.position = BubblePosition.SE;
        levelInfo = new Bubble(10, 200);
        levelInfo.setLockedPosition(true);
        levelInfo.position = BubblePosition.SE;
        lastLevelInfo = new Bubble(10, 200);
        lastLevelInfo.setLockedPosition(true);
        lastLevelInfo.position = BubblePosition.SE;
        lastLevelInfo.setMaxWidth(150);
        levelCounter = new ArrayList<Integer>();
        captureActive = false;
        captureCapture = false;
        helpWin = false;
        helpDefeat = false;
        accept = false;
        defeat = false;
    }

    public void startAlgorithm() {
        for (int i = 0; i < player.graph.vertices.size(); i++)
            levelCounter.add(0);
        levelCounter.set(0, player.graph.vertices.size());
        generalInfo
                .addInformation(
                        "When algorithm begins, all processes will try to capture all other processes. Each process has own ID and current level. Process is stronger than other, if it has bigger level or when levels are equal, bigger ID.",
                        -2);
        player.pause();
    }

    public void finishAlgorithm(Vertex leader) {
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

    boolean captureActive;
    boolean captureCapture;
    boolean helpWin;
    boolean helpDefeat;
    boolean accept;
    boolean defeat;

    public void recieveUpdate(Vertex vertex, String s) {
        //System.out.println(s);
        if (s.contains("level")) {
            int level = Integer.parseInt(s.substring(6).trim());
            levelCounter.set(level, levelCounter.get(level) + 1);
        }
        if (s.contains("capture-active") && !captureActive) {
            GUI.globalTimer.schedule(new Player.AuraEvent(vertex, 7), 0);
            String values[] = s.split(":");
            generalInfo.addInformation("Process with level " + values[3] + " and id " + values[4]
                    + " is trying to capture active process with level " + values[1] + " and id "
                    + values[2]
                    + ". Defender is defeated so it sends acceptance message to attacker.", -2);
            captureActive = true;
            player.pause();
        }
        if (s.contains("capture-capture") && !captureCapture) {
            GUI.globalTimer.schedule(new Player.AuraEvent(vertex, 7), 0);
            String values[] = s.split(":");
            generalInfo.addInformation("Process with id " + values[3]
                    + " is attacked by process with level " + values[1] + " and id " + values[2]
                    + ". But this process is already captured by another process with id "
                    + values[4] + ". So it sends message to its leader for help.", -2);
            captureCapture = true;
            player.pause();
        }
        if (s.contains("help-win") && !helpWin) {
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
            helpWin = true;
            player.pause();
        }
        if (s.contains("help-defeat") && !helpDefeat) {
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
            helpDefeat = true;
            player.pause();
        }
        if (s.contains("accept") && !accept) {
            GUI.globalTimer.schedule(new Player.AuraEvent(vertex, 7), 0);
            String values[] = s.split(":");
            generalInfo.addInformation("Process " + values[1]
                    + " won battle, it gain new subordinate and go to level " + values[2], -2);
            accept = true;
            player.pause();
        }
        if (s.contains("Defeat") && !defeat) {
            GUI.globalTimer.schedule(new Player.AuraEvent(vertex, 7), 0);
            String values[] = s.split(":");
            generalInfo
                    .addInformation("Leader of process " + values[1]
                            + " is defeated. New leader for this process is process " + values[2]
                            + ".", -2);
            defeat = true;
            player.pause();
        }
        GUI.controls.refresh();
        GUI.gRepaint();
    }

    public void draw(Graphics2D g) {
        generalInfo.draw(g);
        if (player.running != RunState.stopped) {
            levelInfo.defaultSettings();
            levelInfo.addInformation("Number of process on", -1);
            for (int i = 0; i < player.graph.vertices.size(); i++) {
                levelInfo.addInformation("level " + ((Integer) i).toString() + ": "
                        + levelCounter.get(i).toString(), -1);
            }
        }
        levelInfo.draw(g);
        lastLevelInfo.draw(g);
    }
}
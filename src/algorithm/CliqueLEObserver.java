package algorithm;

import enums.RunState;
import graph.Bubble;
import graph.Vertex;

import java.util.ArrayList;

import model.Player;
import ui.GUI;
import ui.TextBook;

public class CliqueLEObserver extends Observer {

    Bubble levelInfo, lastLevelInfo;
    public ArrayList<Integer> levelCounter;

    public CliqueLEObserver(Player player) {
        super(player);
        textBook = new TextBook("cliqueLE-observer");
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
        generalInfo.addInformation(textBook.getText("start"), -2);
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
        Object[] values = { leader.getID(), player.model.overallMessageCount,
                player.graph.vertices.size() };
        generalInfo.addInformation(textBook.getText("finish", values), -1);
        levelInfo.transparency = 0f;
        lastLevelInfo.addInformation(textBook.getText("process_number_end"), -1);
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
        String[] help = s.split(":");
        Object[] values = new Object[help.length - 1];
        for (int i = 1; i < help.length; i++)
            values[i - 1] = help[i];
        String event = help[0];
        if (!firstTime(event))
            return;
        GUI.globalTimer.schedule(new Player.AuraEvent(vertex, 7), 0);
        generalInfo.addInformation(textBook.getText(event, values), -2);
        player.pause();
    }

    @Override
    public void step(long time) {
        if (player.state != RunState.stopped) {
            levelInfo.defaultSettings();
            levelInfo.addInformation(textBook.getText("process_number"), -1);
            for (int i = 0; i < player.graph.vertices.size(); i++) {
                levelInfo.addInformation(textBook.getText("level") + ((Integer) i).toString()
                        + ": " + levelCounter.get(i).toString(), -1);
            }
        }
    }
}
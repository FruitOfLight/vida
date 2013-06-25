package algorithm;

import graph.Vertex;
import model.Player;
import ui.GUI;
import ui.TextBook;

public class BFSObserver extends Observer {

    public BFSObserver(Player player) {
        super(player);
        textBook = new TextBook("bfs-observer");
    }

    @Override
    public void onStart() {
        player.pause();
        generalInfo.addInformation(textBook.getText("start"), -2);
    }

    @Override
    public void onFinish() {
        Object[] value = { player.model.overallMessageCount };
        generalInfo.addInformation(textBook.getText("finish", value), -1);
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
        player.pause();
        generalInfo.addInformation(textBook.getText(event, values), -2);
    }
}

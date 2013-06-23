package algorithm;

import graph.Vertex;
import model.Player;
import ui.GUI;
import ui.TextBook;

public class BFSObserver extends Observer {

    public BFSObserver(Player player) {
        super(player);
    }

    @Override
    public void onStart() {
        generalInfo.addInformation(TextBook.getLanguageString("bfs-start"), -2);
        player.pause();
    }

    @Override
    public void onFinish() {
        Object[] value = { player.model.overallMessageCount };
        generalInfo.addInformation(TextBook.getLanguageString("bfs-finish", value), -1);
    }

    @Override
    public void onEvent(Vertex vertex, String s) {
        String[] ids = TextBook.getLanguageArray("bfs-event-id");
        String[] texts = TextBook.getLanguageArray("bfs-event");
        if (matchNotification(s, "recieve")) {
            GUI.globalTimer.schedule(new Player.AuraEvent(vertex, 7), 0);
            String[] values = s.split(":");
            generalInfo.addInformation(TextBook.getMatchedString(ids, texts, "recieve",
                    TextBook.editValues(values, 1)), -2);
            player.pause();
        }
        if (matchNotification(s, "old")) {
            GUI.globalTimer.schedule(new Player.AuraEvent(vertex, 7), 0);
            String[] values = s.split(":");
            generalInfo.addInformation(
                    TextBook.getMatchedString(ids, texts, "old", TextBook.editValues(values, 1)),
                    -2);
            player.pause();
        }
    }
}

package algorithm;

import graph.Vertex;
import model.Player;
import ui.GUI;

public class BFSObserver extends Observer {

    public BFSObserver(Player player) {
        super(player);
    }

    @Override
    public void onStart() {
        generalInfo.addInformation(
                "At the begining of this algorithm, one process know new gossip."
                        + "He wants to share gossip with everyone else. That means, "
                        + "he sends message to each neighbor.", -2);
        player.pause();
    }

    @Override
    public void onFinish() {
        generalInfo.addInformation(
                "All processes know the new gossip. Total number of send messages is"
                        + player.model.overallMessageCount, -1);
        generalInfo.addInformation("Press 'R' to continue.", -1);
    }

    @Override
    public void onEvent(Vertex vertex, String s) {
        if (matchNotification(s, "recieve")) {
            GUI.globalTimer.schedule(new Player.AuraEvent(vertex, 7), 0);
            String[] values = s.split(":");
            generalInfo
                    .addInformation(
                            "Process with id "
                                    + values[1]
                                    + " recieved new gossip. He wants to spread it, so he sends gossip to each neighbor"
                                    + " except the one, who sent him this gossip.", -2);
            player.pause();
        }
        if (matchNotification(s, "old")) {
            GUI.globalTimer.schedule(new Player.AuraEvent(vertex, 7), 0);
            String[] values = s.split(":");
            generalInfo
                    .addInformation(
                            "Process "
                                    + values[1]
                                    + " recieved gossip. But he seen this gossip before."
                                    + " He ignores this message, because he has already sent the gossip to his neighbors.",
                            -2);
            player.pause();
        }
    }
}

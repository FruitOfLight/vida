package algorithm;

import enums.BubblePosition;
import graph.Bubble;
import graph.Vertex;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.TreeSet;

import model.Player;

/*
 * Zivotny cyklus:
 * uvod
 *   vnikne novy observer
 *   nainicializuje sa
 *   procesy sa naloaduju
 * onStart()
 *   procesy sa nastartuju
 * 
 * procesy posielaju updaty onUpdate()
 *   tie sa vykonavaju velakrat
 * a eventy onEvent()
 *   tie sa vacsinou vykonaju len prvykrat( strazi si observer)
 *  
 * ked vsetky procesy vyhlasia dead - onFinish()  
 */

public class Observer {
    Player player;

    private ArrayList<Bubble> allBubbles = new ArrayList<Bubble>();
    private TreeSet<String> events = new TreeSet<String>();

    public Observer(Player player) {
        this.player = player;
    }

    Bubble generalInfo;

    Bubble inicializeBubble(Bubble bubble, int maxWidth) {
        bubble.setLockedPosition(true);
        bubble.setMaxWidth(maxWidth);
        bubble.position = BubblePosition.SE;
        allBubbles.add(bubble);
        return bubble;
    }

    void inicializeAllBubbles() {
        generalInfo = inicializeBubble(new Bubble(10, 10), 300);
    }

    void inicializeEvents() {
        events.clear();
    }

    protected boolean firstTime(String string) {
        if (events.contains(string))
            return false;
        events.add(string);
        return true;
    }

    protected boolean matchNotification(String notification, String string) {
        return (notification.contains(string) && firstTime(string));
    }

    /* spusti sa vzdy pri starte novej simulacie */
    public void init() {
        allBubbles.clear();
        inicializeAllBubbles();
        inicializeEvents();
    }

    public void onStart() {

    }

    public void onFinish() {

    }

    public void onUpdate(Vertex vertex, String message) {

    }

    public void onEvent(Vertex vertex, String message) {

    }

    public void step(long time) {

    }

    public void draw(Graphics2D g) {
        for (Bubble bubble : allBubbles)
            bubble.draw(g);
    }
}
/*
class ObserverEvent {
    private boolean happened;
    private ArrayList<Pair<Bubble, Pair<String, Integer>>> actions = new ArrayList<Pair<Bubble, Pair<String, Integer>>>();

    public ObserverEvent() {
        reset();
    }

    public ObserverEvent(Bubble bubble, String text, int time) {
        this();
        addAction(bubble, text, time);
    }

    boolean getHappened() {
        return happened;
    }

    void happen() {
        if (happened)
            return;
        happened = true;
        for (Pair<Bubble, Pair<String, Integer>> action : actions) {
            action.getFirst().addInformation(action.getSecond().getFirst(),
                    action.getSecond().getSecond());
        }
    }

    void reset() {
        happened = false;
    }

    void addAction(Bubble bubble, String text, int time) {

        actions.add(new Pair<Bubble, Pair<String, Integer>>(bubble, new Pair<String, Integer>(text,
                time)));
    }

}
*/
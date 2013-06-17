package graph;

import ui.GUI;

public class BubbleSet {
    // nemazat tu komentare !!!

    // Vsetko co pracuje s list musi byt synchronized
    //private LinkedList<Bubble> list = new LinkedList<Bubble>();
    //private LinkedList<Bubble> bornlist = new LinkedList<Bubble>();
    public static long time = 1;

    public static void step(long delay) {
        time += (long) (delay * Math.sqrt(GUI.player.getSendSpeed()));
    }

    /*nemazat komentar
     * @Override
    synchronized public void draw(Graphics2D g) {
        g.setFont(new Font(Font.DIALOG, Font.PLAIN, (int) (fontSize / Math
                .sqrt(GUI.graph.canvas.zoom))));

        for (Bubble bubble : list)
            bubble.draw(g);
    }

    synchronized public void clear() {
        list.clear();
        bornlist.clear();
        time = 1;
    }

    public void addBubble(Bubble bubble) {
        bornlist.add(bubble);
    }

    synchronized public void updateBubbles() {
        while (bornlist.size() > 0)
            list.add(bornlist.pop());
        // vyfiltrujeme vsetky mrtve bubliny
        for (Iterator<Bubble> it = list.iterator(); it.hasNext();) {
            if (it.next().state == BubbleState.dead)
                it.remove();
        }
    }
    */
}
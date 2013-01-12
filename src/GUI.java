import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

public class GUI {
    static Random random = new Random();

    static JFileChooser graphLoader;
    static JFileChooser graphSaver;
    /*static Canvas graphCanvas; 
    static Canvas queueCanvas;*/
    // uz pristupujeme cez graf
    static Graph graph;
    static Model model;
    static MessageQueue letterQueue;

    static void addElement(Container to, Component what, int x, int y, int w,
            int h) {
        what.setLocation(x, y);
        what.setSize(w, h);
        to.add(what);
    }

    static class Window implements Runnable {
        @Override
        public void run() {
            // TODO spravit krajsie

            final JFrame frame = new JFrame("ViDA");
            frame.setLayout(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            graphLoader = new JFileChooser("./");
            graphSaver = new JFileChooser("./");

            final JMenuBar menu = new JMenuBar();

            for (int i = 0; i < Menu.menuItems.length; i++) {
                final JMenu item = new JMenu(Menu.menuItems[i]);
                menu.add(item);
                for (int j = 0; j < Menu.allMenuItems[i].length; ++j) {
                    if (Menu.allMenuItems[i][j].equals("--")) {
                        item.addSeparator();
                    } else {
                        final JMenuItem meno = new JMenuItem(
                                Menu.allMenuItems[i][j]);
                        item.add(meno);
                        final int fi = i;
                        final int fj = j;
                        meno.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                Menu.performAction(fi, fj);
                            }
                        });
                    }
                }
            }

            addElement(frame, menu, 0, 0, 800, 25);
            addElement(frame, graph.canvas, 0, 75, 500, 500);
            addElement(frame, MessageQueue.getInstance().canvas, 0, 25, 600, 50);

            frame.setSize(800, 600);
            frame.setResizable(false);
            frame.setVisible(true);
        }

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO spravit krajsie
        graph = new Graph();
        model = new Model();
        letterQueue = MessageQueue.getInstance();
        graph.messages = MessageQueue.getInstance();
        final Window window = new Window();
        SwingUtilities.invokeLater(window);
    }

}

package ui;

import enums.Language;
import graph.MessageQueue;

import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import algorithms.AlgReader;
import algorithms.ModelSettings;
import algorithms.Player;

public class GUI {
    public static Random random = new Random();

    static JFileChooser graphLoader;
    static JFileChooser graphSaver;

    // uz pristupujeme cez graf 
    public static Player player;
    public static Controls controls;
    public static MessageQueue messageQueue;
    public static GlobalKeyListener gkl;
    public static ZoomWindow zoomWindow;
    public static InformationPanel informationPanel;
    public static JFrame frame;
    public static PopupPanel popupInformation;
    public static PopupPanel popupZoomWindow;
    public static JMenuBar menu;
    public static JLayeredPane layeredPane;
    public static Timer globalTimer;

    static Language language;

    static class Window implements Runnable {
        @Override
        public void run() {
            // TODO spravit krajsie

            final JFrame frame = new JFrame("ViDA");
            GUI.frame = frame;
            frame.setLayout(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            graphLoader = new JFileChooser("./");
            graphSaver = new JFileChooser("./");

            menu = new JMenuBar();

            for (int i = 0; i < Menu.menuItems.length; i++) {
                final JMenu item = new JMenu(Menu.menuItems[i]);
                menu.add(item);
                for (int j = 0; j < Menu.allMenuItems[i].length; ++j) {
                    if (Menu.allMenuItems[i][j].equals("--")) {
                        item.addSeparator();
                    } else {
                        final JMenuItem meno = new JMenuItem(Menu.allMenuItems[i][j]);
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

            popupInformation = new PopupPanel(informationPanel.scrollPanel);
            gkl.addMouseListener(KeyEvent.VK_I, popupInformation);
            popupZoomWindow = new PopupPanel(zoomWindow.canvas);

            layeredPane = new JLayeredPane();
            frame.add(layeredPane);
            //layeredPane.setLayout(null);
            layeredPane.add(menu);
            layeredPane.add(player.graph.canvas);
            layeredPane.add(informationPanel.scrollPanel);
            layeredPane.add(zoomWindow.canvas);
            layeredPane.add(popupInformation);
            layeredPane.add(popupZoomWindow);
            frame.add(controls.panel);

            layeredPane.setComponentZOrder(menu, 0);
            layeredPane.setComponentZOrder(player.graph.canvas, 1);
            layeredPane.setComponentZOrder(informationPanel.scrollPanel, 0);
            layeredPane.setComponentZOrder(zoomWindow.canvas, 0);
            layeredPane.setComponentZOrder(popupInformation, 0);
            layeredPane.setComponentZOrder(popupZoomWindow, 0);

            informationPanel.scrollPanel.setVisible(false);
            zoomWindow.canvas.setVisible(false);

            KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            manager.addKeyEventDispatcher(gkl);

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    saveApp();
                }
            });
            frame.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    refreshLayout();
                }
            });

            frame.setSize(CONST.windowWidth, CONST.windowHeight);
            frame.setVisible(true);
            int dw = frame.getWidth() - frame.getContentPane().getWidth();
            int dh = frame.getHeight() - frame.getContentPane().getHeight();
            System.out.println("frame " + dw + " " + dh);
            frame.setMinimumSize(new Dimension(CONST.minWindowWidth + dw, CONST.minWindowHeight
                    + dh));
            gRepaint();

        }
    }

    public static void refreshLayout() {
        int gw = frame.getContentPane().getWidth() - CONST.popupwidth;
        int gh = frame.getContentPane().getHeight() - CONST.menuHeight - CONST.controlsHeight;
        System.out.println("refreshLayout " + gw + " " + gh);
        layeredPane.setBounds(0, 0, frame.getContentPane().getWidth(), gh + CONST.menuHeight);
        menu.setBounds(0, 0, frame.getContentPane().getWidth(), CONST.menuHeight);
        player.graph.canvas.setLocation(0, CONST.menuHeight);
        player.graph.resizeCanvas(gw, gh);
        controls.panel.setBounds(0, gh + CONST.menuHeight, frame.getContentPane().getWidth(),
                CONST.controlsHeight);
        informationPanel.scrollPanel.setBounds(gw - CONST.informationWidth, CONST.menuHeight,
                CONST.informationWidth, gh - CONST.zoomWindowHeight);
        zoomWindow.canvas.setBounds(gw - CONST.zoomWindowWidth, CONST.menuHeight + gh
                - CONST.zoomWindowHeight, CONST.zoomWindowWidth, CONST.zoomWindowHeight);
        popupInformation.setBounds(gw, CONST.menuHeight, CONST.popupwidth, gh
                - CONST.zoomWindowHeight);
        popupZoomWindow.setBounds(gw, CONST.menuHeight + gh - CONST.zoomWindowHeight,
                CONST.popupwidth, CONST.zoomWindowHeight);
        controls.refresh();
        gRepaint();
    }

    public static void gRepaint() {
        if (controls != null)
            GUI.controls.canvas.repaint();
        if (player.graph != null)
            player.graph.canvas.repaint();
        if (layeredPane != null)
            GUI.layeredPane.repaint();
    }

    public static void acceptSettings(ModelSettings settings) {
        System.out.println("Gui is accepting new settings");
        player.setModel(settings.getModel());
        System.out.println("graph is accepting new settings");
        player.graph.acceptSettings(settings);
        System.out.println("Gui accepted new settings");
        gRepaint();
    }

    public static void saveApp() {
        try {
            File file = new File("backup/graf.in");
            PrintStream out = new PrintStream(file);
            player.graph.print(out);
            out.close();
            file = new File("backup/program.alg");
            out = new PrintStream(file);
            player.model.print(out);
            out.close();
            /*TODO file = new File("backup/settings.in");
            out = new PrintStream(file);
            model.settings.print(out);
            out.close();*/
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static void loadApp() {
        try {
            File file = new File("backup/graf.in");
            Scanner in = new Scanner(file);
            player.graph.read(in);
            in.close();
            AlgReader algReader = new AlgReader(new File("backup/program.alg"));
            player.model.read(algReader);
            player.model.program.compile();
            in.close();
            /*file = new File("backup/settings.in");
            in = new Scanner(file);
            player.model.settings.read(in);
            in.close();*/
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static void main(String[] args) {
        language = Language.english;

        gkl = new GlobalKeyListener();
        new Player();
        controls = new Controls();
        player.graph.listener.setControls(controls);

        zoomWindow = new ZoomWindow();
        informationPanel = new InformationPanel();
        globalTimer = new Timer();

        final Window window = new Window();
        SwingUtilities.invokeLater(window);
        loadApp();
    }
}

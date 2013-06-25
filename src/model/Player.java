package model;

import enums.InitType;
import enums.ModelType;
import enums.ObserverType;
import enums.RunState;
import graph.BubbleSet;
import graph.Edge;
import graph.Graph;
import graph.MessageQueue;
import graph.Vertex;

import java.awt.Color;
import java.util.TimerTask;

import ui.Dialog;
import ui.GUI;
import algorithm.Observer;
import algorithm.Program;

/*
 * Spusta programy a stara sa aby bezali
 * Je len jeden, mohol by sa spravit singleton
 */
public class Player {
    // toto je ten jediny spravny hlavny graf a model, z ktoreho ostatne objekty cerpaju
    public Graph graph;
    public Model model;
    public RunState state;
    public Observer observer;
    long startingTime;

    public Player() {
        state = RunState.stopped;
        GUI.player = this;
        model = new Model();
        graph = new Graph();
    }

    public void setModel(ModelType type) {
        ModelSettings settings = model.settings;
        AlgFileSetting program = model.program;
        model = ModelType.getNewInstance(type);
        model.settings = settings;
        model.program = program;
    }

    public RunState getRunState() {
        return state;
    }

    int timerid;

    public void start() {
        GUI.controls.refresh();
        startingTime = System.currentTimeMillis();
        boolean start = false;
        if (state == RunState.stopped) {
            if (model.program.none()) {
                Dialog.showMessage("No program to run");
                return;
            }
            if (model.program.compiling) {
                Dialog.showMessage("Program is still compiling, try it again, please");
                return;
            }
            load();
            model.load();
            observer.init();
            start = true;
        }
        timerid++;
        state = RunState.running;
        StepEvent.time = System.currentTimeMillis();
        GUI.globalTimer.schedule(new StepEvent(this, timerid), 0);
        if (start)
            go();

    }

    boolean savedRandomInitiation = false;

    void load() {
        observer = ObserverType.getNewInstance(this, model.settings.getObserverType());

        Vertex.randomInit.autoInitial();
        savedRandomInitiation = Vertex.randomInit.getInitial() != 0;

        if (savedRandomInitiation) {
            if (model.settings.getInit() == InitType.one) {
                int i = GUI.random.nextInt(graph.vertices.size());
                graph.vertices.get(i).setInitial(1);
            }
            if (model.settings.getInit() == InitType.multi) {
                for (Vertex v : graph.vertices) {
                    if (GUI.random.nextBoolean())
                        v.setInitial(1);
                }
            }
        }

        for (Vertex v : graph.vertices) {
            v.program = new Program(v, this);
            v.program.load(Model.binaryPath, 1);
        }
    }

    void go() {
        for (Vertex v : graph.vertices) {
            v.program.go();
        }
        observer.onStart();
    }

    public void stop() {
        /*if (algorithm != null)
            algorithm.defaultSettings();*/
        if (savedRandomInitiation) {
            Vertex.randomInit.setInitial(1);
        }
        GUI.controls.refresh();
        if (state == RunState.stopped)
            return;
        state = RunState.stopped;
        for (Vertex v : graph.vertices)
            v.program.kill();
        for (Edge e : graph.edges)
            e.restart();
        graph.setDefaultValues();
        observer = null;
        MessageQueue.messageCount = 0;
        GUI.informationPanel.erase();
        GUI.gRepaint();
    }

    public void pause() {
        if (state == RunState.stopped)
            return;
        state = RunState.paused;
        GUI.controls.refresh();
    }

    public void pauseFromProcess(Vertex vertex) {
        state = RunState.paused;
        GUI.globalTimer.schedule(new AuraEvent(vertex, 7), 0);
        pause();
    }

    private double sendSpeed = 1.2;
    private double speedBalance = 1.0, stableSpeedBalance = 1.0;
    public static int afps, sfps, fps;

    public void setSendSpeed(double speed) {
        sendSpeed = speed;
    }

    public double getSendSpeed() {
        return sendSpeed;
    }

    public String getSendSpeedString(int length) {
        Double d = getSendSpeed();
        if (length < 0) {
            length *= -1;
            d *= getSpeedBalance();
        }
        String result = d.toString();
        if (result.length() > length)
            result = result.substring(0, length);
        return result;
    }

    // kazda sprava touto metodou oznamuje modelu svoju planovanu rychlost, 
    // model na zaklade toho moze (ak je zapnute) menit celkovu rychlost pohybu
    public void listenSpeed(double speed) {
        speedBalance = Math.min(speedBalance, 0.7 / Math.abs(speed));
    }

    // zavola sa raz po zozbierani rychlosti sprav
    public void refreshBalance(long time) {
        if (state == RunState.paused) {
            stableSpeedBalance *= Math.pow(0.99, time * 0.001);
            return;
        }
        //double p = Math.pow(0.1, time * 0.001);
        //stableSpeedBalance = p * stableSpeedBalance + (1 - p) * speedBalance;
        stableSpeedBalance = speedBalance;
        speedBalance = 20.0;
    }

    public double getSpeedBalance() {
        return 0.5 * ((GUI.controls != null && GUI.controls.get("p_auto-speed") != null && GUI.controls
                .get("p_auto-speed").isActive()) ? stableSpeedBalance : 1);
    }

    static class StepEvent extends TimerTask {
        static long time = 0;
        Player player;
        int id;

        public StepEvent(Player player, int id) {
            this.player = player;
            this.id = id;
        }

        @Override
        public void run() {
            if (player.state == RunState.stopped || id != player.timerid) {
                return;
            }
            long prevTime = time;
            time = System.currentTimeMillis();
            long delay = time - prevTime;
            if (delay > 0)
                fps = (int) (1000 / delay);

            // Spravy
            if (player.state != RunState.stopped) {
                for (Edge edge : player.graph.edges)
                    edge.queue.measure(delay);
            }
            player.refreshBalance(delay);
            if (player.state != RunState.stopped) {
                for (Edge edge : player.graph.edges)
                    edge.queue.move(delay);
            }
            if (player.state == RunState.running) {
                BubbleSet.step(delay);
                player.observer.step(delay);
            }

            GUI.gRepaint();
            GUI.globalTimer.schedule(new StepEvent(player, id), 30);
        }
    }

    public static class AuraEvent extends TimerTask {
        Vertex vertex;
        int count;

        public AuraEvent(Vertex vertex, int count) {
            this.vertex = vertex;
            this.count = count;
        }

        @Override
        public void run() {
            if (count % 2 == 1)
                vertex.setAuraColor(new Color(0, 50, 125, 50));
            else
                vertex.setAuraColor(new Color(0, 50, 125, 200));
            GUI.gRepaint();
            if (count == 0) {
                vertex.setAuraColor(new Color(255, 255, 255, 0));
            } else
                GUI.globalTimer.schedule(new AuraEvent(vertex, count - 1), 200);
        }
    }
}

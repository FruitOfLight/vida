package algorithms;

import enums.ModelType;
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

/*
 * Spusta programy a stara sa aby bezali
 * Je len jeden, mohol by sa spravit singleton
 */
public class Player {
    // toto je ten jediny spravny hlavny graf a model, z ktoreho ostatne objekty cerpaju
    public Graph graph;
    public Model model;
    public RunState state;
    long startingTime;

    public Player() {
        state = RunState.stopped;
        GUI.player = this;
        model = new Model();
        graph = new Graph();
    }

    public void setModel(ModelType modelTyp) {
        if (modelTyp == ModelType.DEF)
            model = new Model();
        if (modelTyp == ModelType.LE)
            model = new LeaderElectionModel();
        else if (modelTyp == ModelType.BC)
            model = new BroadcastModel();
        else if (modelTyp == ModelType.TR)
            model = new TraversalModel();
    }

    public RunState getRunState() {
        return state;
    }

    int timerid;

    public void start() {
        GUI.controls.refresh();
        startingTime = System.currentTimeMillis();
        if (state == RunState.stopped) {
            if (model.program.compiling) {
                Dialog.showMessage("Program is still compiling, try it again, please");
                return;
            }
            load();
            model.load();
        }
        timerid++;
        state = RunState.running;
        StepEvent.time = System.currentTimeMillis();
        GUI.globalTimer.schedule(new StepEvent(this, timerid), 0);

    }

    void load() {
        for (Vertex v : graph.vertices) {
            v.program = new Program(v, this);
            v.program.load(Model.binaryPath, 1);
        }
        /*if (algorithm != null)
            algorithm.startAlgorithm();*/
    }

    public void stop() {
        /*if (algorithm != null)
            algorithm.defaultSettings();*/
        GUI.controls.refresh();
        if (state == RunState.stopped)
            return;
        state = RunState.stopped;
        for (Vertex v : graph.vertices)
            v.program.kill();
        for (Edge e : graph.edges)
            e.restart();
        graph.setDefaultValues();
        MessageQueue.messageCount = 0;
        GUI.informationPanel.erase();
        GUI.gRepaint();
    }

    public void pause() {
        GUI.controls.refresh();
        if (state == RunState.stopped)
            return;
        state = RunState.paused;
    }

    void pauseFromProcess(Vertex vertex) {
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
            if (player.state == RunState.running)
                BubbleSet.step(delay);

            GUI.gRepaint();
            GUI.globalTimer.schedule(new StepEvent(player, id), 30);
        }
    }

    static class AuraEvent extends TimerTask {
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

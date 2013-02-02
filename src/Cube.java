import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;

public class Cube {
    Message message;
    double x, y, xsp, ysp, oldx, oldy;
    double width, height;
    double gravity, pull;
    CubeState state;

    Cube(Message message) {
        this.message = message;
        state = CubeState.alive;
        width = 0.;
        height = 0.;
        xsp = ysp = 0.;
        gravity = 1;
        pull = 0;

    }

    public void draw(Graphics g, double offset, double zoom) {
        int dx = (int) (offset + (x - width) * zoom);
        int dy = (int) (CONST.queueHeight - (y + height) * zoom);
        int dw = (int) (width * zoom - 2);
        int dh = (int) (height * zoom - 2);

        g.setColor(new Color(200, 255, 200));
        g.fillRect(dx, dy, dw, dh);
        if (x < width) {
            g.setColor(new Color(255, 50, 40));
            Canvas.realFillRect(g, dx, dy, offset - dx, dh);
        }
        g.setColor(new Color(0, 0, 0));
        g.drawRect(dx, dy, dw, dh);

        message.drawInfo(g, dx, dy, dw, dh);

        g.setColor(message.gColor);
        g.fillRect(dx, dy - 1, dw, 3);
    }

    public boolean isOnPoint(double x, double y) {
        if (x < this.x - this.width || x > this.x) {
            return false;
        }
        if (y > this.y + this.height || y < this.y) {
            return false;
        }
        return true;
    }

    public void onClick() {
        if (state == CubeState.asleep) {
            state = CubeState.wakeup;
            return;
        }
        if (state == CubeState.alive) {
            state = CubeState.asleep;
            return;
        }
    }

    public static void stepAll(long time) {
        // TODO
    }

    public static void drawAll(Graphics g, double offset, double zoom) {
        // TODO
    }

    private static final ArrayList<Cube> cubes = new ArrayList<Cube>();

    public static Collection<Cube> getAllCubes() {
        return cubes;
    }

    public static void deleteAllCubes() {
        cubes.clear();
    }

    public static void addCube(Message message) {
        int index = 0;
        // TODO zrychlit
        for (int i = 0; i < cubes.size(); ++i) {
            if (cubes.get(i).message.edge == message.edge) {
                index = i;
            }
        }
        if (index < cubes.size()) {
            index = GUI.random.nextInt(cubes.size() - index) + index + 1;
        }
        Cube cube = new Cube(message);
        cubes.add(index, cube);
    }

    public static void removeCube(int index) {
    }

    static boolean swap(int i, int j) {
        //TODO
        /*if (mainList.get(i).edge == mainList.get(j).edge) {
            return false;
        }
        Collections.swap(mainList, i, j);
        return true;*/
        return false;
    }

    static Cube getCube(double x, double y) {
        Cube chosen = null;
        for (Cube cube : cubes) {
            if (cube.isOnPoint(x, y)) {
                // TODO priority
                chosen = cube;
            }
        }
        return chosen;
    }

    static Cube getCube(int index) {
        if (index < 0 || index >= cubes.size()) {
            return null;
        }
        return cubes.get(index);
    }

    //private ArrayList<ArrayList<Message>> buckets;

    /*public void bucketReduce() {
        /*double maximalPosition = 0.0;
        for (Cube c : MessageQueue.getInstance().mainList) {
            maximalPosition = Math.max(maximalPosition, m.x);
        }
        buckets = new ArrayList<ArrayList<Message>>();
        for (int i = 0; i < (int) maximalPosition + 3; ++i) {
            buckets.add(new ArrayList<Message>());
        }
        for (Message m : MessageQueue.getInstance().mainList) {
            getBucket(m.qX).add(m);
        }
    }*/

    /*public ArrayList<Message> getBucket(double qx) {
        return buckets.get((int) qx + 1);
    }*/

    /*
     

    public void born(int index) {
        state = MessageState.born;
        ePosition = eSpeed = 0.0;
        qSpeed = 0.0;
        qY = 0.0;
        Message prev = MessageQueue.getInstance().safeGet(index - 1);
        Message next = MessageQueue.getInstance().safeGet(index + 1);
        if (prev == null && next == null) {
            qX = 10;
        } else if (next == null) {
            qX = prev.qX + 1;
        } else if (prev == null) {
            qX = next.qX - next.qSize;
        } else {
            qX = (prev.qX + next.qX - next.qSize) / 2;
        }

        qSize = 0.1;
    }

    public void queueStep(long time, int index) {
        Message prev = index > 0 ? MessageQueue.getInstance().mainList.get(index - 1) : null;
        if (prev == null) {
            blockingQx = -faraway;
        } else {
            blockingQx = Math.max(prev.qX, prev.blockingQx);
        }
        if (state == MessageState.born) {
            qSize += vspeed * time * 0.001 * MessageQueue.getInstance().getRealSendSpeed();
            if (qSize > 1.0) {
                qSize = 1.0;
                state = MessageState.main;
            }
        }
        if (state == MessageState.main) {
            moveForward(time, index);
            if (qX < 0.0) {
                MessageQueue.getInstance().mainList.remove(index);
                MessageQueue.getInstance().deadList.add(this);
                state = MessageState.dead;
            }
        }
        if (state == MessageState.sleep) {
            qY = CONST.queueHeight / MessageQueue.getInstance().zoom - qSize;
        } else {
            qY = 0.0;
        }
        if (state == MessageState.dead) {

        }
    }

    void queueMove(long time, int index) {
        
             Message prev = index > 0 ? MessageQueue.getInstance().mainList.get(index - 1) : null;
             double shift = hspeed * time * 0.001 * MessageQueue.getInstance().getRealSendSpeed();

             if (prev != null && prev.state == MessageState.sleep && (qX < prev.qX + qSize + shift)) {
                 boolean success = MessageQueue.getInstance().swapMessages(index - 1, index);
                 if (!success && (blockingQx + qSize < qX)) {
                     state = MessageState.sleep;
                 }
             }
             if (blockingQx + qSize < qX) {
                 if (MessageQueue.getInstance().model.running == RunState.running) {
                     qX -= shift;
                     qY = 0.0;
                 }
             } else {
                 if (blockingQx + qSize - qX < shift) {
                     qX = blockingQx + qSize;
                 } else {
                     qX += shift;
                     qY = 0.1;
                 }
             }
             
    }

    public ArrayList<Message> getCollidedMessages() {
        ArrayList<Message> list = new ArrayList<Message>();
        for (int i = -1; i <= 1; ++i) {
            for (Message message : MessageQueue.getInstance().getBucket(qX + i))
                if (collide(message, this))
                    list.add(message);
        }
        return list;
    }

    static boolean collide(Message m1, Message m2) {
        return (Math.max(m1.qX - m1.qSize, m2.qX - m2.qSize) < Math.min(m1.qX, m2.qX));
    }

    */

}

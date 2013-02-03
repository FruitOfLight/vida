import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Collections;

public class Cube {
    Message message;
    double x, y, xsp, ysp, oldx, oldy;
    double width, height;
    double gravity, pull, depth;
    CubeState state;

    Cube(Message message, int index) {
        this.message = message;
        state = CubeState.alive;
        width = 0.;
        height = 0.;
        xsp = ysp = 0.;
        gravity = 1;
        pull = 0;
        depth = 0.;
        y = 0.;
        Cube prev = getCube(index - 1, CubeSorting.position);
        Cube next = getCube(index + 1, CubeSorting.position);
        if (prev == null && next == null) {
            x = 10;
        } else if (next == null) {
            x = prev.x + 2;
        } else if (prev == null) {
            x = next.x - next.width;
        } else {
            x = (prev.x + next.x - next.width) / 2;
        }
    }

    public void draw(Graphics g, double offset, double zoom) {
        if (state == CubeState.dead)
            return;
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
        if (state == CubeState.alive || state == CubeState.wakeup) {
            state = CubeState.asleep;
            return;
        }
    }

    public void moveByState(long time) {
        if (state == CubeState.alive) {
            y = 0.0;
            //y = Math.max(0.0, y - 0.5 * time * 0.001);
        }
        if (state == CubeState.asleep) {
            y = CONST.queueHeight / MessageQueue.getInstance().zoom - height;
        }
        if (state == CubeState.wakeup) {
            state = CubeState.alive;
        }
        if (width < 1.)
            width += 0.5 * time * 0.001;
        if (height < 1.)
            height += 0.5 * time * 0.001;
    }

    public void forceForward(long time) {
        if (y < 1e-2) {
            xsp = -20 * MessageQueue.getInstance().getRealSendSpeed();
        } else {
            xsp = 0.0;
        }
        message.expectedSpeed = (1.0 - message.ePosition) * (-xsp / x);
    }

    public void calculateCollisions(long time) {
        for (Cube cube : cubes) {
            double c = collision(this, cube);
            //double sign = () ? -1. : 1.;
            if (c > 0 && x >= cube.x)
                xsp += (5 * c * c + c) * 2 * MessageQueue.getInstance().getSendSpeed();
        }
    }

    public void step(long time) {
        x += xsp * time * 0.001;
        if (x <= 0.) {
            state = CubeState.dead;
        }
        if (state == CubeState.dead) {
            message.expectedSpeed = CONST.messageSpeedLimit;
        }
        if (state == CubeState.asleep) {
            message.expectedSpeed = 0.0;
        }
    }

    static double collision(Cube c1, Cube c2) {
        if (c1 == c2)
            return -1.;
        return Math.min(c1.x, c2.x) - Math.max(c1.x - c1.width, c2.x - c2.width);
    }

    static CubeSorting sortedBy = CubeSorting.none;

    public static void stepAll(long time) {
        sortBy(CubeSorting.position);
        for (Cube cube : cubes) {
            cube.moveByState(time);
            cube.forceForward(time);
        }
        for (Cube cube : cubes) {
            cube.calculateCollisions(time);
            cube.step(time);
        }
        /*        for (Cube cube : cubes) {
                    
                }*/
        removeDead();
    }

    public static void drawAll(Graphics g, double offset, double zoom) {
        sortBy(CubeSorting.depth);
        for (Cube cube : cubes) {
            cube.draw(g, offset, zoom);
        }
    }

    private static ArrayList<Cube> cubes = new ArrayList<Cube>();

    public static Collection<Cube> getAllCubes() {
        return cubes;
    }

    public static void removeDead() {
        ArrayList<Cube> newCubes = new ArrayList<Cube>();
        for (Cube cube : cubes) {
            if ((cube.state == CubeState.dead) && (cube.message.ePosition >= 1.0 - 1e-3)) {
                cube.message.recieve();
            } else {
                newCubes.add(cube);
            }
        }
        cubes = newCubes;
    }

    public static void deleteAllCubes() {
        cubes.clear();
    }

    public static void addCube(Message message) {
        sortedBy = CubeSorting.none;
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
        Cube cube = new Cube(message, index);
        cubes.add(index, cube);
    }

    public static void removeCube(int index) {
        cubes.remove(index);
    }

    public static void removeCube(Cube cube) {
        cubes.remove(cube);
    }

    static Cube getCube(double x, double y) {
        Cube chosen = null;
        for (Cube cube : cubes) {
            if (cube.isOnPoint(x, y)) {
                if (chosen == null || chosen.depth < cube.depth)
                    chosen = cube;
            }
        }
        return chosen;
    }

    static Cube getCube(int index, CubeSorting sort) {
        sortBy(sort);
        if (index < 0 || index >= cubes.size()) {
            return null;
        }
        return cubes.get(index);
    }

    static boolean sortBy(CubeSorting sort) {
        if (sort != CubeSorting.none && sortedBy != sort) {
            Collections.sort(cubes, sort.getComparator());
        }
        Cube.sortedBy = sort;
        return false;
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
    */

}

class ComparePosition implements Comparator<Cube> {
    @Override
    public int compare(Cube c1, Cube c2) {
        return Double.compare(c1.x, c2.x);
    }
}

class CompareDepth implements Comparator<Cube> {
    @Override
    public int compare(Cube c1, Cube c2) {
        return Double.compare(c1.depth, c2.depth);
    }
}

enum CubeSorting {
    none, position, depth;
    Comparator<Cube> getComparator() {
        switch (this) {
        case position:
            return new ComparePosition();
        case depth:
            return new CompareDepth();
        }
        return null;
    }
}
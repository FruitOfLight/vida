package algorithms;

public class Pair<A, B> {
    private A first;
    private B second;

    public Pair() {

    }

    public Pair(A f, B s) {
        setFirst(f);
        setSecond(s);
    }

    public A getFirst() {
        return first;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public B getSecond() {
        return second;
    }

    public void setSecond(B second) {
        this.second = second;
    }

}
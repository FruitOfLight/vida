
import java.io.PrintStream;

public interface Algorithm extends Drawable {

    public String getPath();

    public void startAlgorithm();

    public void recieveUpdate(Vertex vertex, String s);

    public void defaultSettings();

    public void finishAlgorithm(Vertex leader);

    public void print(PrintStream out);

}

import java.awt.Component;
import java.awt.Container;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;


public class GUI {
	
	static JFileChooser graphLoader;
	static Canvas canvas;
	static Graph graph;
	
	static void addElement(Container to, Component what, int x, int y, int w, int h){
        what.setLocation(x,y);
        what.setSize(w,h);
        to.add(what);
    }

	
	static class Window implements Runnable {
		@Override
		public void run() {
			final JFrame frame = new JFrame("ViDA");
	        frame.setLayout(null); 
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	        graphLoader=new JFileChooser("./");
	        
            final JMenuBar menu = new JMenuBar();
            addElement(frame, menu, 0, 0, 800, 25);
            
            canvas = new Canvas();
            addElement(frame, canvas, 0, 25, 500, 500);
            
			frame.setSize(800,600);
            frame.setResizable(false);
            frame.setVisible(true);
		}
				
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		graph = new Graph("graf.in");
		final Window window = new Window();
        SwingUtilities.invokeLater(window);
	}

}

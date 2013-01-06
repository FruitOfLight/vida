import javax.swing.*;


public class GUI {

	
	static class Window implements Runnable {
		@Override
		public void run() {
			final JFrame frame = new JFrame("ViDA");
	        frame.setLayout(null); 
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			
			frame.setSize(800,600);
            frame.setResizable(false);
            frame.setVisible(true);
		}
				
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Window window = new Window();
        SwingUtilities.invokeLater(window);
	}

}

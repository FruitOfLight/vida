import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFileChooser;


public class Menu {
	
	static final String[] menuItems = {"Graph"};
	static final String[][] allMenuItems = {
		{"New", "Open", "Save", "--", "Quit"},
	};
	
	static void performAction(int r, int c)
	{
		switch(r)
		{
		case 0:
			switch(c)
			{
			case 0:
				GUI.graph.vertices = new ArrayList<Vertex>();
				GUI.graph.edges = new ArrayList<Edge>();
				GUI.canvas.repaint();
				break;
			case 1:
				int value = GUI.graphLoader.showOpenDialog(null);
                if (value == JFileChooser.APPROVE_OPTION) {
                    try {
                        File file = GUI.graphLoader.getSelectedFile();
                        Scanner input = new Scanner(file);
                        GUI.graph.read(input);
                        input.close();
                    }catch(Exception e){
                        System.out.println("Exception during opening\n");
                    }
                    GUI.canvas.repaint();                
                }
				break;
			case 2:
				value = GUI.graphSaver.showSaveDialog(null);
                if (value == JFileChooser.APPROVE_OPTION) {
                    try {
                        File file = GUI.graphSaver.getSelectedFile();
                        PrintStream output = new PrintStream(file);
                        GUI.graph.print(output);
                        output.close();
                    }catch(Exception e){
                        System.out.println("Exception during saving\n");
                    }
                }
				break;
			case 4:
				System.exit(0);
				return;
			default:
				System.out.println("Invalid entry!");
			}
			break;
		default:
			System.out.println("Invalid entry!");
		}
	}

}

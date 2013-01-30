import java.io.File;
import java.io.PrintStream;
import java.util.Scanner;

import javax.swing.JFileChooser;

public class Menu {

	static final String[] menuItems = { "App", "Graph", "Program" };
	static final String[][] allMenuItems = { { "Configure", "Quit" },
			{ "New", "Open", "Save" }, { "Open", "Settings" } };

	static void performAction(int r, int c) {
		switch (r) {
		case 0:
			switch (c) {
			case 0:
				Dialog.showMessage("Nothing to do");
				break;
			case 1:
				GUI.saveApp();
				System.exit(0);
				return;
			default:
				System.out.println("Invalid entry!");
			}
			break;

		case 1:
			switch (c) {
			case 0:
				GUI.graph.createNew();
				break;
			case 1:
				int value = GUI.graphLoader.showOpenDialog(null);
				if (value == JFileChooser.APPROVE_OPTION) {
					try {
						File file = GUI.graphLoader.getSelectedFile();
						Scanner input = new Scanner(file);
						GUI.graph.read(input);
						input.close();
					} catch (Exception e) {
						System.out.println("Exception during opening\n");
					}
					GUI.graph.canvas.repaint();
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
					} catch (Exception e) {
						System.out.println("Exception during saving\n");
					}
				}
				break;
			default:
				System.out.println("Invalid entry!");
			}
			break;
		case 2:
			if (GUI.model.running != RunState.stopped)
				break;
			switch (c) {
			case 0:
				ModelSettings.getInstance().loadProgram();
				break;
			case 1:
				ModelSettings.getInstance().setSettings();
				break;
			default:
				Dialog.showError("Not implemented");
			}
			break;
		default:
			System.out.println("Invalid entry!");
		}
	}

}

import java.io.File;
import java.io.PrintStream;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class ModelSettings {

	private static ModelSettings instance = new ModelSettings();
	private Anonym anonym;
	private Synchroned synchroned;
	private GraphType graphType;
	private boolean[] locked;

	public static ModelSettings getInstance() {
		return instance;
	}

	private ModelSettings() {
		this.anonym = Anonym.anonymOff;
		this.synchroned = Synchroned.synchronedOff;
		this.graphType = GraphType.none;
		locked = new boolean[3];
		locked[0] = locked[1] = locked[2] = false;
	}

	public void setAnonym(Anonym anonym) {
		this.anonym = anonym;
	}

	public void setSynchroned(Synchroned synchroned) {
		this.synchroned = synchroned;
	}

	public Anonym getAnonym() {
		return anonym;
	}

	public Synchroned getSynchroned() {
		return synchroned;
	}

	public void setGraphType(GraphType graphType) {
		this.graphType = graphType;
	}

	public GraphType getGraphType() {
		return graphType;
	}

	public void setLocked(int i, boolean t) {
		locked[i] = t;
	}

	public boolean getLocked(int i) {
		return locked[i];
	}

	public void setSettings() {
		Dialog.DialogProgramSettings newProgramSettings = new Dialog.DialogProgramSettings();
		int ok = JOptionPane.showConfirmDialog(null,
				newProgramSettings.getPanel(), "Program settings",
				JOptionPane.OK_CANCEL_OPTION);
		if (ok != JOptionPane.OK_OPTION)
			return;
		if (anonym == Anonym.anonymOff && newProgramSettings.getAnonym()) {
			anonym = Anonym.anonymOn;
			AnonymModifier visitor = new AnonymModifier();
			GUI.graph.accept(visitor);
		}
		if (anonym == Anonym.anonymOn && !newProgramSettings.getAnonym()) {
			anonym = Anonym.anonymOff;
			UnanonymModifier visitor = new UnanonymModifier();
			GUI.graph.accept(visitor);
		}
		if (newProgramSettings.getSynchroned())
			synchroned = Synchroned.synchronedOn;
		else
			synchroned = Synchroned.synchronedOff;
		graphType = newProgramSettings.getType();
	}

	public void loadProgram() {
		try {
			JFileChooser programLoader = new JFileChooser("./");
			String path = "";
			int value = programLoader.showOpenDialog(null);
			if (value == JFileChooser.APPROVE_OPTION) {
				File file = programLoader.getSelectedFile();
				path = file.getPath();
			}
			compile(path);
			GUI.model.path = path + ".bin";
			GUI.controls.canvas.repaint();
		} catch (Exception e) {
			Dialog.showError("Something went horribly wrong");
		}
	}

	public void compile(String path) {
		if (path.equals(""))
			return;
		try {
			Runtime.getRuntime().exec("bash algorithms/compile.sh " + path);
		} catch (Exception e) {
			Dialog.showError("Something went horribly wrong");
		}
	}

	public void print(PrintStream out) {
		out.println(CONST.AnonymToInt(anonym));
		out.println(CONST.SynchronedToInt(synchroned));
		out.println(CONST.GraphTypeToInt(graphType));
		for (int i = 0; i < 3; i++)
			if (locked[i])
				out.println("1");
			else
				out.println("0");
	}

	public void read(Scanner in) {
		anonym = CONST.IntToAnonym(in.nextInt());
		synchroned = CONST.IntToSynchroned(in.nextInt());
		graphType = CONST.IntToGraphType(in.nextInt());
		for (int i = 0; i < 3; i++)
			if (in.nextInt() == 0)
				locked[i] = false;
			else
				locked[i] = true;
	}

}

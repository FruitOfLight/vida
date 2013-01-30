import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Dialog {

	static void showError(String errorMessage) {
		JOptionPane.showMessageDialog(null, errorMessage, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	static void showMessage(String message) {
		JOptionPane.showMessageDialog(null, message, "Message",
				JOptionPane.PLAIN_MESSAGE);
	}

	static class DialogNewVertex {
		private JPanel panel = new JPanel();
		private InputField IDField;

		public DialogNewVertex(int defaultID) {
			IDField = new InputField("" + defaultID);
			panel.setLayout(new GridLayout(2, 2, 5, 5));
			panel.add(new JLabel("ID: "));
			panel.add(IDField);
		}

		public int getID() {
			if (ModelSettings.getInstance().getAnonym() == Anonym.anonymOn)
				return 0;
			return IDField.getInt(0, 0, 1000000);
		}

		public JComponent getPanel() {
			return panel;
		}
	}

	static class DialogNewGraph implements ActionListener {
		private JPanel panel = new JPanel();
		String[] graphTypes = { "Empty", "Clique", "Cycle", "Grid", "Wheel",
				"Random" };
		String[][] labelTexts = {
				{ "", "Vertices", "Vertices", "Rows", "Sides", "Vertices" },
				{ "", "", "", "Columns", "", "Edges" } };

		private JComboBox<String> choose = new JComboBox<String>(graphTypes);
		private JCheckBox edges = new JCheckBox();
		private InputField[] inputFields = new InputField[2];
		private JLabel[] labels = new JLabel[2];

		public DialogNewGraph(GraphType graphType) {
			panel.setLayout(new GridLayout(4, 2, 5, 5));
			panel.add(new JLabel("Type "));
			choose.setSelectedIndex(CONST.GraphTypeToInt(graphType));
			choose.addActionListener(this);
			if (graphType != GraphType.none)
				choose.setEnabled(false);
			panel.add(choose);
			for (int i = 0; i < 2; ++i) {
				labels[i] = new JLabel();
				inputFields[i] = new InputField();
				panel.add(labels[i]);
				panel.add(inputFields[i]);
			}
			panel.add(new JLabel("Create edges? "));
			panel.add(edges);
			edges.setSelected(true);
			actionPerformed(null);
		}

		public int getInputValue(int id) {
			return inputFields[id].getInt(-1, 0, 100);
		}

		public boolean getEdges() {
			return edges.isSelected();
		}

		public int getType() {
			return choose.getSelectedIndex();
		}

		public JComponent getPanel() {
			return panel;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < 2; ++i) {
				labels[i].setText(labelTexts[i][choose.getSelectedIndex()]);
				inputFields[i].setVisible(labels[i].getText().length() != 0);
				int value = inputFields[i].getInt(-1, 0, 101, true);
				if (value < 1 || value > 100)
					inputFields[i].setText("5");
			}
		}
	}

	static class DialogProgramSettings implements ActionListener {
		private JPanel panel = new JPanel();

		String[] graphTypes = { "Empty", "Clique", "Cycle" };

		private JComboBox<String> chooseGraphType = new JComboBox<String>(
				graphTypes);
		private JCheckBox anonym = new JCheckBox();
		private JCheckBox synchroned = new JCheckBox();

		public DialogProgramSettings() {
			panel.setLayout(new GridLayout(3, 2, 5, 5));
			panel.add(new JLabel("Anonym "));
			panel.add(anonym);
			anonym.setSelected(ModelSettings.getInstance().getAnonym() == Anonym.anonymOn);
			panel.add(new JLabel("Synchroned "));
			panel.add(synchroned);
			panel.add(new JLabel("Graph type "));
			chooseGraphType.setSelectedIndex(CONST.GraphTypeToInt(ModelSettings
					.getInstance().getGraphType()));
			chooseGraphType.addActionListener(this);
			panel.add(chooseGraphType);
			synchroned
					.setSelected(ModelSettings.getInstance().getSynchroned() == Synchroned.synchronedOn);
			anonym.setEnabled(!ModelSettings.getInstance().getLocked(0));
			synchroned.setEnabled(!ModelSettings.getInstance().getLocked(1));
			chooseGraphType.setEnabled(!ModelSettings.getInstance()
					.getLocked(2));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Dialog.DialogNewGraph newGraphDialog = new Dialog.DialogNewGraph(
					CONST.IntToGraphType(chooseGraphType.getSelectedIndex()));
			JOptionPane.showMessageDialog(null, newGraphDialog.getPanel(),
					"New graph", JOptionPane.OK_CANCEL_OPTION);
			GUI.graph.createNew(newGraphDialog);
		}

		public GraphType getType() {
			return CONST.IntToGraphType(chooseGraphType.getSelectedIndex());
		}

		public boolean getAnonym() {
			return anonym.isSelected();
		}

		public boolean getSynchroned() {
			return synchroned.isSelected();
		}

		public JComponent getPanel() {
			return panel;
		}

	}

}

class InputField extends JTextField {
	private static final long serialVersionUID = 8383005251115198842L;
	private int minValue = 0;
	private int maxValue = 999;

	public InputField() {
		super();
	}

	public InputField(String text) {
		super(text);
	}

	public void setRange(int min, int max) {
		minValue = min;
		maxValue = max;
	}

	// pozor, def moze byt mimo rozsahu
	public int getInt(int def) {
		return getInt(def, minValue, maxValue);
	}

	// pozor, def moze byt mimo rozsahu
	public int getInt(int def, int min, int max) {
		return getInt(def, min, max, false);
	}

	// pozor, def moze byt mimo rozsahu
	// iff (quiet == true) nevypisuje hlasky
	public int getInt(int def, int min, int max, boolean quiet) {
		try {
			Integer res = Integer.parseInt(getText());
			if (res > max) {
				if (!quiet)
					Dialog.showError("Value is too large. Maximum value " + max
							+ " was used instead\n");
				res = max;
			}
			if (res < min) {
				if (!quiet)
					Dialog.showError("Value is too small. Minimum value " + min
							+ " was used instead\n");
				res = min;
			}
			return res;
		} catch (NumberFormatException e) {
			if (!quiet)
				Dialog.showError("Value is not valid integer. Default value "
						+ def + " was used instead\n");
			return def;
		}
	}

}
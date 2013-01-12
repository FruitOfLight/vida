import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Dialog {

	static void vyhlasChybu(String chybovaSprava) {
		JOptionPane.showMessageDialog(null, chybovaSprava, "Chyba",
				JOptionPane.ERROR_MESSAGE);
	}

	static void vyhlasSpravu(String sprava) {
		JOptionPane.showMessageDialog(null, sprava, "Správa",
				JOptionPane.PLAIN_MESSAGE);
	}

	static class DialogNewVertex {
		private JPanel panel = new JPanel();
		private JTextField IDField;

		public DialogNewVertex() {
			IDField = new JTextField("" + 0);
			panel.setLayout(new GridLayout(2, 2, 5, 5));
			panel.add(new JLabel("ID: "));
			panel.add(IDField);
		}

		public int getID() {
			try {
				Integer res = Integer.parseInt(IDField.getText());
				return res;
			} catch (NumberFormatException e) {
				return -1;
			}
		}

		public JComponent getPanel() {
			return panel;
		}
	}

}
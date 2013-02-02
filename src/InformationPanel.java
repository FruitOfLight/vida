import java.awt.Color;
import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class InformationPanel {

    public JTextArea panel;
    public JScrollPane scrollPanel;

    public InformationPanel() {
        panel = new JTextArea();
        scrollPanel = new JScrollPane(panel);
        settings();
    }

    public void settings() {
        scrollPanel.setAutoscrolls(true);
        panel.setEnabled(false);
        panel.setDisabledTextColor(new Color(0, 0, 0));
    }

    public void erase() {
        panel.setText("");
    }

    public synchronized void printInformation(Vertex vertex, String information) {
        panel.setFont(new Font(null, Font.PLAIN, 13));
        panel.append(((Integer) vertex.getID()).toString() + " say: ");
        panel.append(information + "\n");
        panel.setCaretPosition(panel.getDocument().getLength());
    }

}

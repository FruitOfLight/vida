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

    public void printInformation(Vertex vertex, String information) {
        scrollPanel.setFont(new Font(null, Font.PLAIN, 12));
        panel.append(((Integer) vertex.getID()).toString() + " say: " + information + "\n");
        panel.setCaretPosition(panel.getDocument().getLength());
    }
}

import java.awt.Color;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class InformationPanel {

    public JTextPane panel;
    public JScrollPane scrollPanel;

    public InformationPanel() {
        panel = new JTextPane();
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
        try {
            SimpleAttributeSet attributeSet = new SimpleAttributeSet();
            StyleConstants.setBold(attributeSet, true);
            StyleConstants.setFontSize(attributeSet, 13);
            panel.setCharacterAttributes(attributeSet, true);
            Document document = panel.getStyledDocument();
            document.insertString(document.getLength(), ((Integer) vertex.getID()).toString()
                    + " say: ", attributeSet);
            StyleConstants.setBold(attributeSet, false);
            panel.setCharacterAttributes(attributeSet, true);
            document = panel.getStyledDocument();
            document.insertString(document.getLength(), information + "\n", attributeSet);
            panel.setCaretPosition(panel.getDocument().getLength());
        } catch (Exception e) {
        }
    }
}

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

    static void vyhlasChybu(String chybovaSprava) {
        JOptionPane.showMessageDialog(null, chybovaSprava, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    static void vyhlasSpravu(String sprava) {
        JOptionPane.showMessageDialog(null, sprava, "Message",
                JOptionPane.PLAIN_MESSAGE);
    }

    static class DialogNewVertex {
        private JPanel panel = new JPanel();
        private JTextField IDField;

        public DialogNewVertex(int defaultID) {
            IDField = new JTextField("" + defaultID);
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
    
    static class DialogNewGraph implements ActionListener{
        private JPanel panel = new JPanel();
        String[] graphTypes = { "Empty", "Clique", "Circle", "Grid", "Wheel", "Random" };
        String[] Labels1    = { "", "Vertices", "Vertices", "Rows", "Sides", "Vertices" };
        String[] Labels2    = { "", "", "", "Columns", "", "Edges" };
        //String[] vertexNumbers = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "12", "15", "20", "30", "50" };
        private JComboBox choose = new JComboBox(graphTypes);
        private JCheckBox edges = new JCheckBox();
        private JTextField TF1 = new JTextField();
        private JTextField TF2 = new JTextField();
        private JLabel[] labels = new JLabel[2];

        public DialogNewGraph() {
            labels[0] = new JLabel();
            labels[1] = new JLabel();
            panel.setLayout(new GridLayout(4, 2, 5, 5));
            panel.add(new JLabel("Type "));
            choose.addActionListener(this);
            panel.add(choose);
            panel.add(labels[0]);
            panel.add(TF1);
            panel.add(labels[1]);
            panel.add(TF2);
            panel.add(new JLabel("Create edges? "));
            panel.add(edges);
            edges.setSelected(true);
            actionPerformed(null);            
        }

        public int getTF1() {
            try {
                Integer res = Integer.parseInt(TF1.getText());
                return res;
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        public int getTF2() {
            try {
                Integer res = Integer.parseInt(TF2.getText());
                return res;
            } catch (NumberFormatException e) {
                return -1;
            }
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
            labels[0].setText(Labels1[choose.getSelectedIndex()]);
            if (labels[0].getText().length() == 0) TF1.setVisible(false);
            else TF1.setVisible(true);
            labels[1].setText(Labels2[choose.getSelectedIndex()]);
            if (labels[1].getText().length() == 0) TF2.setVisible(false);
            else TF2.setVisible(true);
            if (getTF1()<1) TF1.setText("5");
            if (getTF2()<1) TF2.setText("5");
            
        }
    }

}
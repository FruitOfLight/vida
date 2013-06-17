package ui;

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

import algorithms.ModelSettings;
import enums.GraphType;
import enums.Property;

public class Dialog {

    public static void showError(String errorMessage) {
        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Message", JOptionPane.PLAIN_MESSAGE);
    }

    public static class DialogNewVertex {
        private JPanel panel = new JPanel();
        private InputField IDField;

        public DialogNewVertex(int defaultID) {
            IDField = new InputField("" + defaultID);
            panel.setLayout(new GridLayout(2, 2, 5, 5));
            panel.add(new JLabel("ID: "));
            panel.add(IDField);
        }

        public int getID() {
            if (GUI.player.model.settings.isProperty(Property.anonym))
                return 0;
            return IDField.getInt(0, 0, 1000000);
        }

        public JComponent getPanel() {
            return panel;
        }
    }

    public static class DialogNewGraph implements ActionListener {
        private JPanel panel = new JPanel();
        String[] graphTypes = { "Empty", "Clique", "Cycle", "Grid", "Wheel", "Random" };
        String[][] labelTexts = { { "", "Vertices", "Vertices", "Rows", "Sides", "Vertices" },
                { "", "", "", "Columns", "", "Edges" } };

        private JComboBox<String> choose = new JComboBox<String>(graphTypes);
        private JCheckBox edges = new JCheckBox();
        private InputField[] inputFields = new InputField[2];
        private JLabel[] labels = new JLabel[2];

        public DialogNewGraph(GraphType graphType, boolean force) {
            panel.setLayout(new GridLayout(4, 2, 5, 5));
            panel.add(new JLabel("Type "));
            choose.setSelectedIndex(graphType.ordinal());
            choose.addActionListener(this);
            if (force && graphType != GraphType.any)
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

    public static class DialogProgramSettings implements ActionListener {
        private JPanel panel = new JPanel();

        String[] graphTypes = { "Any", "Clique", "Cycle", "Grid", "Wheel" };
        String[] modelTypes = { "Default", "Leader Election", "Broadcast", "Traversal" };

        private JComboBox<String> chooseGraphType, chooseModelType;
        private JComponent[] setters = new JComponent[Property.values().length];
        private String[] captions = { "Anonym", "Synchroned", "Graph type", "Model type" };

        public DialogProgramSettings(ModelSettings settings) {
            panel.setLayout(new GridLayout(4, 2, 5, 5));
            // TODO zautomatizovat
            setters[0] = new JCheckBox("", settings.isProperty(Property.anonym));
            setters[1] = new JCheckBox("", settings.isProperty(Property.synchroned));
            setters[2] = chooseGraphType = new JComboBox<String>(graphTypes);
            setters[3] = chooseModelType = new JComboBox<String>(modelTypes);

            for (int i = 0; i < setters.length; ++i) {
                panel.add(new JLabel(captions[i]));
                panel.add(setters[i]);
                setters[i].setEnabled(!settings.getLocked(Property.values()[i]));
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Dialog.DialogNewGraph newGraphDialog = new Dialog.DialogNewGraph(
                    GraphType.values()[chooseGraphType.getSelectedIndex()], true);
            JOptionPane.showMessageDialog(null, newGraphDialog.getPanel(), "New graph",
                    JOptionPane.OK_CANCEL_OPTION);
            GUI.player.graph.createNew(newGraphDialog);
        }

        public void apply(ModelSettings settings) {
            // TODO automatizovat
            settings.setProperty(Property.anonym, ((JCheckBox) setters[0]).isSelected());
            settings.setProperty(Property.synchroned, ((JCheckBox) setters[1]).isSelected());
            settings.setProperty(Property.graphType, chooseGraphType.getSelectedIndex());
            settings.setProperty(Property.model, chooseModelType.getSelectedIndex());
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
                Dialog.showError("Value is not valid integer. Default value " + def
                        + " was used instead\n");
            return def;
        }
    }

}
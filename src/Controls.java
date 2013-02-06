import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Controls implements Drawable {

    JPanel panel;
    Canvas canvas;
    JButton playButton;
    JButton stopButton;
    JButton pauseButton;
    JButton forwardButton;
    JButton backwardButton;
    JLabel speedLabel;
    JLabel programLabel;

    public Controls() {
        panel = new JPanel();
        canvas = new Canvas(this);
        createButtons();
        speedLabel = new JLabel();
        programLabel = new JLabel();
        panel.removeAll();
        panel.add(programLabel);
        panel.add(playButton);
        panel.add(pauseButton);
        panel.add(backwardButton);
        panel.add(stopButton);
        panel.add(forwardButton);
        panel.add(speedLabel);
        canvas.repaint();
    }

    public void createButtons() {
        try {
            BufferedImage buttonIcon = ImageIO.read(new File("images/playButton.jpg"));
            playButton = new JButton(new ImageIcon(buttonIcon));
            withoutBorder(playButton);
            buttonIcon = ImageIO.read(new File("images/pauseButton.jpg"));
            pauseButton = new JButton(new ImageIcon(buttonIcon));
            withoutBorder(pauseButton);
            buttonIcon = ImageIO.read(new File("images/stopButton.jpg"));
            stopButton = new JButton(new ImageIcon(buttonIcon));
            withoutBorder(stopButton);
            buttonIcon = ImageIO.read(new File("images/forwardButton.jpg"));
            forwardButton = new JButton(new ImageIcon(buttonIcon));
            withoutBorder(forwardButton);
            buttonIcon = ImageIO.read(new File("images/backwardButton.jpg"));
            backwardButton = new JButton(new ImageIcon(buttonIcon));
            withoutBorder(backwardButton);
            playButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    GUI.model.start();
                    canvas.repaint();
                }
            });
            pauseButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    GUI.model.pause();
                    canvas.repaint();
                }
            });
            stopButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    GUI.model.stop();
                    GUI.graph.setDefaultValues();
                    GUI.graph.canvas.repaint();
                    canvas.repaint();
                }
            });
            forwardButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MessageQueue.getInstance().setSendSpeed(
                            MessageQueue.getInstance().getSendSpeed() * CONST.speedFactor);
                    canvas.repaint();
                }
            });
            backwardButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MessageQueue.getInstance().setSendSpeed(
                            MessageQueue.getInstance().getSendSpeed() / CONST.speedFactor);
                    canvas.repaint();
                }
            });
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void withoutBorder(JButton button) {
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
    }

    public void draw(Graphics2D g) {
        String programString = "";
        if (GUI.model.path.equals(""))
            programString = "none";
        else {
            int last = 0;
            for (int i = 0; i < GUI.model.path.length(); i++)
                if (GUI.model.path.charAt(i) == '/')
                    last = i;
            programString = GUI.model.path.substring(last + 1,
                    Math.max(GUI.model.path.length() - 4, last + 1));
        }
        programLabel.setText("Current program: " + programString);
        String speedString = ((Double) MessageQueue.getInstance().getSendSpeed()).toString();
        if (speedString.length() > 5)
            speedString = speedString.substring(0, 5);
        if (GUI.model.running == RunState.running) {
            pauseButton.setVisible(true);
            playButton.setVisible(false);
            speedLabel.setText("Running " + speedString);
        } else if (GUI.model.running == RunState.paused) {
            pauseButton.setVisible(false);
            playButton.setVisible(true);
            speedLabel.setText("Paused (" + speedString + ")");
        } else {
            pauseButton.setVisible(false);
            playButton.setVisible(true);
            speedLabel.setText("Stopped (" + speedString + ")");
        }
    }
}

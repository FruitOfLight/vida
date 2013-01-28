import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

public class Controls implements Drawable {

    Canvas canvas;
    JButton playButton;
    JButton stopButton;
    JButton pauseButton;
    JButton forwardButton;
    JButton backwardButton;
    JLabel speedLabel;

    public Controls() {
        setCanvas(new Canvas(this));
        createButtons();
        speedLabel = new JLabel();
        canvas.removeAll();
        canvas.add(playButton);
        canvas.add(pauseButton);
        canvas.add(backwardButton);
        canvas.add(stopButton);
        canvas.add(forwardButton);
        canvas.add(speedLabel);
        canvas.repaint();
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
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
                    GUI.model.graph = GUI.graph;
                    GUI.model.load();
                    canvas.repaint();
                }
            });
            pauseButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    GUI.model.graph = GUI.graph;
                    GUI.model.load();
                    canvas.repaint();
                }
            });
            stopButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    GUI.model.stop();
                    GUI.graph.canvas.repaint();
                    canvas.repaint();
                }
            });
            forwardButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MessageQueue.getInstance().setSendSpeed(MessageQueue.getInstance().getSendSpeed()*CONST.speedFactor);
                }
            });
            backwardButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MessageQueue.getInstance().setSendSpeed(MessageQueue.getInstance().getSendSpeed()/CONST.speedFactor);
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

    public void draw(Graphics g) {
        String speedString = ((Double)MessageQueue.getInstance().getSendSpeed()).toString();
        if (speedString.length()>5) speedString = speedString.substring(0, 5);
        if (GUI.model.running != RunState.running) {
            pauseButton.setVisible(false);
            playButton.setVisible(true);
            speedLabel.setText("Running " + speedString);
        } else if (GUI.model.running != RunState.paused) {
            pauseButton.setVisible(true);
            playButton.setVisible(false);
            speedLabel.setText("Paused (" + speedString +")");
        } else {
            pauseButton.setVisible(true);
            playButton.setVisible(false);
            speedLabel.setText("Stopped (" + speedString +")");
        }
    }

}

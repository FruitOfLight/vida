import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;


public class Controls implements Drawable {

	Canvas canvas;
	JButton playButton;
	JButton stopButton;
	JButton pauseButton;
	
	public Controls() {
		setCanvas(new Canvas(this));
		createButtons();
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
		}
		catch (Exception e) {System.out.println(e.toString());}
	}
	
	public void withoutBorder(JButton button) {
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setContentAreaFilled(false);
	}
	
	public void draw(Graphics g) {
		canvas.removeAll();
		if(!GUI.model.running) {
			canvas.add(playButton);
			canvas.add(stopButton);
		}
		else {
			canvas.add(pauseButton);
			canvas.add(stopButton);
		}
	}
	
}

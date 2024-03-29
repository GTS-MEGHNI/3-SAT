

import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;



public class Window {

	private JFrame frame;
	private String title;
	private int width, height;
	private Canvas canvas;
	
	
	public Window(String title, int width, int height) {
		this.title = title;
		this.width = width;
		this.height = height;
		
		createWindow();
	}
	public Canvas getCanvas() {
		return canvas;
	}
	public void createWindow() {
		frame = new JFrame(title);
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		Dimension dimension = new Dimension(width,height);
		canvas = new Canvas();
		canvas.setPreferredSize(dimension);
		canvas.setMaximumSize(dimension);
		canvas.setMinimumSize(dimension);
		frame.add(canvas);
		frame.pack();
	}
	
	
}

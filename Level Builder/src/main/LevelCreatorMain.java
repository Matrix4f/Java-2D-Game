package main;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import loop.Loop;

public class LevelCreatorMain {

	public static void main(String[] args) {
		boolean systemLAF = false;
		if(systemLAF)
			try {
				UIManager.setLookAndFeel(
						UIManager.getSystemLookAndFeelClassName());
			} catch(ClassNotFoundException | InstantiationException
					| IllegalAccessException
					| UnsupportedLookAndFeelException e) {
				JOptionPane.showMessageDialog(null,
						"There was an error with setting the system GUI theme.",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		Input input = new Input();

		DisplayFrame.create();
		DisplayFrame.setVisible(true);
		DisplayFrame.setSize(1000, 700);
		DisplayFrame.maximize();

		DisplayCanvas.create();
		DisplayCanvas.addKeyListener(input);
		DisplayCanvas.addMouseListener(input);
		DisplayCanvas.addMouseWheelListener(input);
		DisplayCanvas.addMouseMotionListener(input);

		DisplayFrame.add(DisplayCanvas.toAWTCanvas());
		DisplayCanvas.requestFocus();

		LevelCreator lc = new LevelCreator();
		Loop.run(lc, lc);
	}
}

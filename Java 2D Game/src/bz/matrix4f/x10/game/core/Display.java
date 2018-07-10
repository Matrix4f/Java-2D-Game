package bz.matrix4f.x10.game.core;

import java.awt.*;
import java.awt.Image;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import bz.matrix4f.x10.game.Game;

public class Display {
	
	private static Frame frame;
	private static Canvas canvas;
	
	public static void init() {
		frame = new Frame("Warlings of Sparta");
		frame.setResizable(false);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Game.close();
			}
		});

		canvas = new Canvas();
		frame.add(canvas);

		Toolkit tk = Toolkit.getDefaultToolkit();
		Image img = tk.createImage("data/res/texture/ui/cursor.png");
		Point hotspot = new Point(0, 0);
		Cursor cursor = tk.createCustomCursor(img, hotspot, "game-cursor");
		frame.setCursor(cursor);
	}

	public static int getXOffset() {
		return frame.getX() + canvas.getX();
	}

	public static int getYOffset() {
		return frame.getY() + canvas.getY();
	}

	public static void resize(int w, int h) {
		frame.setSize(w, h);
	}

	public static void show() {
		frame.setVisible(true);
		canvas.createBufferStrategy(3);
		canvas.requestFocus();
	}
	
	public static void center() {
		frame.setLocationRelativeTo(null);
	}
	
	public static Canvas toAWTCanvas() {
		return canvas;
	}
	
	public static Frame toAWTFrame() {
		return frame;
	}

	public static void addKeyListener(KeyListener listener) {
		canvas.addKeyListener(listener);
	}

	public static void removeKeyListener(KeyListener listener) {
		canvas.removeKeyListener(listener);
	}
}

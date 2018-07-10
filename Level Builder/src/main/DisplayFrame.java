package main;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JFrame;

import main.gui.Menubar;
import main.map.Level;

public class DisplayFrame {

	private static JFrame frame;
	public static Level lvl;

	public static void create() {
		frame = new JFrame();
		frame.requestFocus();
		// frame.setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Level Creator");
		maximize();
	}

	public static int getWidth() {
		return frame.getWidth();
	}

	public static int getHeight() {
		return frame.getHeight();
	}

	public static JFrame getFrame() {
		return frame;
	}

	public static void setMenuBar(Menubar toolbar) {
		frame.setJMenuBar(toolbar);
	}

	public static void maximize() {
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	public static void setSize(int w, int h) {
		frame.setSize(w, h);
	}

	public static void setVisible(boolean flag) {
		frame.setVisible(flag);
	}

	public static void add(Component comp) {
		frame.add(comp);
	}

	public static void setTitle(String title) {
		frame.setTitle(title);
	}

	public static void setCursor(Cursor cursor) {
		frame.setCursor(cursor);
	}

	public static void setLocation(int x, int y) {
		frame.setLocation(x, y);
	}

	public static void setLocation(Point p) {
		frame.setLocation(p);
	}

	public static int getX() {
		return frame.getX();
	}

	public static int getY() {
		return frame.getY();
	}

	public static Dimension getSize() {
		return frame.getSize();
	}

	public static void validate() {
		frame.invalidate();
		frame.validate();
	}

	public static void add(Component comp, Object constraints) {
		frame.add(comp, constraints);
	}
}

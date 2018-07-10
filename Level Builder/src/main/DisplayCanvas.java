package main;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;

import loop.Renderer;

public class DisplayCanvas {
	
	private static Canvas canvas;
	
	public static void create() {
		canvas = new Canvas();
	}
	
	public static void prepareAndRender(Renderer r) {
		BufferStrategy strat = canvas.getBufferStrategy();
		if(strat == null) {
			canvas.createBufferStrategy(3);
			strat = canvas.getBufferStrategy();
		}
		Graphics2D g = (Graphics2D) strat.getDrawGraphics();
		r.render(g);
		g.dispose();
		strat.show();
	}
	
	public static void resize(Dimension d) {
		canvas.setPreferredSize(d);
		canvas.setMaximumSize(d);
		canvas.setMinimumSize(d);
	}
	
	public static Canvas toAWTCanvas() {
		return canvas;
	}

	public static int getWidth() {
		return canvas.getWidth();
	}
	
	public static int getHeight() {
		return canvas.getHeight();
	}
	
	public static void addFocusListener(FocusListener l) {
		canvas.addFocusListener(l);
	}

	public static void removeFocusListener(FocusListener l) {
		canvas.removeFocusListener(l);
	}

	public static void addKeyListener(KeyListener l) {
		canvas.addKeyListener(l);
	}

	public static void removeKeyListener(KeyListener l) {
		canvas.removeKeyListener(l);
	}

	public static void addMouseListener(MouseListener l) {
		canvas.addMouseListener(l);
	}

	public static void removeMouseListener(MouseListener l) {
		canvas.removeMouseListener(l);
	}

	public static void addMouseMotionListener(MouseMotionListener l) {
		canvas.addMouseMotionListener(l);
	}

	public static void removeMouseMotionListener(MouseMotionListener l) {
		canvas.removeMouseMotionListener(l);
	}

	public static void addMouseWheelListener(MouseWheelListener l) {
		canvas.addMouseWheelListener(l);
	}

	public static void removeMouseWheelListener(MouseWheelListener l) {
		canvas.removeMouseWheelListener(l);
	}

	public static void requestFocus() {
		canvas.requestFocus();
	}	
}

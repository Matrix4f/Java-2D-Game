package bz.matrix4f.x10.game.ui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EventListener;

import bz.matrix4f.x10.game.core.Display;

public class UIObj {

	protected int x, y, w, h;
	protected Color fg = Color.BLACK, bg = Color.WHITE;
	protected ArrayList<EventListener> listeners = new ArrayList<>();
	protected static BufferedImage[][] icons;

	public UIObj(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public void tick() {
	}

	public void render(Graphics2D g) {
	}

	/**
	 * When the UIObj's Condition is loaded and destroyed,
	 * its event listeners must comply by (de)activating them.<br>
	 * Currently supports: KeyListener, MouseListener, MouseMotionListener, MouseWheelListener
	 */
	public void onLoad() {
		Canvas c = Display.toAWTCanvas();
		for(EventListener evt : listeners) {
			if(evt instanceof KeyListener)
				c.addKeyListener((KeyListener) evt);
			else if(evt instanceof MouseListener)
				c.addMouseListener((MouseListener) evt);
			else if(evt instanceof MouseMotionListener)
				c.addMouseMotionListener((MouseMotionListener) evt);
			else if(evt instanceof MouseWheelListener)
				c.addMouseWheelListener((MouseWheelListener) evt);
		}
	}

	public void onDestroy() {
		Canvas c = Display.toAWTCanvas();
		for(EventListener evt : listeners) {
			if(evt instanceof KeyListener)
				c.removeKeyListener((KeyListener) evt);
			else if(evt instanceof MouseListener)
				c.removeMouseListener((MouseListener) evt);
			else if(evt instanceof MouseMotionListener)
				c.removeMouseMotionListener((MouseMotionListener) evt);
			else if(evt instanceof MouseWheelListener)
				c.removeMouseWheelListener((MouseWheelListener) evt);
		}
	}

	protected void bindListener(EventListener evt) {
		listeners.add(evt);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Color getFg() {
		return fg;
	}

	public void setFg(Color fg) {
		this.fg = fg;
	}

	public Color getBg() {
		return bg;
	}

	public void setBg(Color bg) {
		this.bg = bg;
	}
}

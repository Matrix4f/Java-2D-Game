package bz.matrix4f.x10.game.core;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import bz.matrix4f.x10.game.cond.Condition;

public class GameLoop {

	public static long lastTime = System.currentTimeMillis();
	public static long now = lastTime;
	public static double delta = 1d;
	public static double accumulated = 0d;
	public static final int PER_SECOND = 30;
	public static final double RATE = 1000d / PER_SECOND;

	public static int CURRENT_SPEED = 0;
	public static int LAST_RUNS = 0;
	public static String RUNS_FORMATTED = "";

	private static long lastOutputTime = System.currentTimeMillis();
	private static final int delay = 1000;

	public void run(Canvas canvas) {
		while(true) {
			now = System.currentTimeMillis();
			accumulated += (now - lastTime) / RATE;
			lastTime = now;
			if(accumulated >= 1) {
				accumulated = 0;
				delta = (accumulated + 1) * 2;
				CURRENT_SPEED++;
				
				Condition.tickCurrent();
				
				BufferStrategy bs = canvas.getBufferStrategy();
				Graphics2D g = (Graphics2D) bs.getDrawGraphics();
				Condition.renderCurrent(g);
				g.dispose();
				bs.show();
			}
			
			//FPS code:
			if(System.currentTimeMillis() - lastOutputTime >= delay) {
				lastOutputTime += delay;
				LAST_RUNS = CURRENT_SPEED;
				CURRENT_SPEED = 0;

			}
		}
	}
}

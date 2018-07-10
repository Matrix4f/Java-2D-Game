package loop;

import main.DisplayCanvas;

public class Loop {
	
	public static volatile boolean running = false;
	public static int UPDATE_SPEED = 60;
	public static int RENDER_SPEED = 60;
	
	public static void run(Updater updater, Renderer r) {
		running = true;
		long lastTime = System.nanoTime();
		final double rendering = 1000000000 / RENDER_SPEED;
		final double updating = 1000000000 / UPDATE_SPEED;
		double deltaUpdating = 0d;
		double deltaRendering = 0d;
		
		long lastDebug = System.currentTimeMillis();
		int fps = 0, updates = 0;
		while(running) {
			long now = System.nanoTime();
			deltaUpdating += (now - lastTime) / rendering;
			deltaRendering += (now - lastTime) / updating;
			lastTime = now;
			while(deltaUpdating >= 1) {
				updater.update();
				deltaUpdating--;
				fps++;
			}
			if(deltaRendering >= 1) {
				DisplayCanvas.prepareAndRender(r);
				deltaRendering--;
				updates++;
			}
			
			if(System.currentTimeMillis() - lastDebug >= 1000) {
				lastDebug += 1000;
				System.out.println("Fps: " + fps + ", Updates: " + updates);
				fps = 0;
				updates = 0;
			}
		}
	}
}

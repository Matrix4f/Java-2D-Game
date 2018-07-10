package bz.matrix4f.x10.game.cond;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import bz.matrix4f.x10.game.Game;
import bz.matrix4f.x10.game.core.Loader;
import bz.matrix4f.x10.game.core.Resources;

public class ConditionResources extends Condition {

	public static volatile Object lock = new Object();
	private Thread continueOnFinish;

	public ConditionResources() {
		super(Type.LOADING_RESOURCES);
		continueOnFinish = new Thread("ContinueOnFinish") {
			@Override
			public void run() {
				synchronized (lock) {
					try {
						Resources.load();
						lock.wait();
						Resources.lastLoaded = "Loaded " + Loader.resourcesLoaded + " resources in " + Resources.timeTaken +"ms!";
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Condition.set(Type.MENU);
				}
			}
		};
		continueOnFinish.start();
	}

	@Override
	public void onLoad() {
	}

	@Override
	public void onExit() {
	}

	@Override
	public void tick() {
	}

	@Override
	public void render(Graphics2D g) {
		g.clearRect(0, 0, Game.WIDTH + 10, Game.HEIGHT + 10);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, Game.WIDTH + 10, Game.HEIGHT + 10);

		g.setFont(new Font("Consolas", Font.PLAIN, 70));
		g.setColor(new Color(0, 150, 0));
		String loadingTitle = "Loading...";
		g.drawString(loadingTitle, 50, 200);
		g.setFont(new Font("Consolas", Font.PLAIN, 30));
		g.drawString(Resources.lastLoaded, 50, 300);

	}

}

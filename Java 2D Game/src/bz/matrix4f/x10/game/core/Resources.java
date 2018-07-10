package bz.matrix4f.x10.game.core;

import java.awt.image.BufferedImage;

import bz.matrix4f.x10.game.cond.ConditionResources;

import static bz.matrix4f.x10.game.core.Loader.*;

public class Resources {
	
	public static String lastLoaded = "Initializing filesystem...";
	public static int timeTaken = 0;
	
	public static volatile BufferedImage[][] player;
	public static volatile BufferedImage[][] torch;
	public static volatile BufferedImage arrow;
	public static volatile BufferedImage chicken;
	public static volatile BufferedImage egg;
	
	public static volatile BufferedImage[][] tiles;
	
	public static volatile BufferedImage[][] items;
	public static volatile BufferedImage[][] invslot;
	public static volatile BufferedImage[][] uiIcons;
	public static volatile BufferedImage background1;
	public static volatile BufferedImage background2;
	
	public static void load() {
		new Thread("ResourceLoader") {
			@Override
			public void run() {
				long begin = System.currentTimeMillis();
				
				player = textureAtlas("entity/player", 24, 48);
				arrow = texture("entity/arrow");
				chicken = texture("entity/chicken");
				egg = texture("entity/egg");
				torch = textureAtlas("entity/torch", 24, 24);
				
				tiles = textureAtlas("map/tilemap", 24, 24);
				
				items = textureAtlas("map/item", 48, 48);
				invslot = textureAtlas("ui/invslot", 48, 48);
				background1 = texture("ui/background");
				background2 = texture("ui/background-2");
				uiIcons = textureAtlas("ui/uiicons", 8, 8);
				
				Log.print("Loaded resources.");
				long end = System.currentTimeMillis();
				timeTaken = (int) (end - begin);
				synchronized(ConditionResources.lock) {
					ConditionResources.lock.notify();
				}
			}
		}.start();
	}
	
}

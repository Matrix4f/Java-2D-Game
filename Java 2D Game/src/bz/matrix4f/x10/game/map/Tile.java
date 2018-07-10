package bz.matrix4f.x10.game.map;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Random;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import bz.matrix4f.x10.game.core.Resources;
import bz.matrix4f.x10.game.core.Toolbox;

public class Tile {
	
	public static final int WIDTH = 24, HEIGHT = WIDTH;
	
	private int id;
	private String name;
	private Map map;
	private int x, y;
	private BufferedImage img;
	private JSONObject data;
	
	private static boolean firstTile = true;
	
	private static BufferedImage[][] images;
	
	public Tile(int x, int y, Map map, int id) {
		this.map = map;
		this.id = id;
		
		if(firstTile) {
			firstTile = false;
			images = Resources.tiles;
		}
		
		img = images[id % 8][id / 8];
		this.name = TileInfo.get(id);
		this.x = x * WIDTH;
		this.y = y * HEIGHT;

		int[] rotIDs = {0,1,2,4,5};
		boolean canRot = false;
		for(int i: rotIDs)
			if(id == i)
				canRot = true;
		if(canRot) {
			Random random = new Random();
			img = Toolbox.rotateImage(random.nextInt(3) * 90, img, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		}

		data = new JSONObject();
	}
	
	@SuppressWarnings("unchecked")
	public void set(String prop, String val) {
		data.put(prop, val);
	}
	
	public String get(String prop) {
		return data.get(prop).toString();
	}
	
	public void setData(String data) { 
		this.data = (JSONObject) JSONValue.parse(data);
	}
	
	public String getData() {
		return data.toJSONString();
	}
	
	public void render(Graphics2D g) {
		g.drawImage(img, x, y, WIDTH, HEIGHT, null);
	}

	public static int getWidth() {
		return WIDTH;
	}

	public static int getHeight() {
		return HEIGHT;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Map getMap() {
		return map;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public Rectangle bounds() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}
	
	public static BufferedImage[][] getImages() {
		return images;
	}
}

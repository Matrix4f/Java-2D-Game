package main.map;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Block {

	private Level parent;
	private int id;
	private int x, y;
	private String name;
	private BufferedImage img;

	public Block(Level parent, int x, int y, int id) {
		this.parent = parent;
		this.id = id;
		this.img = parent.imgAt(new int[] {id % 8, id / 8});
		this.x = x;
		this.y = y;
		this.name = TileInfo.getName(id);
	}
	
	public Block copy(int x, int y) {
		return new Block(parent, x, y, id);
	}
	
	public void render(Graphics2D g) {
		g.drawImage(img, x, y, Level.BLOCK_SIZE, Level.BLOCK_SIZE, null);
		g.drawRect(x, y, Level.BLOCK_SIZE, Level.BLOCK_SIZE);
	}

	public Level getParent() {
		return parent;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public BufferedImage getImg() {
		return img;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}

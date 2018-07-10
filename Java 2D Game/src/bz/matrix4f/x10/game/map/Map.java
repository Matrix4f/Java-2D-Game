package bz.matrix4f.x10.game.map;

import java.awt.Graphics2D;

import bz.matrix4f.x10.game.Game;
import bz.matrix4f.x10.game.gfx.Camera;

public class Map {
	
	private Tile[][] tiles;
	private Camera cam;
	
	public Map(int width, int height)  {
		tiles = new Tile[height][width];
		cam = Game.game.getCamera();
	}
	
	public int getWidth() {
		return tiles[0].length;
	}
	
	public int getHeight() {
		return tiles.length;
	}
	
	public void setTile(int x, int y, Tile tile) {
		tiles[x][y] = tile;
	}

	public void render(Graphics2D g) {
		for(int y = 0; y < tiles.length; y++) {
			for(int x = 0; x < tiles[y].length; x++) {
				if(tiles[x][y] == null || cam == null)
					continue;

				if(tiles[x][y].bounds().intersects(Camera.BOUNDS))
					tiles[x][y].render(g);
			}
		}
	}
	
	public boolean isTileAt(int x, int y, String string) {
		return tiles[x][y].getName().equalsIgnoreCase(string);
	}

	public Tile[][] getTiles() {
		return tiles;
	}

}

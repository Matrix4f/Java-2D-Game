package bz.matrix4f.x10.game.gfx;

import java.awt.Rectangle;

import bz.matrix4f.x10.game.Game;
import bz.matrix4f.x10.game.entity.SPPlayer;
import bz.matrix4f.x10.game.map.Map;
import bz.matrix4f.x10.game.map.Tile;

public class Camera {

	public static Rectangle BOUNDS = new Rectangle(0, 0, Game.WIDTH, Game.HEIGHT);

	private int x, y, xOff, yOff;
	private SPPlayer target;

	public Camera(SPPlayer p) {
		this.x = 0;
		this.y = 0;
		target = p;
	}

	public void move() {
		int xtol = 200;
		int ytol = 150;
		float mx = -target.getX() + Game.WIDTH / 2;
		float my = -target.getY() + Game.HEIGHT / 2;

		Map map = Game.game.getMap();
		if(map != null) {
			// Map width and height in pixels
			int mwp = map.getWidth() * Tile.WIDTH;
			int mhp = map.getHeight() * Tile.HEIGHT;

			xOff = (mwp > Game.WIDTH) ? 0 : -Tile.WIDTH - 10;
			yOff = (mhp > Game.HEIGHT) ? 0 : -Tile.HEIGHT;

			// Use the tolerance around the center to change the camera's X and
			// Y
			if(mwp > Game.WIDTH) {
				if(x < mx - xtol)
					x = (int) (mx - xtol);
				else if(x > mx + xtol)
					x = (int) (mx + xtol);
			}
			if(mhp > Game.HEIGHT) {
				if(y < my - ytol)
					y = (int) (my - ytol);
				else if(y > my + ytol)
					y = (int) (my + ytol);
			}

			// Lock the camera to edge of the map
			if(mwp > Game.WIDTH && x > 0)
				x = 0;
			if((-x + Game.WIDTH) > mwp)
				x = Game.WIDTH - mwp;
			if(mhp > Game.HEIGHT && y > 0)
				y = 0;
			if((-y + Game.HEIGHT) > mhp)
				y = Game.HEIGHT - mhp;
		}
		x += xOff;
		y += yOff;

        BOUNDS.x = -x;
        BOUNDS.y = -y;
	}

	public Rectangle bounds() {
		return new Rectangle((int) -x, (int) -y, Game.WIDTH, Game.HEIGHT);
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
}

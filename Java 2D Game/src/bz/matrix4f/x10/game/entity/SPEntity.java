package bz.matrix4f.x10.game.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import bz.matrix4f.x10.game.Game;
import bz.matrix4f.x10.game.core.Renderer;
import bz.matrix4f.x10.game.core.Ticker;

public abstract class SPEntity implements Ticker, Renderer {

	protected float x, y, vx, vy;
	protected int width, height;
	protected String uuid;
	protected static Game game = Game.game;
	protected double maxHealth = 20;
	protected double health = maxHealth;
	protected boolean firstInWorld;
	private static List<Class<?>> entitiesInWorld = new ArrayList<>();

	public SPEntity(float x, float y, int w, int h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		uuid = UUID.generate();
		firstInWorld = !entitiesInWorld.contains(getClass());
		if(!entitiesInWorld.contains(getClass()))
			entitiesInWorld.add(getClass());
		System.out.println(getClass().getSimpleName() + " is " + firstInWorld);
	}

	public abstract void tick();

	public abstract void render(Graphics2D g);

	protected void moveRegular() {
		if(vx != 0 || vy != 0) {
			x += vx;
			y += vy;
		}
	}

	public float mx() {
		return x + (width / 2f);
	}

	public float my() {
		return y + (height / 2f);
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getVx() {
		return vx;
	}

	public void setVx(float vx) {
		this.vx = vx;
	}

	public void setUUID(String uuid) {
		this.uuid = uuid;
	}

	public float getVy() {
		return vy;
	}

	public void setVy(float vy) {
		this.vy = vy;
	}

	public Rectangle bounds() {
		return new Rectangle((int) x, (int) y, width, height);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getUUID() {
		return uuid;
	}

	public void setHealth(double health) {
		this.health = health;
	}

	public double getHealth() {
		return health;
	}

	public double getMaxHealth() {
		return maxHealth;
	}

	public String getUuid() {
		return uuid;
	}
}

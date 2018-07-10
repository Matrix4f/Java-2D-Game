package bz.matrix4f.x10.game.networking.server.entities;

import bz.matrix4f.x10.game.networking.packet.Packet0bMove;
import bz.matrix4f.x10.game.networking.packet.Packet0nEntityHealth;
import bz.matrix4f.x10.game.networking.server.Server;

import java.awt.Rectangle;

public abstract class MPEntity {
	
	protected float x, y, vx, vy;
	protected int width, height;
	protected String uuid;
	protected Server server;
	protected final double maxHealth = 20;
	protected double health = 20;
	
	public MPEntity(float x, float y, int w, int h, String uuid, Server server) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		this.uuid = uuid;
		this.server = server;
	}


	public abstract boolean tick();

	protected void sendMovePacket() {
		Packet0bMove move = new Packet0bMove((int) x, (int) y, vx, vy, uuid);
		server.sendPacketToAllClients(move);
	}

	protected void move() {
		if(vx != 0)
			x += vx;
		if(vy != 0)
			y += vy;
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
		Packet0nEntityHealth packet = new Packet0nEntityHealth(getUUID(), health);
		server.sendPacketToAllClients(packet);
	}

	public void damage(double damage) {
		setHealth(getHealth() - damage);
	}

	public void heal(double healpower) {
		setHealth((getHealth() + healpower > maxHealth) ? maxHealth : getHealth() + healpower);
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

package bz.matrix4f.x10.game.entity;

import java.awt.Graphics2D;

import bz.matrix4f.x10.game.inventory.InvItem;

public class SPNetPlayer extends SPPlayer {

	private String addr;
	private String username;
	private InvItem selectedItem;

	public SPNetPlayer(float x, float y, String username, String addr, String uuid) {
		super(x, y);
		this.username = username;
		this.uuid = uuid;
		this.addr = addr;
	}
	
	public void tick() {
		super.tick();
	}
	
	@Override
	public void render(Graphics2D g) {
		super.render(g);
	}

	public InvItem getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(InvItem selectedItem) {
		this.selectedItem = selectedItem;
	}

	public String getAddr() {
		return addr;
	}

	public String getUsername() {
		return username;
	}
}

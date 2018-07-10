package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;

import loop.Renderer;
import loop.Updater;
import main.gui.Menubar;
import main.gui.Toolbar;
import main.map.Level;

public class LevelCreator implements Renderer, Updater {
	
	private Level lvl;
	private Toolbar tb;
	private Menubar mb;
	public static LevelCreator This;
	
	public LevelCreator() {
		This = this;
		lvl = new Level(32,32);
		Input.lvl = lvl;
		DisplayFrame.setMenuBar(mb = new Menubar(lvl));
		tb = new Toolbar(lvl);
		DisplayFrame.add(tb, BorderLayout.PAGE_END);
		DisplayFrame.validate();
	}
	
	@Override
	public void update() {
	}

	@Override
	public void render(Graphics2D g) {
		g.clearRect(0, 0, DisplayFrame.getSize().width, DisplayFrame.getSize().height);
		g.setColor(Color.DARK_GRAY.darker());
		g.fillRect(0, 0, DisplayCanvas.getWidth(), DisplayCanvas.getHeight());
		lvl.render(g);
	}

	public Level getLvl() {
		return lvl;
	}

	public void setLvl(Level lvl) {
		this.lvl.getSel().dispose();
		this.lvl.getTod().dispose();
		this.lvl = lvl;
		tb.setLvl(lvl);
		mb.setLvl(lvl);
		Input.lvl = lvl;
	}
}

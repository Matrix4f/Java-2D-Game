package bz.matrix4f.x10.game.ui;

import java.awt.Graphics2D;
import java.util.LinkedList;

import bz.matrix4f.x10.game.core.Resources;

public class UI {

	private LinkedList<UIObj> objs = new LinkedList<>();
	private static boolean firstUI = true;
	
	public UI() {
		if(firstUI) {
			firstUI = false;
			UIObj.icons = Resources.uiIcons;
		}
	}
	
	public void onLoad() {
		for(UIObj obj : objs)
			obj.onLoad();
	}

	public void onDestroy() {
		for(UIObj obj : objs)
			obj.onDestroy();
	}

	public void tick() {
		for(UIObj obj : objs)
			obj.tick();
	}

	public void add(UIObj obj) {
		objs.add(obj);
	}

	public void remove(UIObj obj) {
		obj.onDestroy();
		objs.remove(obj);
	}

	public void render(Graphics2D g) {
		for(int i = 0; i < objs.size(); i++)
			objs.get(i).render(g);
	}

	
	public LinkedList<UIObj> getObjs() {
		return objs;
	}
}

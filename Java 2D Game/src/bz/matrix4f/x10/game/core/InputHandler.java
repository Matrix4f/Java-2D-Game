package bz.matrix4f.x10.game.core;

import java.awt.Canvas;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.Field;
import java.util.HashMap;

import bz.matrix4f.x10.game.Game;
import bz.matrix4f.x10.game.entity.GameControls;
import bz.matrix4f.x10.game.entity.SPPlayer;
import bz.matrix4f.x10.game.inventory.Inv;
import bz.matrix4f.x10.game.inventory.InvItem;

public class InputHandler implements MouseListener, MouseWheelListener,
		KeyListener, MouseMotionListener {

	private SPPlayer target;
	private HashMap<String, Integer> strKeybinds;
	private HashMap<Integer, String> strReverseBinds;
	private HashMap<Character, Integer> keybinds;
	private HashMap<Integer, Character> reverseBinds;

	public InputHandler() {
		Canvas c = Display.toAWTCanvas();
		c.addMouseListener(this);
		c.addMouseWheelListener(this);
		c.addMouseMotionListener(this);
		c.addKeyListener(this);

		target = Game.game.getSPPlayer();
		bindKeys();
	}

	private void bindKeys() {
		strKeybinds = new HashMap<>();
		strReverseBinds = new HashMap<>();
		keybinds = new HashMap<>();
		reverseBinds = new HashMap<>();

		registerKeybinds("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
				"l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x",
				"y", "z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0");
	}

	@Override
	public void keyPressed(KeyEvent e) {	
		int key = e.getKeyCode();
		char c = Character.MIN_VALUE;
		try {
			c = reverseBinds.get(key);
		} catch(NullPointerException ex) {
		}
		// SPPlayer movement
		if(Game.game.getChat() != null && !Game.game.getChat().isEnabled() && !Game.game.getInv().isOpen()) {
			float speed = (float) (2f * GameLoop.delta);
			if(c == GameControls.UP) {
				target.setVy(-speed);
			} else if(c == GameControls.DOWN) {
				target.setVy(speed);
			} else if(c == GameControls.RIGHT) {
				target.setVx(speed);
			} else if(c == GameControls.LEFT) {
				target.setVx(-speed);
			}
		}
		if(c == GameControls.INVENTORY && !Game.game.getChat().isEnabled()) {
			Game.game.getInv().setOpen(!Game.game.getInv().isOpen());
		}
		//Debug screen
		if(c == 'i' && !Game.game.getChat().isEnabled() && !Game.game.getInv().isOpen())
			Game.game.getDebugGfx().visible = !Game.game.getDebugGfx().visible;
			
		//Chat
		if(c == 'c' && !Game.game.getInv().isOpen()) {
			Game.game.getChat().setEnabled(true);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		char c = Character.MIN_VALUE;
		try {
			c = reverseBinds.get(key);
		} catch(NullPointerException ex) {
		}
		////////////////// Player movement ////////////////////////
		// Resets the speed
		boolean stopsPlayerMovement = (c == GameControls.UP || c == GameControls.DOWN || c == GameControls.RIGHT || c
				== GameControls.LEFT);
		if(c == GameControls.UP) {
			target.setVy(0);
		} else if(c == GameControls.DOWN) {
			target.setVy(0);
		} else if(c == GameControls.RIGHT) {
			target.setVx(0);
		} else if(c == GameControls.LEFT) {
			target.setVx(0);
		}
		if(stopsPlayerMovement) {
			target.forceMovePacket(target.getX(), target.getY());
		}
	}

	private void registerKeybinds(String... names) {
		for(String str : names) {
			registerKeybind(str);
		}
	}

	private void registerKeybind(String varName) {
		registerKeybind(varName.charAt(0), varName);
	}

	private void registerKeybind(char val, String varName) {
		try {
			Field field = KeyEvent.class
					.getDeclaredField("VK_" + varName.toUpperCase());
			int value = Integer.parseInt(field.get(null).toString());

			strKeybinds.put(varName, value);
			strReverseBinds.put(value, varName);

			keybinds.put(val, value);
			reverseBinds.put(value, val);
		} catch(NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(!Game.game.getChat().isEnabled() && !Game.game.getInv().isOpen()) {
			Inv inv = Game.game.getInv();
			InvItem selected = inv.getSelectedItem();
			if(selected != null)
				selected.use();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

}

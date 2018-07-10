package bz.matrix4f.x10.game.cond;

import java.awt.Graphics2D;

import bz.matrix4f.x10.game.Game;

/**
 * This class represents the Game condition, where the user
 * is playing the Game itself, not editing a setting, nor is
 * on the Main Menu screen. It is a placeholder for the {@code Game.class},
 * because all "states" or "phases" should be a Condition. It is a wrapper
 * for the Game.
 * @see Game
 */
public class ConditionGame extends Condition {
	
	private Game game;
	
	public ConditionGame(Game game) {
		super(Type.GAME);
		
		this.game = game;
	}

	/**
	 * No GUI needs to be initialized or destroyed.
     */
	@Override
	public void onLoad() {
	}
	
	@Override
	public void onExit() {
	}

	/**
	 * However, it must update the Game and render it. That is the
	 * whole point of a Wrapper class.
     */
	@Override
	public void tick() {
		game.tick();
	}
	
	@Override
	public void render(Graphics2D g) {
		game.render(g);
	}
}

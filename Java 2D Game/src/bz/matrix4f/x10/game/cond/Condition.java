package bz.matrix4f.x10.game.cond;

import java.awt.Graphics2D;
import java.util.HashMap;

/**
 * This class is used to represent the Game's "states", or different
 * stages of the game, such as in the Menu screen, playing the Game, adding a Server,
 * etc.
 */
public abstract class Condition {
	
	public static enum Type {
		MENU, GAME, SETTINGS, SERVER, SERVER_CREATOR, LOADING_RESOURCES;
	}

	/**
	 * The current condition that the game is in
     */
	private static Condition current;
	/**
	 * A map of all possible conditions, with a Type and the Condition itself.
     */
	private static HashMap<Type, Condition> conds = new HashMap<>();

	/**
	 * Every condition has a Type that separates it from
	 * others.
	 * @see Type
	 */
	protected Type type;

	/**
	 * Initializes a condition object, with the sepcified type.
	 * There may be no more than 1 condition with the same type,
	 * @param type The type of this Condition
     */
	public Condition(Type type) {
		this.type = type;
	}

	/**
	 * Updates the current condition of the game. For example,
	 * if the player is in the Menu screen, it will update the Menu screen.
     */
	public static void tickCurrent() {
		if(current != null)
			current.tick();
	}

	/**
	 * Renders the current condition of the game on the screen. For example,
	 * if the player is in the GameCondition, it draw the game.
	 */
	public static void renderCurrent(Graphics2D g) {
		if(current != null)
			current.render(g);
	}

	/**
	 * Sets the current condition to the one provided.<br>
	 * This will call the {@code onExit()} method on the
	 * old condition, de-initializing it.<br>It will also
	 * call the {@code onLoad()} method on the new condition,
	 * initializing it.
	 * @param cond The new condition
     */
	public static void set(Type cond) {
		if(current != null)
			current.onExit();
		current = conds.get(cond);
		if(current != null)
			current.onLoad();
	}

	/**
	 * Gets the current condition of the game.
	 * @return The current condition
     */
	public static Condition get() {
		return current;
	}

	/**
	 * This registers all of the conditions to the HashMap conds,
	 * allowing them to be set to the current condition.<br>
	 * <strong>NOTE:</strong> This will not initialize the Conditions themselves, but rather, will <i>allow</i> them
	 * to be loaded.
	 * @param conditions The conditions to be registered.
     */
	public static void registerAll(Condition... conditions) {
		for(Condition cond: conditions)
			conds.put(cond.type, cond);
	}

	/**
	 * This method is called when the Condition is loaded. It should initialize GUI components and do
	 * any other initialization code. This procedure is called when the current condition is set to {@code this}.
     */
	public abstract void onLoad();

	/**
	 * This method is called when the Condition is exited. For example, going from the Main Menu to the Settings screen
	 * would result in the {@code onExit()} method being called in the {@code Main Menu Condition}.
     */
	public abstract void onExit();

	/**
	 * This method is called to tick, or update, the current condition. For example, in the GameCondition,
	 * this would update the Game.
     */
	public abstract void tick();

	/**
	 * This renders the condition to the Display, effectively "drawing" it.
	 * @param g The Graphics2D object to be used for drawing
     */
	public abstract void render(Graphics2D g);
}

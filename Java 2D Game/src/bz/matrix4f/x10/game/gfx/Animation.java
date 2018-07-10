package bz.matrix4f.x10.game.gfx;

import java.awt.image.BufferedImage;

/**
 * A generic animation in the game, such as the player's feet moving while
 * he/she runs.
 * 
 * @author vgsoh_000
 */
public class Animation {

	public static final int HORIZ_VERT = 0;
	public static final int VERT_HORIZ = 1;
	
	// The sprites to loop through
	private BufferedImage[] cycle;
	// The current frame that the animation is using
	private int currentFrameIndex = 0;
	// The amount of ticks that should pass before the animation should update
	private int ticksPerFrame;
	// Counts the ticks until they reach ticksPerFrame
	private int ticksCounter = 0;

	/**
	 * Initalizes an Animation.
	 * 
	 * @param cycle
	 *            The sprites that the animation should loop through
	 * @param ticksPerFrame
	 *            The amount of ticks that should pass before the animation
	 *            should update
	 */
	public Animation(BufferedImage[] cycle, int ticksPerFrame) {
		this.cycle = cycle;
		this.ticksPerFrame = ticksPerFrame;
	}

	/**
	 * Initializes an animation, but calculates a rectangular area between the
	 * coordinates provided.
	 * 
	 * @param fullSpriteSheet
	 *            The matrix of bufferedimages loaded from the file
	 * @param x1
	 *            The Rectangle's first x-coord
	 * @param y1
	 *            The rectangle's first y-coord
	 * @param x2
	 *            The Rectangle's second x-coord
	 * @param y2
	 *            The rectangle's second y-coord
	 * @param ticksPerFrame
	 *            The amount of ticks that should pass before the animation
	 *            should update
	 * @param direction Either {@code HORIZ_VERT} or {@code VERT_HORIZ}. Determines
	 * 			  which is given supremacy over the other.
	 */
	public Animation(BufferedImage[][] fullSpriteSheet, int x1, int y1, int x2,
			int y2, int direction, int ticksPerFrame) {
		//Calculate the rectangle area
		//Adds 1 because it is 0-indexed
		int len = (x2 - x1 + 1) * (y2 - y1 + 1);
		int index = 0;
		
		BufferedImage[] buf = new BufferedImage[len];
		if(direction == HORIZ_VERT) {
			for(int y = y1; y <= y2; y++) {
				for(int x = x1; x <= x2; x++) {
					buf[index++] = fullSpriteSheet[x][y];
				}
			}
		} else {
			for(int x = x1; x <= x2; x++) {
				for(int y = y1; y <= y2; y++) {
					buf[index++] = fullSpriteSheet[x][y];
				}
			}
		}
		this.cycle = buf;
		this.ticksPerFrame = ticksPerFrame;
	}

	/**
	 * Updates the animation and moves it to the next frame if necessary
	 */
	public void tick() {
		if(ticksCounter >= ticksPerFrame) {
			ticksCounter = 0;
			currentFrameIndex++;
			if(currentFrameIndex >= cycle.length)
				currentFrameIndex = 0;
		}
		ticksCounter++;
	}

	/**
	 * @return The current frame calculated by the animation.
	 */
	public BufferedImage getCurrentFrame() {
		return cycle[currentFrameIndex];
	}

	public BufferedImage[] getCycle() {
		return cycle;
	}

	public int getCurrentFrameIndex() {
		return currentFrameIndex;
	}

	public int getTicksPerFrame() {
		return ticksPerFrame;
	}

	public int getTicksCounter() {
		return ticksCounter;
	}
}

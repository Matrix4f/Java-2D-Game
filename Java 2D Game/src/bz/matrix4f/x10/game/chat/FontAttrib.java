package bz.matrix4f.x10.game.chat;

/**
 * Represents a font attribute
 * @author vgsoh_000
 */
public class FontAttrib {
	
	//The character of the attribute. Example: 'b' for bold
	private char value;
	//Whether it was the expected value. Example: 'b' for bold
	private boolean isExpected;
	
	/**
	 * Initializes the font attribute with the character
	 * and the correct/expected character
	 * @param value The value that the attribute holds
	 * @param expected The value required for the attribute to be active
	 */
	public FontAttrib(char value, char expected) {
		this.value = value;
		isExpected = value == expected;
	}

	public char getValue() {
		return value;
	}
	
	/**
	 * @return Whether the value was the correct value
	 */
	public boolean isExpected() {
		return isExpected;
	}
}

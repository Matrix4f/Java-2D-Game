package bz.matrix4f.x10.game.chat;

/**
 * A single token (piece of text) with a specified color in the chat
 * @author vgsoh_000
 */
public class ChatToken {
	
	private String value; // Text
	private ChatColor color; // The color of the text
	private boolean bold, italic; //Whether the token is bold or italic
	
	/**
	 * Initializes the ChatToken
	 * @param value The text of the token
	 * @param color The index-in-regex color of the token.
	 */
	public ChatToken(String value, int color) {
		this.value = value;
		this.color = Chat.parseColor(color);
	}

	public String getValue() {
		return value;
	}

	public ChatColor getColor() {
		return color;
	}
	
	public boolean isBold() {
		return bold;
	}

	public ChatToken setBold(boolean bold) {
		this.bold = bold;
		return this;
	}

	public boolean isItalic() {
		return italic;
	}

	public ChatToken setItalic(boolean italic) {
		this.italic = italic;
		return this;
	}
	
	
}

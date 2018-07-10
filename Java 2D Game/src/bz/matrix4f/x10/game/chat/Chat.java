package bz.matrix4f.x10.game.chat;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bz.matrix4f.x10.game.Game;
import bz.matrix4f.x10.game.core.Display;
import bz.matrix4f.x10.game.networking.packet.Packet0fChatMsg;

/**
 * This class represents the in-game chat. It handles
 * things like rendering and tokenizers for the chat styles.
 * @author vgsoh_000
 */
public class Chat {
	
	/**
	 * Whether the player's key presses register to the chat or the game
	 */
	public static boolean HAS_FOCUS = false;
	/**
	 * The KeyListener object that runs code when a key is pressed
	 */
	private KeyListener listener;
	/**
	 * The text currently being typed by the player
	 */
	private String textBuffer = "";
	/**
	 * The string used to split the textBuffer. This helps find the positions of the
	 * colors.
	  	 Red
		 Orange
		 Yellow
		 Green
		 Blue
		 Violet
		 Pink
		 Black
		 White
		 Brown
		 Gray
	
		 Example string: :r:R:o:A:y:I:g:N:b:B:v:O:p:W:bl:Z:w:!:br:!:gy:! returns
		 RAINBOWZ!!!
	 */
	public static String colorRegex = "(:r:|:o:|:y:|:g:|:b:|:v:|:p:|:bl:|:w:|:br:|:gy:)";
	/**
	 * The old/previous chat messages that this user or another user has sent.
	 */
	private List<ChatItem> textQueue = new ArrayList<>();
	
	/**
	 * Initializes the key listener and tells it what to do.
	 */
	public Chat() {
		listener = new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int k = e.getKeyCode();
				char c = e.getKeyChar();
				boolean alt = e.isAltDown();
				boolean shift = e.isShiftDown(); // Whether the SHIFT key is pressed

				if(k != KeyEvent.VK_BACK_SPACE && k != KeyEvent.VK_ENTER) {
					// Regular character
					if(!e.isControlDown()) {
						//Removes all invalid characters from the string:
						String str = Character.toString(c).replace(
								Character.toString(KeyEvent.CHAR_UNDEFINED),
								"");
						//Concatenates it to the text buffer
						if(str.length() >= 1)
							textBuffer += str.charAt(0);
					} else {
						// Paste mechanic
						if(k == KeyEvent.VK_V && !alt && !shift) {
							try {
								Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
								String str = clipboard.getData(DataFlavor.stringFlavor).toString();
								textBuffer += str;
							} catch(Exception e1) {
								e1.printStackTrace();
							}
						} else if(k == KeyEvent.VK_C && !alt && !shift) {
						}
					}
				} else if(k == KeyEvent.VK_BACK_SPACE) {
					if(textBuffer.length() >= 1)
						textBuffer = textBuffer.substring(0,
								textBuffer.length() - 1);
				} else if(k == KeyEvent.VK_ENTER) {
					setEnabled(false);
					if(!textBuffer.startsWith("~")) {
						//Send a packet to the server about the chat message
						Packet0fChatMsg packet = new Packet0fChatMsg(":bl:" + textBuffer);
						packet.sendToServer();
					} else {
						executeCommand(textBuffer.substring(1));
					}
					clear();
				}
			}
		};
	}
	
	/**
	 * Executes a client-side command
	 */
	private void executeCommand(String cmd) {
		if(cmd.equals("hcon")) { //Hide console
			Game.game.getClient().getGui().setVisible(false);
		} else if(cmd.equals("scon")) { //Show console
			Game.game.getClient().getGui().setVisible(true);
		}
	}
	
	
	/**
	 * This method displays a chat message in the chat by adding
	 * it to the textQueue.
	 * @param msg The ChatMessage to be sent to the queue.
	 */
	public void queueMessage(ChatItem msg) {
		textQueue.add(msg);
	}
	
	/**
	 * The chat's update method. It decreases the opacity of all ChatItems
	 * until they are removed.
	 */
	public void tick() {
		Iterator<ChatItem> iter = textQueue.iterator();
		while(iter.hasNext()) {
			ChatItem item = iter.next();
			item.tick(iter);
		}
	}
	
	/**
	 * Adds/removes the KeyListener on the Display when the chat is (not)
	 * toggled.
	 */
	public void refresh() {
		if(HAS_FOCUS) {
			Display.addKeyListener(listener);
		} else {
			Display.removeKeyListener(listener);
		}
	}
	
	/**
	 * Renders the chat
	 * @param g The Graphics2D object to be used.
	 */
	public void render(Graphics2D g) {
		g.setFont(new Font("Calibri", Font.PLAIN, 16));
		FontMetrics fm = g.getFontMetrics();

		if(HAS_FOCUS)
			drawTextbuffer(fm, g);
		drawMessageQueue(fm, g);
	}

	private void drawMessageQueue(FontMetrics fm, Graphics2D g) {
		//The current y-position of the current message being rendered
		int dy = Game.HEIGHT - 50;

		//How much space (in y-pixels) every message will take
		int messageHeight = fm.getHeight() + 10;

		for(int i = 0; i < textQueue.size(); i++) {
			ChatItem item = textQueue.get(i);

			//Make enough space for the new message
			dy -= messageHeight;

			// Make the background color fade with the chat's lifetime
			double ratio = item.lifetime * 100 / (double) ChatItem.maxLifetime;
			g.setColor(new Color(0, 0, 0, (int) ratio));
			g.fillRect(0, dy - g.getFontMetrics().getHeight(), Game.WIDTH + 10,
					messageHeight);

			// Width is used for rendering the colors in chat.
			// Is the pointer for each new section of color's x value
			int width = 0;

			for(ChatToken token : item.tokens) {
				drawStyledString(g, toAWTColor(token.getColor()), token.getValue(), token.isBold(), token.isItalic(), width, dy);
				width += fm.stringWidth(token.getValue());
			}
		}
	}

	private void drawTextbuffer(FontMetrics fm, Graphics2D g) {
		int height = 50;

		//Draw the background of the focused chat
		g.setColor(new Color(0, 0, 0, 75));
		g.fillRect(0, Game.HEIGHT - height - 10, Game.WIDTH + 10, height + 10);
		g.drawRect(0, Game.HEIGHT - height - 10, Game.WIDTH + 10, height + 10);

		//Draw the text the user is currently typing
		g.setColor(Color.BLACK);
		g.drawString(textBuffer, 5, Game.HEIGHT - height - 10 + g.getFontMetrics().getHeight());

		//Draw the caret at the end of the string
		int caretHeight = fm.getHeight() + 2;
		int caretWidth = 1;
		int caretStartX = fm.stringWidth(textBuffer) + 4;
		int caretStartY = Game.HEIGHT - height - 7;

		g.setColor(Color.black);
		g.fillRect(caretStartX, caretStartY, caretWidth, caretHeight);
	}
	
	/**
	 * The function used to draw strings of a custom color in a single line.
	 * @param g The Graphics2D object to be used.
	 * @param color The Color of the text
	 * @param text The text to be rendered
	 * @param bold Whether it should be bold
	 * @param italic Whether it should be italic
	 * @param dx The x-coordinate
	 * @param dy The y-coordinate
	 */
	private void drawStyledString(Graphics2D g, Color color,
			String text, boolean bold, boolean italic, int dx, int dy) {
		int style = Font.PLAIN;
		if(bold || italic)
			if(bold && !italic)
				style = Font.BOLD;
			else if(italic && !bold)
				style = Font.ITALIC;
			else if(bold && italic)
				style = Font.BOLD | Font.ITALIC;
		
		Font font = g.getFont();
		font = font.deriveFont(style);
		g.setFont(font);
		g.setColor(color);
		g.drawString(text, 5 + dx, dy);
	}
	
	/**
	 * This function converts the individual strings- without
	 * color codes- and the indices that define color codes
	 * into a full ChatToken.
	 * @param strTokens The array of individual strings without color codes.
	 * @param colorIndices The indices that define where the colors go
	 * @return a ChatToken[]  that has the specified colors and text
	 */
	private ChatToken[] toTokens(String[] strTokens, int[] colorIndices) {
		ChatToken[] tokens = new ChatToken[strTokens.length];
		for(int i = 0; i < strTokens.length; i++) {
			String str = strTokens[i];
			int properties = 2;
			
			if(str.startsWith("*"))
				tokens[i] = new ChatToken(str.substring(properties + 1), colorIndices[i]);
			else
				tokens[i] = new ChatToken(str, colorIndices[i]);
			
			ChatToken tk = tokens[i];
			if(str.startsWith("*")) {
				String propStr = str.substring(1, 1 + properties);
				if(propStr.length() > properties) {
					propStr = propStr.substring(0, properties);
				} else if(propStr.length() < properties) {
					//Add in '0's for every character that is missing.
					int difference = properties - propStr.length();
					char fillChar = '0';
					char[] appendChars = new char[difference];
					Arrays.fill(appendChars, fillChar);
					propStr += new String(appendChars);
				}
				char[] propChars = propStr.toCharArray();
				FontAttrib bold = new FontAttrib(propChars[0], 'b');
				FontAttrib italic = new FontAttrib(propChars[1], 'i');
				tk.setBold(bold.isExpected());
				tk.setItalic(italic.isExpected());
			}
		}
		return tokens;
	}
	
	/**
	 * This converts the ChatColor enum into an AWT color.
	 * @param color The ChatColor to be converted
	 * @return The ChatColor's AWT form
	 */
	public static Color toAWTColor(ChatColor color) {
		switch(color) {
			case BLUE:
				return Color.BLUE.brighter();
			case GREEN:
				return Color.GREEN.darker().darker();
			case ORANGE:
				return Color.ORANGE;
			case PINK:
				return Color.PINK.darker();
			case RED:
				return Color.RED;
			case VIOLET:
				return Color.MAGENTA.darker();
			case YELLOW:
				return Color.YELLOW.darker();
			case BLACK:
				return Color.BLACK;
			case BROWN:
				return new Color(122, 64, 5).brighter();
			case GRAY:
				return Color.DARK_GRAY;
			case WHITE:
				return Color.WHITE;
		}
		return null;
	}

	/**
	 * This converts the index into a ChatColor
	 * @param index The location in the color regex of the color.
	 * @return the ChatColor of the index
	 */
	public static ChatColor parseColor(int index) {
		switch(index) {
			case 0:
				return ChatColor.RED;
			case 1:
				return ChatColor.ORANGE;
			case 2:
				return ChatColor.YELLOW;
			case 3:
				return ChatColor.GREEN;
			case 4:
				return ChatColor.BLUE;
			case 5:
				return ChatColor.VIOLET;
			case 6:
				return ChatColor.PINK;
			case 7:
				return ChatColor.BLACK;
			case 8:
				return ChatColor.WHITE;
			case 9:
				return ChatColor.BROWN;
			case 10:
				return ChatColor.GRAY;
		}
		return null;
	}
	
	/**
	 * This function converts the chat message into
	 * an int[].
	 * @param regex The Regex to be used for splitting.
	 * @param str The String that should be split by the regex
	 * @param offset The value every index-in-regex should be changed by
	 * @return the int[] indices that define colors for the string.
	 */
	private int[] toIndices(String regex, String str, int offset) {
		int len = splitWithoutFirstElement(regex, str).length;
		int[] indices = new int[len];
		// The regex without first: '(' and last: ')'
		String raw = regex.substring(1, regex.length() - 1);
		String[] regices = raw.split("\\|");
		List<String> regicesList = new ArrayList<>(Arrays.asList(regices));

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		int indexOfIndices = 0;
		while(matcher.find()) {
			// Finds which element of regex it is
			String substring = str.substring(matcher.start(), matcher.end());
			int indexInList = regicesList.indexOf(substring);
			indices[indexOfIndices++] = indexInList + offset;
		}
		return indices;
	}
	
	/**
	 * This function splits a string according to a regex and
	 * removes the first element.
	 * @param regex The regex the string will be split by
	 * @param object The String to be split
	 * @return the array of strings resulted after splitting the object
	 */
	private String[] splitWithoutFirstElement(String regex, String object) {
		String[] data = object.split(regex);
		List<String> list = new ArrayList<>();
		for(String str : data)
			list.add(str);
		list.remove(0);
		return list.toArray(new String[list.size()]);
	}
	
	/**
	 * @return Whether the chat is in focus or not
	 */
	public boolean isEnabled() {
		return HAS_FOCUS;
	}
	
	/**
	 * @param enabled Whether the chat should be in focus or not
	 */
	public void setEnabled(boolean enabled) {
		if(HAS_FOCUS == enabled)
			return;
		HAS_FOCUS = enabled;
		refresh();
	}
	
	/**
	 * Toggles the chat's state
	 */
	public void toggle() {
		setEnabled(!isEnabled());
	}
	
	/**
	 * Sets the textBuffer to ""
	 */
	public void clear() {
		textBuffer = "";
	}
	
	/**
	 * A class that represents every chat message
	 * @author vgsoh_000
	 */
	public class ChatItem {
		/**
		 * The chat tokens
		 */
		private ChatToken[] tokens;
		/**
		 * The maximum opacity
		 */
		private static final int maxLifetime = 240;
		/**
		 * The chat message's current opacity
		 */
		private int lifetime = maxLifetime;

		public ChatItem(String text) {
			// Makes the default color of text black
			if(!text.startsWith(":")) {
				text = ":bl:" + text;
			}
			// strTokens refers to the Strings after they have been split up by
			// the color codes
			String[] strTokens = splitWithoutFirstElement(colorRegex, text);
			// Each index in indices points to an element in the regex, which is
			// used for chat coloring
			// :bl:hi:pl:asdf:bd:bye
			int[] colorIndices = toIndices(colorRegex, text, 0);
			tokens = toTokens(strTokens, colorIndices);

		}
		
		/**
		 * Decreases the lifetime and then removes the
		 * chat message.
		 * @param iterator
		 */
		public void tick(Iterator<ChatItem> iterator) {
			if(lifetime > 0)
				lifetime--;
			else
				iterator.remove();
		}
	}
}

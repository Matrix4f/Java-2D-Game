package bz.matrix4f.x10.game.networking.packet;

import java.io.PrintStream;

import bz.matrix4f.x10.game.Game;

public class Packet {

	public static final String SEPERATOR = "@";

	protected String id;
	protected String data;

	public Packet(Object NULL) {}
	
	public Packet(String id, String data) {
		this.id = id;
		this.data = data;
	}

	public void writeData(PrintStream out) {
		out.println(id + data);
		out.flush();
	}

	public String getData() {
		return data;
	}

	public void setData(Object... data) {
		this.data = concat(data);
	}
	
	public void sendToServer() {
		writeData(Game.game.getClient().getPrint());
	}
	
	public static String concat(Object... data) {
		String concat = "";
		for(Object str: data)
			concat += str.toString() + SEPERATOR;
		
		return concat.substring(0, concat.length() - SEPERATOR.length());
	}

	public String getId() {
		return id;
	}
}

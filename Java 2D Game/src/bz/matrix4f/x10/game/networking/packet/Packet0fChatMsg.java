package bz.matrix4f.x10.game.networking.packet;

public class Packet0fChatMsg extends Packet {

	public Packet0fChatMsg(String msg) {
		super("0f", concat(msg));
	}

}

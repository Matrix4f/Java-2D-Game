package bz.matrix4f.x10.game.networking.packet;

public class Packet0aLogin extends Packet {

	public Packet0aLogin(String username, String uuid, float x, float y, String sessionID) {
		super("0a", concat(username, uuid, x, y, sessionID));
	}
}

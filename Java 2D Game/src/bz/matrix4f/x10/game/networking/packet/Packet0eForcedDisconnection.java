package bz.matrix4f.x10.game.networking.packet;

public class Packet0eForcedDisconnection extends Packet {

	public Packet0eForcedDisconnection(String reason) {
		super("0e", reason);
	}

}

package bz.matrix4f.x10.game.networking.packet;

public class Packet0bMove extends Packet {

	public Packet0bMove(int x, int y, float vx, float vy, String uuid) {
		super("0b", concat(x, y, vx, vy, uuid));
	}
}

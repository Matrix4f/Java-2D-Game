package bz.matrix4f.x10.game.networking.packet;

public class Packet0nEntityHealth extends Packet {

    public Packet0nEntityHealth(String uuid, double newHealth) {
        super("0n", concat(uuid, newHealth));
    }
}

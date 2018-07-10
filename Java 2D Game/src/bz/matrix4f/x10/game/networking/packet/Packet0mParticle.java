package bz.matrix4f.x10.game.networking.packet;

public class Packet0mParticle extends Packet {

    public Packet0mParticle(int amount, int x, int y, double multiply, int lifetime) {
        super("0m", concat(amount, x, y, multiply, lifetime));
    }
}

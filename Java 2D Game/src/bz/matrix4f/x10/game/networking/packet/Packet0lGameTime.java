package bz.matrix4f.x10.game.networking.packet;

/**
 * Created by Matrix4f on 6/9/2016.
 */
public class Packet0lGameTime extends Packet {
    public Packet0lGameTime(int alpha) {
        super("0l", concat(alpha));
    }
}

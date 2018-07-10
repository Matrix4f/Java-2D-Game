package bz.matrix4f.x10.game.networking.packet;

/**
 * Created by Matrix4f on 5/30/2016.
 */
public class Packet0kRemoveEntity extends Packet {
    public Packet0kRemoveEntity(String uuid) {
        super("0k", uuid);
    }
}

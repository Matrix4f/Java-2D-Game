package bz.matrix4f.x10.game.networking.packet;

/**
 * Created by Matrix4f on 5/23/2016.
 */
public class Packet0hItemUse extends Packet {

    public Packet0hItemUse(int mouseX, int mouseY) {
        super("0h", concat(mouseX, mouseY));
    }
}

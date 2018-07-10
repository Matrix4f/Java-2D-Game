package bz.matrix4f.x10.game.networking.packet;

import org.json.simple.JSONObject;

/**
 * Created by Matrix4f on 5/23/2016.
 */
public class Packet0iInvUpdate extends Packet {
    public Packet0iInvUpdate(JSONObject data) {
        super("0i", "");
        this.data = data.toJSONString();
    }
}

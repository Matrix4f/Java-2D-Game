package bz.matrix4f.x10.game.networking.server;

import bz.matrix4f.x10.game.inventory.Inv;
import bz.matrix4f.x10.game.inventory.InvItem;
import bz.matrix4f.x10.game.networking.packet.Packet0iInvUpdate;
import bz.matrix4f.x10.game.networking.server.entities.MPEntity;
import bz.matrix4f.x10.game.networking.server.entities.MPPlayer;
import org.json.simple.JSONObject;

public class PlayerInteractor {

    public static void setItem(String key, InvItem item, ClientConn conn, Server server) {
        JSONObject obj = server.generateItemJSON(item, key, "set");

        MPPlayer affected = server.getPlayerByConnection(conn);
        while (affected == null)
            affected = server.getPlayerByConnection(conn);
        Inv inv = affected.getInv();
        inv.set(item, key);

        Packet0iInvUpdate packet = new Packet0iInvUpdate(obj);
        packet.writeData(conn.getPrint());
    }

    public static void setEntityHealth(double hp, MPEntity affected, Server server) {
        affected.setHealth(hp);
    }
}

package bz.matrix4f.x10.game.networking.server.entities;

import bz.matrix4f.x10.game.entity.SPPlayer;
import bz.matrix4f.x10.game.inventory.Inv;
import bz.matrix4f.x10.game.inventory.InvItem;
import bz.matrix4f.x10.game.networking.packet.Packet0iInvUpdate;
import bz.matrix4f.x10.game.networking.server.ClientConn;
import bz.matrix4f.x10.game.networking.server.Server;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.Iterator;

public class MPPlayer extends MPEntity {

    private static final int shootArrowMax = 45;
    private String username;
    private String addr;
    private Inv inv;
    private boolean canShootArrow = true;
    private int shootArrowTimer = 0;

    public MPPlayer(float x, float y, String uuid, String username, String addr, Server server) {
        super(x, y, SPPlayer.WIDTH, SPPlayer.HEIGHT, uuid, server);
        this.username = username;
        this.addr = addr;
        inv = new Inv(true);
    }

    @Override
    public boolean tick() {
        if (shootArrowTimer <= 0 && !canShootArrow) {
            shootArrowTimer = shootArrowMax;
            canShootArrow = true;
        } else {
            shootArrowTimer--;
        }

        if (health <= 0) {
            server.kickPlayer(username, "You died!!! :)");
            return true;
        }
        return false;
    }

    public void setItem(InvItem item, String key) {
        JSONObject obj = server.generateItemJSON(item, key, "set");

        inv.set(item, key);

        Packet0iInvUpdate packet = new Packet0iInvUpdate(obj);
        ClientConn conn = server.getConnectionByPlayer(this);
        packet.writeData(conn.getPrint());
    }

    public void registerAttack(int mouseX, int mouseY, int damage) {
        float reachBlocks = 1.5f;
        int reach = (int) (reachBlocks * 32);
        Rectangle rect = new Rectangle(mouseX, mouseY, 1, 1);
        Rectangle reachBounds = new Rectangle((int) mx() - reach,
                                             (int) my() - reach,
                                             (int) mx() + reach * 2,
                                             (int) my() + reach * 2);
        if(!rect.intersects(reachBounds))
            return;

        Iterator<MPEntity> iter = server.getEntities().getData().values().iterator();

        while (iter.hasNext()) {
            MPEntity entity = iter.next();
            if (entity == this)
                continue;
            if (entity.bounds().intersects(rect)) {
                entity.damage(damage);
                server.addParticles(damage, (int) entity.mx(), (int) entity.my(), 1.5, 20);
            }
        }
    }

    public boolean isCanShootArrow() {
        return canShootArrow;
    }

    public void setCanShootArrow(boolean x) {
        canShootArrow = x;
    }

    public Inv getInv() {
        return inv;
    }

    public String getUsername() {
        return username;
    }

    public String getAddr() {
        return addr;
    }
}

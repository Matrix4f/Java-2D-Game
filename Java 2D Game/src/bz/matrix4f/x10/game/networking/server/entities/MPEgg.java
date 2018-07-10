package bz.matrix4f.x10.game.networking.server.entities;

import bz.matrix4f.x10.game.networking.packet.Packet0mParticle;
import bz.matrix4f.x10.game.networking.server.PlayerInteractor;
import bz.matrix4f.x10.game.networking.server.Server;

public class MPEgg extends MPEntity {

    public static final int LIFETIME = 100;
    private int lifeleft = LIFETIME;
    private boolean madeParticles = false;

    public MPEgg(float x, float y, String uuid, Server server) {
        super(x, y, 16, 16, uuid, server);
    }

    @Override
    public boolean tick() {
        move();
        sendMovePacket();
        if (lifeleft <= 0) {
            return true;
        } else
            lifeleft--;
        for(int i = 0; (i < server.clients().size() && !madeParticles); i++) {
            MPPlayer player = server.clients().get(i).getPlayer();
            if(player != null && player.bounds().intersects(bounds())) {
                server.sendPacketToAllClients(new Packet0mParticle(2, (int) player.mx(), (int) player.my(), 1, 15));
                PlayerInteractor.setEntityHealth(player.getHealth() - .1, server.clients().get(i).getPlayer(), server);
                madeParticles = true;
            }
        }
        return false;
    }
}

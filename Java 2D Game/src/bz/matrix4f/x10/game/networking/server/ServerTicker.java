package bz.matrix4f.x10.game.networking.server;

import bz.matrix4f.x10.game.core.Log;
import bz.matrix4f.x10.game.networking.packet.Packet0kRemoveEntity;
import bz.matrix4f.x10.game.networking.server.entities.MPEntity;

import java.util.*;


/**
 * Created by Matrix4f on 5/30/2016.
 */
public class ServerTicker implements Runnable {

    private Server server;

    public ServerTicker(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        long lastTime = System.currentTimeMillis();
        double accumulated = 0;
        final double RATE = 1000d / 30d;
        long lastOutputTime = System.currentTimeMillis();
        int delay = 1000;

        while(!server.isStopped()) {
            long now = System.currentTimeMillis();
            accumulated += (now - lastTime) / RATE;
            lastTime = now;
            while(accumulated >= 1) {
                accumulated -= 1;
                tick();
            }

            //FPS code:
            if(System.currentTimeMillis() - lastOutputTime >= delay) {
                lastOutputTime = System.currentTimeMillis();
            }
        }
    }

    private void tick() {
        try {
            Collection<MPEntity> entities = server.getEntities().getData().values();
            MPEntity[] ents = entities.toArray(new MPEntity[entities.size()]);
            for (int i = 0; i < ents.length; i++) {
                boolean isRemoved = ents[i].tick();
                if (isRemoved) {
                    Packet0kRemoveEntity packet = new Packet0kRemoveEntity(ents[i].getUUID());
                    server.sendPacketToAllClients(packet);
                    entities.remove(ents[i]);
                }
            }
            server.getCycle().tick();
        } catch (ConcurrentModificationException e) {
            Log.err("ConcurrentModificationException because too many entities.");
        }
    }
}

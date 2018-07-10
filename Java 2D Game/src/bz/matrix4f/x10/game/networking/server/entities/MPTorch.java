package bz.matrix4f.x10.game.networking.server.entities;

import bz.matrix4f.x10.game.networking.server.Server;

public class MPTorch extends MPEntity {

    public MPTorch(float x, float y, String uuid, Server server) {
        super(x, y, 1, 1, uuid, server);
    }

    @Override
    public boolean tick() {
        return false;
    }
}

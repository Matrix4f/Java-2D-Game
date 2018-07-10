package bz.matrix4f.x10.game.networking.server.entities;

import java.util.ArrayList;
import java.util.List;

import bz.matrix4f.x10.game.core.Toolbox;
import bz.matrix4f.x10.game.networking.server.Server;

/**
 * Created by Matrix4f on 5/30/2016.
 */
public class MPArrow extends MPEntity {

    public static final int LIFETIME = 200;

    private float rotation;
    private int lifeleft = LIFETIME;
    private boolean hasCollided = false;
    private int damage;

    public MPArrow(float x, float y, float desX, float desY, String uuid, Server server) {
        super(x, y, 24, 24, uuid, server);

        rotation = Toolbox.getAngle(x, y, desX, desY) + 45;

        boolean useTrig = true;

        if (!useTrig) {
            /*
            * Calculates slope to find velX and velY
             */
            vx = (desX - x) / 25f;
            vy = (desY - y) / 25f;
        } else {
            /*
            * Uses trigonometry to find velX and velY
             */
            float opposite = desY - y;
            float adjacent = desX - x;
            float hypotenuse = (float) Math.sqrt(opposite * opposite + adjacent * adjacent);
            
            float cosine = adjacent / hypotenuse;
            float sine = opposite / hypotenuse;
            int scale = 17;
            vx = cosine * scale;
            vy = sine * scale;
        }
    }

    @Override
    public boolean tick() {
        move();
        if (lifeleft <= 0) {
            return true;
        } else
            lifeleft--;
        sendMovePacket();
        int ticksAlive = (LIFETIME - lifeleft);
        damage = ticksAlive / 30 + 1;

        List<MPEntity> entities = new ArrayList<>(server.getEntities().getData().values());
        for (int i = 0; (i < entities.size() && !hasCollided); i++) {
            MPEntity entity = entities.get(i);
            if (entity != null && entity != this && !(entity instanceof MPEgg)
                    && ticksAlive > 5 && entity.bounds().intersects(bounds())) {
                server.addParticles(damage, (int) x, (int) y, 1, 25);
                entity.damage(damage);
                hasCollided = true;
                return true;
            }
        }
        return false;
    }

    public int getDamage() {
        return damage;
    }

    public float getRotation() {
        return rotation;
    }
}

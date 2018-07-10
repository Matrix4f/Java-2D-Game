package bz.matrix4f.x10.game.entity;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import bz.matrix4f.x10.game.core.Resources;

/**
 * Created by Matrix4f on 5/30/2016.
 */
public class SPArrow extends SPEntity {

    private float rotation;
    private static BufferedImage sprite;

    public SPArrow(float x, float y, float vx, float vy, float rotation) {
        super(x, y, 24, 24);
        this.rotation = rotation;
        setVx(vx);
        setVy(vy);
        if(firstInWorld)
        	sprite = Resources.arrow;
    }

    @Override
    public void tick() {
        moveRegular();
    }

    @Override
    public void render(Graphics2D g) {
        AffineTransform tx = new AffineTransform();
        tx.rotate(Math.toRadians(rotation), sprite.getWidth() / 2, sprite.getHeight() / 2);
        g.drawImage(new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR).filter(sprite, null), (int) x, (int) y, null);
    }
}

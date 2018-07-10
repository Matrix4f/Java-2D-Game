package bz.matrix4f.x10.game.entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import bz.matrix4f.x10.game.core.Resources;

public class SPChicken extends SPEntity {

    private static BufferedImage sprite;

    public SPChicken(float x, float y) {
        super(x, y, 48, 48);
        if(firstInWorld)
        	sprite = Resources.chicken;
    }

    @Override
    public void tick() {
        moveRegular();
    }

    @Override
    public void render(Graphics2D g) {
        g.drawImage(sprite, (int) x, (int) y, width, height, null);
    }
}

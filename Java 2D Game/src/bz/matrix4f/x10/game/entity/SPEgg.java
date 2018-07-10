package bz.matrix4f.x10.game.entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import bz.matrix4f.x10.game.core.Resources;

public class SPEgg extends SPEntity {

    private static BufferedImage img;

    public SPEgg(float x, float y) {
        super(x, y, 12, 12);
        if(firstInWorld)
        	img = Resources.egg;
    }

    @Override
    public void tick() {
        moveRegular();
    }

    @Override
    public void render(Graphics2D g) {
        g.drawImage(img, (int) x, (int) y, width, height, null);
    }
}

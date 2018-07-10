package bz.matrix4f.x10.game.entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import bz.matrix4f.x10.game.Game;
import bz.matrix4f.x10.game.core.Resources;
import bz.matrix4f.x10.game.gfx.Animation;
import bz.matrix4f.x10.game.lighting.LightMap;
import bz.matrix4f.x10.game.lighting.LightSource;
import bz.matrix4f.x10.game.map.Tile;

/**
 * Created by vgsoh_000 on 4/30/2016.
 */
public class SPTorch extends SPEntity {

    private LightSource source;
    private static BufferedImage[][] sprites;
    private static Animation anim;

    public SPTorch(float x, float y) {
        super(x, y, Tile.WIDTH, Tile.HEIGHT);
        source = new LightSource((int) x + width / 2, (int) y + height / 2, 64, LightMap.ALPHA_VALUE);
        Game.game.getLightMap().lights.add(source);
        
        if(firstInWorld)
        	sprites = Resources.torch;
        anim = new Animation(sprites, 0, 0, 3, 0, Animation.HORIZ_VERT, 10);
    }


    public void tick() {
        anim.tick();
        source.setMaxAlpha(LightMap.ALPHA_VALUE);
    }

    public void render(Graphics2D g) {
        g.drawImage(anim.getCurrentFrame(), (int) x, (int) y, width, height, null);
    }
}

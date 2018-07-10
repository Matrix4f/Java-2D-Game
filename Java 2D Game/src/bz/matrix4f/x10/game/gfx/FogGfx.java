package bz.matrix4f.x10.game.gfx;

import java.awt.*;

import bz.matrix4f.x10.game.Game;
import bz.matrix4f.x10.game.lighting.LightMap;

public class FogGfx {

    private RadialGradientPaint fog;

    public FogGfx() {
        float r = 0 / 255f;
        float g = 0 / 255f;
        float b = LightMap.ALPHA_VALUE / 255f;

        fog = new RadialGradientPaint(Game.WIDTH / 2, Game.HEIGHT / 2, Game.WIDTH / 2 - 100,
                new float[]{0f, 1f},
                new Color[]{new Color(r, g, b, 0), new Color(r, g, b, LightMap.ALPHA_VALUE / 255f * .25f)});
    }

    public void render(final Graphics2D g) {
        Paint oldPaint = g.getPaint();
        g.setPaint(fog);
        g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
        g.setPaint(oldPaint);
    }
}

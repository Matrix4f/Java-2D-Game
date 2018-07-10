package bz.matrix4f.x10.game.gfx;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import bz.matrix4f.x10.game.Game;
import bz.matrix4f.x10.game.core.GameLoop;
import bz.matrix4f.x10.game.entity.SPPlayer;
import bz.matrix4f.x10.game.lighting.LightMap;
import bz.matrix4f.x10.game.map.Tile;

public class DebugGfx {

    public static final long MEGA_BYTE = 1024 * 1024;

    public boolean visible = false;
    private SPPlayer player = Game.game.getSPPlayer();

    public void render(Graphics2D g) {
        if (!visible)
            return;

        int y = 15;
        int x = 5;
        int yi = g.getFontMetrics().getHeight() + 5;
        g.setColor(new Color(35,0, 87));
        g.setFont(new Font("Monaco", Font.PLAIN, 14));

        g.drawString("Frames: " + GameLoop.LAST_RUNS + " / " + GameLoop.PER_SECOND, x, y);
        g.drawString("Location: [" + (int) player.getX() / Tile.WIDTH + "," + (int) player.getY() / Tile.HEIGHT + "]",
                x, y += yi);
        g.drawString("Lightmap.Alpha = " + LightMap.ALPHA_VALUE, x, y += yi * 2);
    }
}

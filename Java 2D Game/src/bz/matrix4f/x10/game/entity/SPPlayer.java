package bz.matrix4f.x10.game.entity;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import bz.matrix4f.x10.game.Game;
import bz.matrix4f.x10.game.core.GameLoop;
import bz.matrix4f.x10.game.core.Resources;
import bz.matrix4f.x10.game.gfx.Animation;
import bz.matrix4f.x10.game.gfx.Camera;
import bz.matrix4f.x10.game.inventory.InvItem;
import bz.matrix4f.x10.game.map.Tile;
import bz.matrix4f.x10.game.networking.client.Client;
import bz.matrix4f.x10.game.networking.packet.Packet0bMove;

public class SPPlayer extends SPEntity {

    public static final int WIDTH = 24;
    public static final int HEIGHT = 48;

    private enum Direction { LEFT, RIGHT }

    public static BufferedImage[][] sprites;

    private Animation runRight, runLeft, still, water_runLeft, water_runRight,
            water_still;
    private Animation anim;
    private boolean isInWater = false;
    private Direction lastDirection = Direction.LEFT;

    public SPPlayer(float x, float y) {
        super(x, y, WIDTH, HEIGHT);
        initAnimation();
    }

    @Override
    public void tick() {
        updateAnimation();
        movePlayer();
        anim.tick();
    }

    private void movePlayer() {
        if (vx != 0 || vy != 0) {
            if(!(this instanceof SPNetPlayer))
                forceMovePacket(x + vx, y + vy); // Send to the server that the player has moved
            if(vx < 0)
                lastDirection = Direction.LEFT;
            else
                lastDirection = Direction.RIGHT;
        }
    }

    public void forceMovePacket(float x, float y) {
        this.x = x;
        this.y = y;

        Packet0bMove packet = new Packet0bMove((int) x, (int) y, vx, vy, "&player;");
        packet.sendToServer();
    }

    @Override
    public void render(Graphics2D g) {
        g.drawImage(anim.getCurrentFrame(), (int) x, (int) y, width, height,
                null);
        renderUsername(g);
        renderCurrentItem(g);
        if(!(this instanceof SPNetPlayer))
            renderHealthBar(g);
    }

    private void renderHealthBar(Graphics2D g) {
        Camera cam = Game.game.getCam();
        g.translate(-cam.getX(), -cam.getY());

        int hbarfullw = 150;
        int hbarh = hbarfullw / 8;
        int hbarx = Game.WIDTH / 2 - hbarfullw / 2;
        int hbary = Game.HEIGHT - 100 - hbarh;
        int hbarw = (int) (health / maxHealth * hbarfullw);
        g.setColor(new Color(255, 255, 255, 50));
        g.fillRect(hbarx, hbary, hbarfullw, hbarh);
        g.setColor(new Color(0, 126, 29, 190));
        g.fillRect(hbarx, hbary, hbarw, hbarh);
        g.setColor(Color.black);
        g.drawRect(hbarx, hbary, hbarfullw, hbarh);

        g.translate(cam.getX(), cam.getY());
    }

    private void renderUsername(Graphics2D g) {
        if (!(this instanceof SPNetPlayer) && Client.USERNAME != null) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Consolas", Font.PLAIN, 13));
            g.drawString(Client.USERNAME, x, y - 5);
        }
    }

    private void renderCurrentItem(Graphics2D g) {
        InvItem item = null;
        if(!(this instanceof SPNetPlayer)) {
            item = Game.game.getInv().getSelectedItem();
        } else {
            item = ((SPNetPlayer) this).getSelectedItem();
        }
        if(item != null) {
            BufferedImage itemImg = item.getImg();
            int dx = (int) x - 8;
            int dy = (int) my() - 8;
            if(lastDirection == Direction.RIGHT)
                dx += WIDTH;

            g.drawImage(itemImg, dx, dy, 16, 16, null);
        }
    }

    private void initAnimation() {
    	if(firstInWorld)
   			sprites = Resources.player;
        int speed = (int) (10 / GameLoop.delta);
        
        still = new Animation(sprites, 0, 0, 1, 0, Animation.HORIZ_VERT, speed);
        runRight = new Animation(sprites, 2, 0, 5, 0, Animation.HORIZ_VERT, speed);
        runLeft = new Animation(sprites, 6, 0, 9, 0, Animation.HORIZ_VERT, speed);

        water_still = new Animation(sprites, 0, 1, 1, 1, Animation.HORIZ_VERT,
                speed);
        water_runRight = new Animation(sprites, 2, 1, 5, 1,
                Animation.HORIZ_VERT, speed);
        water_runLeft = new Animation(sprites, 6, 1, 9, 1, Animation.HORIZ_VERT,
                speed);

        anim = still;
    }

    private void updateAnimation() {
        try {
            if (game.getMap() != null) {
                int x = (int) mx() / Tile.WIDTH;
                int y = ((int) my() + width / 4) / Tile.HEIGHT + 1;
                if (!(x > game.getMap().getTiles().length || y > game.getMap().getTiles()[0].length || x < 0 || y < 0)) {
                    Tile tile = game.getMap().getTiles()[x][y];
                    isInWater = tile.getName().equalsIgnoreCase("water") || tile.getName().equalsIgnoreCase("lava");
                }
            }
            if (!isInWater) {
                if (vx == 0 && vy == 0 && anim != still)
                    anim = still;
                else if (vx > 0 && anim != runRight)
                    anim = runRight;
                else if (vx < 0 && anim != runLeft)
                    anim = runLeft;
            } else {
                if (vx == 0 && vy == 0 && anim != water_still)
                    anim = water_still;
                else if (vx > 0 && anim != water_runRight)
                    anim = water_runRight;
                else if (vx < 0 && anim != water_runLeft)
                    anim = water_runLeft;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
    }
}

package bz.matrix4f.x10.game.inventory;

import java.awt.image.BufferedImage;

import bz.matrix4f.x10.game.Game;
import bz.matrix4f.x10.game.core.Display;
import bz.matrix4f.x10.game.gfx.Camera;
import bz.matrix4f.x10.game.networking.packet.Packet0hItemUse;

public class InvItem {

    public static final int SPRITES_WIDTH = 4;

    private int id, count;
    private String name;
    private InvSlot parent;
    private BufferedImage img;

    public static BufferedImage[][] sprites;

    public InvItem(int id) {
    	if(sprites != null)
        	this.img = sprites[id % SPRITES_WIDTH][id / SPRITES_WIDTH];
        this.id = id;
        this.name = ItemRegistry.idNames.get(id);
        count = 1;
    }

    public void use() {
        int[] pos = Game.getMouseLocation();
        Camera cam = Game.game.getCamera();
        int xPos = pos[0] - Display.getXOffset() - cam.getX();
        int yPos = pos[1] - 24 - Display.getYOffset() - cam.getY();
        Packet0hItemUse packet = new Packet0hItemUse(xPos, yPos);
        packet.sendToServer();
    }

    public BufferedImage getImg() {
        return img;
    }

    public InvSlot getParent() {
        return parent;
    }

    public void setParent(InvSlot parent) {
        this.parent = parent;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "InvItem{" +
                "count=" + count +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", parent=" + parent +
                ", img=" + img +
                '}';
    }
}

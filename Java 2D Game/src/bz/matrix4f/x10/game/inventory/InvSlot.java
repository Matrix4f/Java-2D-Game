package bz.matrix4f.x10.game.inventory;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import org.json.simple.JSONObject;

import bz.matrix4f.x10.game.Game;
import bz.matrix4f.x10.game.core.Display;
import bz.matrix4f.x10.game.networking.packet.Packet0iInvUpdate;

/**
 * Created by Matrix4f on 5/16/2016.
 */
public class InvSlot {

    public static enum Type {
        storage, quickAccess, armory;
    }


    private InvItem child;
    private Type type;
    private int x, y;
    public static BufferedImage[][] sprites;
    private boolean focused = false;
    private String key;
    private int slot;
    private int yAtlas = 0;

    private MouseMotionListener motion;
    private MouseListener mouse;

    public InvSlot(final int x, final int y, int slot, int yAtlas, Type type) {
        this.x = x;
        this.y = y;
        this.slot = slot;
        this.type = type;
        this.yAtlas = yAtlas;
        key = type.toString() + " " + slot;

        motion = new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Rectangle btnBounds = new Rectangle(x, y, 48, 48);
                Rectangle mouseBounds = new Rectangle(e.getX(), e.getY(), 1, 1);
                if (mouseBounds.intersects(btnBounds)) {
                    focused = true;
                } else if (focused) {
                    focused = false;
                }
            }
        };
        mouse = new MouseListener() {
            public void mouseClicked(MouseEvent e) {
            }

            @SuppressWarnings("unchecked")
			@Override
            public void mousePressed(MouseEvent e) {
                Rectangle btnBounds = new Rectangle(x, y, 48, 48);
                Rectangle mouseBounds = new Rectangle(e.getX(), e.getY(), 1, 1);
                if (mouseBounds.intersects(btnBounds)) {
                    InvItem oldMouseItem = Game.game.getInv().getMouseItem();
                    if(oldMouseItem == null && child != null) { //Select new item
                        Game.game.getInv().setMouseItem(child);
                        child = null;
                    } else if(oldMouseItem != null) { //Swap items
                        Game.game.getInv().setMouseItem(null);
                        InvItem childCopy = child;
                        InvSlot slot1 = InvSlot.this;
                        InvSlot slot2 = oldMouseItem.getParent();
                        slot1.setChild(oldMouseItem);
                        slot2.setChild(childCopy);

                        JSONObject packetData = new JSONObject();
                        packetData.put("type", "swap");
                        packetData.put("s1", slot1.getKey());
                        packetData.put("s2", slot2.getKey());

                        Packet0iInvUpdate packet = new Packet0iInvUpdate(packetData);
                        packet.sendToServer();
                    }
                }
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        };
    }

    public void addListeners() {
        Display.toAWTCanvas().addMouseMotionListener(motion);
        Display.toAWTCanvas().addMouseListener(mouse);
    }

    public void removeListeners() {
        Display.toAWTCanvas().removeMouseMotionListener(motion);
        Display.toAWTCanvas().removeMouseListener(mouse);
    }

    public void render(Graphics2D g) {
        g.drawImage((!focused) ? sprites[0][yAtlas] : sprites[1][yAtlas], x, y, null);
        if (child != null)
            g.drawImage(child.getImg(), x, y, null);
    }

    public void render(Graphics2D g, int x, int y) {
        boolean isSelected = Game.game.getInv().getSelindex() == slot;
        g.drawImage((!isSelected) ? sprites[0][yAtlas] : sprites[1][yAtlas], x, y, null);
        if (child != null)
            g.drawImage(child.getImg(), x, y, null);
    }

    public Type getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isFocused() {
        return focused;
    }

    public String getKey() {
        return key;
    }

    public int getSlot() {
        return slot;
    }

    public int getyAtlas() {
        return yAtlas;
    }

    public MouseListener getMouse() {
        return mouse;
    }

    public MouseMotionListener getMotion() {
        return motion;
    }

    public InvItem getChild() {
        return child;
    }

    public void setChild(InvItem child) {
        this.child = child;
        if(child != null)
            child.setParent(this);
    }
}

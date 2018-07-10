package bz.matrix4f.x10.game.inventory;

import bz.matrix4f.x10.game.Game;
import bz.matrix4f.x10.game.core.Display;
import bz.matrix4f.x10.game.core.Resources;
import bz.matrix4f.x10.game.networking.packet.Packet0iInvUpdate;
import org.json.simple.JSONObject;

import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Created by Matrix4f on 5/16/2016.
 */
public class Inv {

    private InvSlot[] storage;
    private InvSlot[] armory;
    private InvSlot[] quickAccess;

    private InvItem mouseItem;
    private boolean open = false;
    private int selindex = 0;

    private MouseWheelListener scroll;

    public Inv(boolean server) {
        storage = new InvSlot[8];
        armory = new InvSlot[4];
        quickAccess = new InvSlot[4];


        for (int i = 0; i < storage.length; i++) {
            int x = 255 + ((i % 4) * 50);
            int y = 200 + ((i / 4) * 50);
            storage[i] = new InvSlot(x, y, i, 0, InvSlot.Type.storage);
        }
        if(!server) {
        	InvSlot.sprites = Resources.invslot;
        	InvItem.sprites = Resources.items;
        }
        
        armory = new InvSlot[4];
        armory[0] = new InvSlot(155, 100, 0, 1, InvSlot.Type.armory);
        armory[1] = new InvSlot(155, 150, 1, 2, InvSlot.Type.armory);
        armory[2] = new InvSlot(155, 200, 2, 3, InvSlot.Type.armory);
        armory[3] = new InvSlot(155, 250, 3, 4, InvSlot.Type.armory);

        quickAccess = new InvSlot[4];
        quickAccess[0] = new InvSlot(255, 100, 0, 5, InvSlot.Type.quickAccess);
        quickAccess[1] = new InvSlot(305, 100, 1, 0, InvSlot.Type.quickAccess);
        quickAccess[2] = new InvSlot(355, 100, 2, 0, InvSlot.Type.quickAccess);
        quickAccess[3] = new InvSlot(405, 100, 3, 0, InvSlot.Type.quickAccess);

        if (!server) { //Client is calling constructor
            scroll = new MouseWheelListener() {
                @SuppressWarnings("unchecked")
				@Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    int oldSelindex = selindex;
                    int val = (int) e.getPreciseWheelRotation();
                    selindex += val;
                    if (selindex > 3)
                        selindex = 3;
                    else if (selindex < 0)
                        selindex = 0;
                    if (selindex != oldSelindex) {
                        JSONObject obj = new JSONObject();
                        obj.put("type", "select");
                        obj.put("index", selindex);

                        Packet0iInvUpdate packet = new Packet0iInvUpdate(obj);
                        packet.sendToServer();
                    }
                }
            };
            Display.toAWTCanvas().addMouseWheelListener(scroll);
        }
    }

    public void render(Graphics2D g) {
        int x = Game.WIDTH / 2 - 100;
        int y = Game.HEIGHT - 87;


        quickAccess[0].render(g, x, y);
        quickAccess[1].render(g, x += 50, y);
        quickAccess[2].render(g, x += 50, y);
        quickAccess[3].render(g, x += 50, y);

        if (!open)
            return;

        for (InvSlot slot : storage)
            if (slot != null)
                slot.render(g);
        for (InvSlot slot : armory)
            if (slot != null)
                slot.render(g);
        for (InvSlot slot : quickAccess)
            if (slot != null)
                slot.render(g);

        if (mouseItem != null) {
            int[] pos = Game.getMouseLocation();
            g.drawImage(mouseItem.getImg(), pos[0] - 24 - Display.getXOffset(), pos[1] - 24 - Display.getYOffset(),
                    null);
        }
    }

    public InvItem getSelectedItem() {
        return quickAccess[selindex].getChild();
    }

    public void set(InvItem item, String key) {
        String[] secs = key.split("\\s+");
        int loc = Integer.parseInt(secs[1]);
        if (secs[0].equalsIgnoreCase("storage"))
            storage[loc].setChild(item);
        else if (secs[0].equalsIgnoreCase("armory"))
            armory[loc].setChild(item);
        else if (secs[0].equalsIgnoreCase("quickAccess"))
            quickAccess[loc].setChild(item);
    }

    public InvSlot get(String key) {
        String[] secs = key.split("\\s+");
        int loc = Integer.parseInt(secs[1]);
        if (secs[0].equalsIgnoreCase("storage"))
            return storage[loc];
        else if (secs[0].equalsIgnoreCase("armory"))
            return armory[loc];
        else if (secs[0].equalsIgnoreCase("quickAccess"))
            return quickAccess[loc];
        return null;
    }

    public void setMouseItem(InvItem mouseItem) {
        this.mouseItem = mouseItem;
    }

    public int getSelindex() {
        return selindex;
    }

    public InvSlot[] getStorage() {
        return storage;
    }

    public InvSlot[] getArmory() {
        return armory;
    }

    public InvSlot[] getQuickAccess() {
        return quickAccess;
    }

    public InvItem getMouseItem() {
        return mouseItem;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;

        if (open) {
            for (InvSlot slot : storage)
                if (slot != null) {
                    slot.addListeners();
                }
            for (InvSlot slot : armory)
                if (slot != null) {
                    slot.addListeners();
                }
            for (InvSlot slot : quickAccess)
                if (slot != null) {
                    slot.addListeners();
                }
        } else {
            for (InvSlot slot : storage)
                if (slot != null) {
                    slot.removeListeners();
                }
            for (InvSlot slot : armory)
                if (slot != null) {
                    slot.removeListeners();
                }
            for (InvSlot slot : quickAccess)
                if (slot != null) {
                    slot.removeListeners();
                }
        }
    }

    public void setSelindex(int selindex) {
        this.selindex = selindex;
    }
}

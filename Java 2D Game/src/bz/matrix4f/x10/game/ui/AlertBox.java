package bz.matrix4f.x10.game.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;

import bz.matrix4f.x10.game.Game;
import bz.matrix4f.x10.game.core.Display;

/**
 * Created by Matrix4f on 5/15/2016.
 */
public class AlertBox {

    private String message, title;
    private static LinkedList<AlertBox> boxes = new LinkedList<>();

    private KeyListener[] keys;
    private MouseListener[] mouses;
    private MouseMotionListener[] motions;
    private UIButton okBtn;

    public AlertBox(String message, String title) {
        this.message = message;
        this.title = title;
    }

    private void removeListeners() {
        for (KeyListener key : keys)
            Display.toAWTCanvas().removeKeyListener(key);
        for (MouseListener lis : mouses)
            Display.toAWTCanvas().removeMouseListener(lis);
        for (MouseMotionListener lis : motions)
            Display.toAWTCanvas().removeMouseMotionListener(lis);
    }

    private void addListeners() {
        for (KeyListener key : keys)
            Display.toAWTCanvas().addKeyListener(key);
        for (MouseListener lis : mouses)
            Display.toAWTCanvas().addMouseListener(lis);
        for (MouseMotionListener lis : motions)
            Display.toAWTCanvas().addMouseMotionListener(lis);
    }

    public void show() {
        keys = Display.toAWTCanvas().getKeyListeners();
        mouses = Display.toAWTCanvas().getMouseListeners();
        motions = Display.toAWTCanvas().getMouseMotionListeners();

        removeListeners();
        boxes.add(this);

        okBtn = new UIButton(Game.WIDTH / 2 - 30, Game.HEIGHT / 2 - 300 / 2 + 190, 60, 40);
        okBtn.setArcProportion(3);
        okBtn.setRenderShadow(false);
        okBtn.setBg(new Color(159, 240, 166, 50));
        okBtn.setFadeTo(new Color(84, 218, 115, 80));
        okBtn.setFg(new Color(0, 58, 3, 175));

        okBtn.setMaxOffset(4);
        okBtn.setFont(new Font("Corbel", Font.BOLD, 18));
        okBtn.setStrokeWidth(2);
        okBtn.setRenderingStyle(UIButton.REGULAR_RECT);
        okBtn.setText("Close");

        okBtn.loadColor();
        okBtn.loadFontData(okBtn.getFont().getSize() + 3);
        okBtn.addAction(new Runnable() {
            public void run() {
                addListeners();
                okBtn.onDestroy();
                boxes.remove(AlertBox.this);
            }
        });
        okBtn.onLoad();
    }


    public static void tickAll() {
        for (AlertBox box : boxes) {
            box.okBtn.tick();
        }
    }

    public static void renderAll(Graphics2D g) {
        for (AlertBox box : boxes)
            box.render(g);
    }

    private void render(Graphics2D g) {
        int tbarW = 400;
        int tbarH = 25;
        int fullH = 150;

        int alpha = 75;
        g.setStroke(new BasicStroke(2));
        g.setColor(new Color(221, 221, 221, alpha));
        g.fillRect(Game.WIDTH / 2 - tbarW / 2, Game.HEIGHT / 2 - fullH / 2 + tbarH, tbarW, fullH);
        g.setColor(new Color(0, 58, 3));
        g.drawRect(Game.WIDTH / 2 - tbarW / 2, Game.HEIGHT / 2 - fullH / 2 + tbarH, tbarW, fullH);
        g.setColor(new Color(105, 194, 113, alpha));
        g.fillRect(Game.WIDTH / 2 - tbarW / 2, Game.HEIGHT / 2 - fullH / 2, tbarW, tbarH);
        g.setColor(new Color(0, 58, 3));
        g.drawRect(Game.WIDTH / 2 - tbarW / 2, Game.HEIGHT / 2 - fullH / 2, tbarW, tbarH);

        g.setFont(new Font("Monaco", Font.PLAIN, 13));
        FontMetrics fm = g.getFontMetrics();
        int h = fm.getHeight();
        int tw = fm.stringWidth(title);
        int mw = fm.stringWidth(message);

        g.setColor(new Color(0, 58, 3));
        g.drawString(title, Game.WIDTH / 2 - tw / 2, Game.HEIGHT / 2 - fullH / 2 + h);
        g.drawString(message, Game.WIDTH / 2 - mw / 2, Game.HEIGHT / 2 + h);

        okBtn.render(g);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

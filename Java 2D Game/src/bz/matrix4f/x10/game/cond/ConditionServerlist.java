package bz.matrix4f.x10.game.cond;

import bz.matrix4f.x10.game.Game;
import bz.matrix4f.x10.game.core.Loader;
import bz.matrix4f.x10.game.core.Resources;
import bz.matrix4f.x10.game.networking.client.ServerCondAction;
import bz.matrix4f.x10.game.networking.client.ServerList;
import bz.matrix4f.x10.game.ui.AlertBox;
import bz.matrix4f.x10.game.ui.UI;
import bz.matrix4f.x10.game.ui.UIButton;
import bz.matrix4f.x10.game.ui.UIServerButton;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Matrix4f on 5/14/2016.
 */
public class ConditionServerlist extends Condition {

    public static ServerList loader;
    private static UI ui;
    private BufferedImage bg;

    static {
        try {
            loader = new ServerList(new FileInputStream("data/servers.json"), "data/servers.json");
        } catch (Exception e) {
        	System.err.println("Oh noes! An error!");
            e.printStackTrace();
        }
    }

    public ConditionServerlist() {
        super(Type.SERVER);
        ui = new UI();

        UIButton add = new UIButton(25, 100, 45, 45);
        add.setRenderShadow(false);
        add.setBg(new Color(111, 211, 127, 75));
        add.setFadeTo(new Color(47, 177, 59, 200));
        add.setFg(new Color(0, 32, 13));
        add.setArcProportion(1);
        add.setMaxOffset(5);
        add.setFont(new Font("Corbel", Font.PLAIN, 25));
        add.setText("+");
        add.loadColor();
        add.loadFontData(40);
        add.addAction(new Runnable() {
            public void run() {
                set(Type.SERVER_CREATOR);
            }
        });

        UIButton remove = new UIButton(25, 170, 45, 45);
        remove.setRenderShadow(false);
        remove.setBg(new Color(211, 100, 102, 75));
        remove.setFadeTo(new Color(244, 72, 68, 200));
        remove.setFg(new Color(7, 3, 3));
        remove.setArcProportion(1);
        remove.setMaxOffset(5);
        remove.setFont(new Font("Corbel", Font.PLAIN, 25));
        remove.setText("X");
        remove.loadColor();
        remove.loadFontData(40);
        remove.addAction(new Runnable() {
            public void run() {
                ServerCondAction.action = ServerCondAction.Action.DELETE;
            }
        });

        UIButton info = new UIButton(25, 240, 45, 45);
        info.setRenderShadow(false);
        info.setBg(new Color(145, 211, 201, 75));
        info.setFadeTo(new Color(141, 180, 217, 200));
        info.setFg(new Color(0, 18, 27));
        info.setArcProportion(1);
        info.setMaxOffset(5);
        info.setFont(new Font("Corbel", Font.ITALIC, 25));
        info.setText("i");
        info.loadColor();
        info.loadFontData(40);
        info.addAction(new Runnable() {
            public void run() {
                ServerCondAction.action = ServerCondAction.Action.INFO;
            }
        });

        UIButton backBtn = UIButton.genBackToMenuBtn(25, 25, 45, 45, 75);

        ui.add(info);
        ui.add(add);
        ui.add(remove);
        ui.add(backBtn);
    }

    @Override
    public void onLoad() {
        UIServerButton tester = new UIServerButton(0, 1010, "localhost", "This PC");
        ui.add(tester);
        bg = Resources.background2;
        ui.onLoad();
    }

    @Override
    public void onExit() {
        ui.onDestroy();
    }

    @Override
    public void tick() {
        ui.tick();
        AlertBox.tickAll();
    }

    @Override
    public void render(Graphics2D g) {
        g.clearRect(0, 0, Game.WIDTH, Game.HEIGHT);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(bg, 0, 0, Game.WIDTH, Game.HEIGHT, null);

        g.setFont(new Font("Corbel", Font.PLAIN, 60));
        String header = "My Servers";
        int w = g.getFontMetrics().stringWidth(header);
        g.drawString(header, Game.WIDTH / 2 - w / 2, g.getFontMetrics().getHeight());

        ui.render(g);
        AlertBox.renderAll(g);
    }
}

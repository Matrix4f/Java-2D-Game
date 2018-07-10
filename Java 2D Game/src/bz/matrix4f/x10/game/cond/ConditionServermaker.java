package bz.matrix4f.x10.game.cond;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import bz.matrix4f.x10.game.Game;
import bz.matrix4f.x10.game.core.Resources;
import bz.matrix4f.x10.game.networking.client.ServerInfo;
import bz.matrix4f.x10.game.ui.UI;
import bz.matrix4f.x10.game.ui.UIButton;
import bz.matrix4f.x10.game.ui.UITextField;

/**
 * Created by Matrix4f on 5/14/2016.
 */
public class ConditionServermaker extends Condition {

    private UI ui;
    private BufferedImage bg;
    private UITextField portField, ipField, nameField;

    public ConditionServermaker() {
        super(Type.SERVER_CREATOR);

        UIButton ok = new UIButton(Game.WIDTH / 2 - 200 / 2, 500, 200, 60);
        ok.setRenderShadow(false);
        ok.setArcProportion(2);
        ok.setBg(new Color(111, 211, 127));
        ok.setFadeTo(new Color(53, 198, 67));
        ok.setFg(new Color(55, 111, 57));
        ok.setMaxOffset(5);
        ok.setFont(new Font("Corbel", Font.PLAIN, 25));
        ok.setText("Add");
        ok.loadColor();
        ok.loadFontData(40);
        ok.addAction(new Runnable() {
            public void run() {
                ConditionServerlist.loader.add(new ServerInfo(Integer.parseInt(portField.getText()), nameField.getText(), ipField.getText()));
                ConditionServerlist.loader.save();
//                ConditionServerlist.reloadButtons();
                portField.setText("");
                ipField.setText("");
                nameField.setText("");
                set(Type.SERVER);
            }
        });

        int width = 300;
        portField = new UITextField(Game.WIDTH / 2, 250, width, 25, 25);
        portField.setText("1010");
        portField.setBg(new Color(255,255,255,25));
        ipField = new UITextField(Game.WIDTH / 2, 200, width, 25, 25);
        ipField.setBg(new Color(255,255,255,25));
        nameField = new UITextField(Game.WIDTH / 2, 150, width, 25, 25);
        nameField.setBg(new Color(255,255,255,25));

        ui = new UI();
        ui.add(ok);
        ui.add(portField);
        ui.add(ipField);
        ui.add(nameField);
    }

    @Override
    public void onLoad() {
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
    }

    @Override
    public void render(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.clearRect(0, 0, Game.WIDTH, Game.HEIGHT);

        g.drawImage(bg, 0, 0, Game.WIDTH, Game.HEIGHT, null);
        ui.render(g);
        g.setFont(new Font("Corbel", Font.PLAIN, 60));
        g.drawString("Add Server", Game.WIDTH / 2 - g.getFontMetrics().stringWidth("Add Server") / 2, 25 + g.getFontMetrics().getHeight());
        g.setFont(new Font("Segoe UI Light", Font.PLAIN, 14));
        g.drawString("Display name", 100, 150 + g.getFontMetrics().getHeight());
        g.drawString("Server ip", 100, 200 + g.getFontMetrics().getHeight());
        g.drawString("Port", 100, 250 + g.getFontMetrics().getHeight());
    }
}

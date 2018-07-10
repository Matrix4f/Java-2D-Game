package bz.matrix4f.x10.game.cond;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import bz.matrix4f.x10.game.Game;
import bz.matrix4f.x10.game.core.Loader;
import bz.matrix4f.x10.game.core.Resources;
import bz.matrix4f.x10.game.ui.UI;
import bz.matrix4f.x10.game.ui.UIButton;

/**
 * This class is the Condition for when the Game loads - the
 * Main Menu screen.
 */
public class ConditionMenu extends Condition {

    //The User Interface to be used
    private UI ui;

    //The background of the Main Menu
    private BufferedImage background;

    public ConditionMenu() {
        super(Type.MENU);

        String fontFamily = "Corbel";

        ui = new UI();
        UIButton play = new UIButton(Game.WIDTH / 2 - 100, 250, 200, 60);

        play.setBg(new Color(107, 185, 240));
        play.setFadeTo(new Color(52, 152, 219));
        play.setFg(new Color(52, 73, 94));

        play.setMaxOffset(9);
        play.setFont(new Font(fontFamily, Font.PLAIN, 25));
        play.setText("Play");

        play.loadColor();
        play.loadFontData();
        play.addAction(new Runnable() {
            public void run() {
                Condition.set(Type.SERVER);
            }
        });

        UIButton close = new UIButton(Game.WIDTH / 2 - 100, 450, 200, 60);
        close.setBg(new Color(255, 123, 130));
        close.setFadeTo(new Color(235, 107, 68));
        close.setFg(new Color(108, 31, 25));

        close.setMaxOffset(9);
        close.setFont(new Font(fontFamily, Font.PLAIN, 25));
        close.setText("Quit");

        close.loadColor();
        close.loadFontData();
        close.addAction(new Runnable() {
            public void run() {
                System.exit(0);
            }
        });

        UIButton options = new UIButton(Game.WIDTH / 2 - 100, 350, 200, 60);
        options.setBg(new Color(177, 227, 156));
        options.setFadeTo(new Color(103, 198, 131));
        options.setFg(new Color(0, 68, 17));
        options.setStrokeWidth(3);

        options.setMaxOffset(9);
        options.setFont(new Font(fontFamily, Font.PLAIN, 25));
        options.setText("Options");

        options.loadColor();
        options.loadFontData();
        options.addAction(new Runnable() {
            public void run() {
                Condition.set(Type.SETTINGS);
            }
        });

        ui.add(play);
        ui.add(close);
        ui.add(options);

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onLoad() {
    	background = Resources.background1;
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
        g.clearRect(0, 0, Game.WIDTH, Game.HEIGHT);
        g.setStroke(new BasicStroke(2));

        g.drawImage(background, 0, 0, null);
        ui.render(g);

        g.setFont(new Font("Corbel", Font.BOLD, 60));
        g.setColor(new Color(0, 52, 72));
        g.drawString("Warlings of Sparta", Game.WIDTH / 2 - g.getFontMetrics().stringWidth("Warlings of Sparta") / 2,
                80);

        g.setFont(new Font("Corbel", Font.BOLD, 30));
        g.setColor(new Color(0, 97, 129));
        g.drawString(Game.loginUsername, Game.WIDTH / 2 - g.getFontMetrics().stringWidth(Game.loginUsername) / 2,
                140);
        g.setColor(new Color(232, 232, 232, 100));
    }
}

package bz.matrix4f.x10.game.cond;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import bz.matrix4f.x10.game.Game;
import bz.matrix4f.x10.game.core.Settings;
import bz.matrix4f.x10.game.ui.UI;
import bz.matrix4f.x10.game.ui.UIButton;
import bz.matrix4f.x10.game.ui.UIToggleButton;

public class ConditionOptions extends Condition {

    private UI ui;
    private Properties properties;
    private UIToggleButton toggle;

    public ConditionOptions() {
        super(Type.SETTINGS);
        ui = new UI();

        UIButton back = UIButton.genBackToMenuBtn(25, 25, 45, 45, 255);
        back.addAction(new Runnable() {
            public void run() {
                save();
            }
        });
        ui.add(back);

        toggle = new UIToggleButton(200, 200, 100, 30);
        toggle.addListener(new Runnable() {
            public void run() {
                properties.setProperty(Settings.KEY_LIGHTING_ENABLED, String.valueOf(toggle.isEnabled()));
            }
        });
        toggle.setText("Light");
        ui.add(toggle);

        properties = new Properties();
        File file = new File("data/.settings");
        if(file.exists())
            load();
    }

    private void load() {
        properties.clear();
        try {
            properties.load(new FileReader("data/.settings"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Settings.LIGHTING_ENABLED = Boolean.parseBoolean(properties.getProperty(Settings.KEY_LIGHTING_ENABLED));
    }

    private void save() {
        try {
            properties.store(new FileWriter("data/.settings"), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Settings.LIGHTING_ENABLED = Boolean.parseBoolean(properties.getProperty(Settings.KEY_LIGHTING_ENABLED));
    }

    @Override
    public void onLoad() {
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
        int grayness = 230;
        g.setColor(new Color(grayness, grayness, grayness));
        g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);

        ui.render(g);

        g.setFont(new Font("Corbel", Font.PLAIN, 50));
        g.setColor(Color.DARK_GRAY);
        String str = "Settings";
        g.drawString(str,
                Game.WIDTH / 2 - g.getFontMetrics().stringWidth(str) / 2, 75);
    }
}

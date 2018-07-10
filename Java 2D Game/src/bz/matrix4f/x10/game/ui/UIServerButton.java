package bz.matrix4f.x10.game.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import bz.matrix4f.x10.game.Game;
import bz.matrix4f.x10.game.cond.Condition;

public class UIServerButton extends UIObj {

    private static final String defaultToolTipText = "";

    private String ip, display;
    private int port;
    private String toolTipText = defaultToolTipText;

    public UIServerButton(int place, int port, String ip, String display) {
        super(0, 0, 0, 0);
        
        x = 100;
        y = place * 90 + 100; //Leave room for the 'Server list' heading at the top of UI. Every button is 80 pixels
        // high.
        w = Game.WIDTH - x - 25;
        h = 80;

        this.port = port;
        this.ip = ip;
        this.display = display;

        bindListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {
                if(new Rectangle(e.getX(), e.getY(), 1, 1).intersects(x, y, w, h)) {
                    Game.ip = ip;
                    Game.port = port;
                    Condition.set(Condition.Type.GAME);
                }
            }
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        });
    }

    @Override
    public void onLoad() {
    	super.onLoad();
    }
    
    public void render(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(x, y, w, h);
        g.setStroke(new BasicStroke(2));
        g.drawRect(x, y, w, h);

        g.setColor(new Color(0, 126, 29));
        g.setFont(new Font("Corbel", Font.BOLD, 20));
        g.drawString(display, x + 10, y + g.getFontMetrics().getHeight() * 1);

        g.setColor(new Color(69, 101, 0));
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18));
        g.drawString(ip + " : " + port, x + 10, y + g.getFontMetrics().getHeight() * 2);

        g.setFont(new Font("Consolas", Font.PLAIN, 15));
        g.setColor(new Color(0, 126, 87));
        g.drawString(toolTipText, x + w - g.getFontMetrics().stringWidth(toolTipText) - 5, y + h - g.getFontMetrics()
                .getHeight() / 2);
    }
}

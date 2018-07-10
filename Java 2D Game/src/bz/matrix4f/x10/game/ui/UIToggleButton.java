package bz.matrix4f.x10.game.ui;

import bz.matrix4f.x10.game.core.Toolbox;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;

public class UIToggleButton extends UIObj {

    private String text = "Property";
    private String suffix = "OFF";
    private boolean enabled = false;
    private static final Color fadeToWhenDisabled = new Color(226, 95, 102);
    private static final Color fadeToWhenEnabled = new Color(104, 193, 113);
    private static final Color colorEnabled = new Color(58, 127, 25);
    private static final Color colorDisabled = new Color(157, 54, 62);
    private Color fadeTo = fadeToWhenDisabled;
    private Color current = fg;
    private double rs, gs, bs, rsb, gsb, bsb;
    private int ticks = 0;
    private MouseListener mouse;
    private java.util.List<Runnable> listeners = new LinkedList<>();
    private MouseMotionListener motion;
    private boolean updateTicks = false;

    public UIToggleButton(int x, int y, int w, int h) {
        super(x, y, w, h);
        fg = colorDisabled;
        current = fg;
        fadeTo = fadeToWhenDisabled;
        calculateColorChanges();

        mouse = new MouseListener() {
            public void mouseClicked(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
                boolean intersects = new Rectangle(e.getX(), e.getY(), 1, 1).intersects(UIToggleButton.super.x, UIToggleButton.super.y, UIToggleButton.super.w, UIToggleButton.super.h);
                if (intersects) {
                    enabled = !enabled;
                    if (enabled) {
                        fadeTo = fadeToWhenEnabled;
                        fg = colorEnabled;
                        suffix = "ON";
                    } else {
                        fadeTo = fadeToWhenDisabled;
                        fg = colorDisabled;
                        suffix = "OFF";
                    }
                    calculateColorChanges();
                    for (Runnable run : listeners)
                        run.run();
                }
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        };
        motion = new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
            }

            public void mouseMoved(MouseEvent e) {
                boolean intersects = new Rectangle(e.getX(), e.getY(), 1, 1).intersects(UIToggleButton.super.x, UIToggleButton.super.y, UIToggleButton.super.w, UIToggleButton.super.h);
                if (intersects) {
                    updateTicks = true;
                } else updateTicks = false;
            }
        };
        bindListener(motion);
        bindListener(mouse);
    }

    public void tick() {
        if (updateTicks) {
            if (ticks <= 10 && ticks >= 0) {
                int nr = (int) Toolbox.clamp((rs * ticks) + fg.getRed(), 255, 0);
                int ng = (int) Toolbox.clamp((gs * ticks) + fg.getGreen(), 255, 0);
                int nb = (int) Toolbox.clamp((bs * ticks) + fg.getBlue(), 255, 0);
                current = new Color(nr, ng, nb);
                ticks++;
            } else if (ticks >= 11 && ticks <= 20) {
                int nr = ((int) (rsb * (ticks - 10)) + fadeTo.getRed());
                int ng = ((int) (gsb * (ticks - 10)) + fadeTo.getGreen());
                int nb = ((int) (bsb * (ticks - 10)) + fadeTo.getBlue());
                current = new Color(nr, ng, nb);
                ticks++;
            } else if (ticks == 21) {
                ticks = 0;
            }
        }
    }

    private void calculateColorChanges() {
        rs = (-fg.getRed() + fadeTo.getRed()) / 10d;
        gs = (-fg.getGreen() + fadeTo.getGreen()) / 10d;
        bs = (-fg.getBlue() + fadeTo.getBlue()) / 10d;
        rsb = -rs;
        gsb = -gs;
        bsb = -bs;
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(current);
        g.fillRect(x, y, w, h);
        g.setStroke(new BasicStroke(3));
        g.setColor(current.darker());
        g.drawRect(x, y, w, h);

        g.setColor(current.darker().darker().darker());
        g.setFont(new Font("Consolas", Font.PLAIN, 14));
        String str = text + " - " + suffix;
        Rectangle bounds = Toolbox.getStringBounds(g, str, x, y);
        g.drawString(str, x + w / 2 - bounds.width / 2, y + h / 2 + bounds.height / 2);
    }

    public void addListener(Runnable run) {
        listeners.add(run);
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getBs() {
        return bs;
    }

    public double getBsb() {
        return bsb;
    }

    public static Color getColorDisabled() {
        return colorDisabled;
    }

    public static Color getColorEnabled() {
        return colorEnabled;
    }

    public Color getCurrent() {
        return current;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Color getFadeTo() {
        return fadeTo;
    }

    public static Color getFadeToWhenDisabled() {
        return fadeToWhenDisabled;
    }

    public static Color getFadeToWhenEnabled() {
        return fadeToWhenEnabled;
    }

    public double getGs() {
        return gs;
    }

    public double getGsb() {
        return gsb;
    }

    public MouseMotionListener getMotion() {
        return motion;
    }

    public MouseListener getMouse() {
        return mouse;
    }

    public double getRs() {
        return rs;
    }

    public double getRsb() {
        return rsb;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getText() {
        return text;
    }

    public int getTicks() {
        return ticks;
    }

    public boolean isUpdateTicks() {
        return updateTicks;
    }
}

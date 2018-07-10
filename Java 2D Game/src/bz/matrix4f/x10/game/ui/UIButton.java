package bz.matrix4f.x10.game.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.List;

import bz.matrix4f.x10.game.Game;
import bz.matrix4f.x10.game.cond.Condition;
import bz.matrix4f.x10.game.core.Toolbox;
import bz.matrix4f.x10.game.timefunc.ParabolicTimeFunc;

public class UIButton extends UIObj {

    private enum Direction {
        SMALLER, LARGER, NULL, LARGER_TO_NULL, SMALLER_TO_NULL
    }

    public static final int ROUNDED_RECT = 0;
    public static final int REGULAR_RECT = 1;
    public static final int WIDTH = 2;
    public static final int HEIGHT = 3;

    private String text = "";
    private float scale = 1f;

    private Rectangle rect;
    private ParabolicTimeFunc func;
    private Direction dir = Direction.NULL;
    private int maxOffset = 16;
    private float maxOffsetSqrt = (float) Math.sqrt(maxOffset);
    private MouseMotionListener motionListener;
    private MouseListener clickListener;
    private List<Runnable> actions = new LinkedList<>();

    private Font font;
    private int fontSizeOrig, fontSizeEnd, fontSize;
    private double fontSizeSlope;

    private boolean renderShadow = true;

    // Red fade, Green fade, Blue fade, Alpha fade
    private double rf, gf, bf, af;
    private int lrtp, srtp;

    private int renderingStyle = ROUNDED_RECT;
    private int strokeWidth = 4;
    private int arcProportionalTo = HEIGHT;
    private int arcProportion = 3;

    private Color fadeTo = new Color(23, 213, 87), origBg = bg, endBg = fadeTo;

    public UIButton(int x, int y, int w, int h) {
        super(x, y, w, h);

        rect = new Rectangle(x, y, w, h);
        func = new ParabolicTimeFunc(1, 0, 0);
        func.setIncrement(.25f);
        func.setX(0);

        motionListener = new MouseMotionListener() {
            @Override
            public void mouseMoved(MouseEvent e) {
                boolean intersects = new Rectangle(e.getX(), e.getY(), 1, 1)
                        .intersects(rect);
                if (intersects && (dir == Direction.SMALLER_TO_NULL
                        || dir == Direction.NULL)) {
                    dir = Direction.LARGER;
                } else if (!intersects && dir == Direction.LARGER_TO_NULL) {
                    dir = Direction.SMALLER;
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
            }
        };

        clickListener = new MouseListener() {
            @Override
            public void mouseReleased(MouseEvent e) {
                boolean intersects = new Rectangle(e.getX(), e.getY(), 1, 1)
                        .intersects(rect);
                if (intersects && actions != null) {
                    for (Runnable runnable : actions)
                        runnable.run();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
            }
        };
        bindListener(motionListener);
        bindListener(clickListener);

        font = new Font("Monaco", Font.PLAIN, 14);
        loadFontData();
    }

    public void loadColor() {
        origBg = bg;
        endBg = fadeTo;
        int ticksTaken = (int) (func.getA() * Math.ceil(maxOffsetSqrt)
                / func.getIncrement());
        rf = (fadeTo.getRed() - bg.getRed()) / (double) ticksTaken;
        gf = (fadeTo.getGreen() - bg.getGreen()) / (double) ticksTaken;
        bf = (fadeTo.getBlue() - bg.getBlue()) / (double) ticksTaken;
        af = (fadeTo.getAlpha() - bg.getAlpha()) / (double) ticksTaken;
    }

    public void loadFontData() {
        int ticksTaken = (int) (func.getA() * Math.ceil(maxOffsetSqrt)
                / func.getIncrement());
        fontSizeOrig = font.getSize();
        fontSizeEnd = (int) (fontSizeOrig
                * ((w + (double) maxOffset * 10) / w));
        fontSizeSlope = (fontSizeEnd - fontSizeOrig) / (double) ticksTaken;
        fontSize = fontSizeOrig;
    }

    public void loadFontData(int fontSizeEnd) {
        int ticksTaken = (int) (func.getA() * Math.ceil(maxOffsetSqrt)
                / func.getIncrement());
        fontSizeOrig = font.getSize();
        this.fontSizeEnd = fontSizeEnd;
        fontSizeSlope = (fontSizeEnd - fontSizeOrig) / (double) ticksTaken;
        fontSize = fontSizeOrig;
    }

    public void tick() {
        if (dir == Direction.LARGER)
            largerRect();
        else if (dir == Direction.SMALLER)
            smallerRect();
    }

    /**
     * Graph notes:
     * <p>
     * First half of graph (Quad II, x is negative):
     * <p>
     * *RECTANGLE_SIZE DECREASES Large -> Small
     * <p>
     * Scale decreases
     * <p>
     * <p>
     * <p>
     * Second half of graph (Quad I, x is positive):
     * <p>
     * RECTANGLE_SIZE INCREASES Small -> Large
     * <p>
     * Scale increases
     * <p>
     * <p>
     * As dx increases with the scale, the x and y decrease, making the
     * rectangle. SCALE LARGE = RECT LARGE
     */

    private void smallerRect() {
        srtp++;
        scale = func.next();
        if (func.getX() >= 0) {
            // Prepare for large function
            scale = 0;
            func.setX(0);
            dir = Direction.SMALLER_TO_NULL;
            srtp = 0;

            bg = origBg;
            font = font.deriveFont((float) fontSizeOrig);
            return;
        }
        int r = (int) Toolbox.clamp((endBg.getRed() - (rf) * srtp), 255, 0);
        int g = (int) Toolbox.clamp((endBg.getGreen() - (gf) * srtp), 255, 0);
        int b = (int) Toolbox.clamp((endBg.getBlue() - (bf) * srtp), 255, 0);
        int a = (int) Toolbox.clamp((endBg.getAlpha() - (af) * srtp), 255, 0);
        bg = new Color(r, g, b, a);

        fontSize = (int) (fontSizeEnd - fontSizeSlope * srtp);
        font = font.deriveFont((float) fontSize);

        int dx = (int) Math.abs(scale);
        int dy = (int) Math.abs(scale);

        rect.x = x - dx;
        rect.y = y - dy;
        rect.width = w + dx * 2;
        rect.height = h + dy * 2;
    }

    private void largerRect() {
        scale = func.next();
        lrtp++;
        if (scale >= maxOffset) {
            scale = maxOffset;
            // Prepare for the small function
            func.setX(-maxOffsetSqrt);
            dir = Direction.LARGER_TO_NULL;
            lrtp = 0;

            bg = endBg;
            font = font.deriveFont((float) fontSizeEnd);
            return;
        }
        int r = (int) Toolbox.clamp((origBg.getRed() + rf * lrtp), 255, 0);
        int g = (int) Toolbox.clamp((origBg.getGreen() + gf * lrtp), 255, 0);
        int b = (int) Toolbox.clamp((origBg.getBlue() + bf * lrtp), 255, 0);
        int a = (int) Toolbox.clamp((origBg.getAlpha() + af * lrtp), 255, 0);
        bg = new Color(r, g, b, a);

        fontSize = (int) (fontSizeOrig + fontSizeSlope * lrtp);
        font = font.deriveFont((float) fontSize);

        int dx = (int) Math.abs(scale);
        int dy = (int) Math.abs(scale);

        rect.x = x - dx;
        rect.y = y - dy;
        rect.width = w + dx * 2;
        rect.height = h + dy * 2;
    }

    @Override
    public void render(Graphics2D g) {
        renderRect(g);
        renderText(g);
    }

    private void renderText(Graphics2D g) {
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int sw = fm.stringWidth(text);
        int sh = fm.getAscent();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawString(text, rect.x + rect.width / 2 - sw / 2,
                rect.y + rect.height / 2 + sh / 2 - 4);
    }

    private void renderRect(Graphics2D g) {
        g.setStroke(new BasicStroke(strokeWidth));
        if (renderingStyle == REGULAR_RECT) {
            if (renderShadow) {
                g.setColor(new Color(89, 89, 89, 100));
                g.fillRect(x + w / 32, y + w / 32, w + maxOffset, h);
            }

            g.setColor(bg);
            g.fill(rect);
            g.setColor(fg);
            g.draw(rect);
        } else if (renderingStyle == ROUNDED_RECT) {
            int arcw = rect.height / 3, arch = rect.height / 3;

            if (arcProportionalTo == HEIGHT) {
                arcw = rect.height / arcProportion;
                arch = rect.height / arcProportion;
            } else if (arcProportionalTo == WIDTH) {
                arcw = rect.width / arcProportion;
                arch = rect.width / arcProportion;
            }
            if (renderShadow) {
                g.setColor(new Color(89, 89, 89, 100));
                g.fillRoundRect(rect.x + w / 32, rect.y + w / 32,
                        rect.width + (rect.width - w), rect.height, arcw, arch);
            }
            // g.fillRoundRect(x + w / 32, y + w / 32, w, h, arcw, arch);

            g.setColor(bg);
            g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, arcw,
                    arch);
            g.setColor(fg);
            g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, arcw,
                    arch);
        }
    }

    public static UIButton genServerBtn(int x, int y, int h, String text, Runnable runnable) {
        UIButton btn = new UIButton(x, y, Game.WIDTH - x - 30, h);

        btn.setRenderingStyle(UIButton.REGULAR_RECT);
        btn.setRenderShadow(false);

        btn.setBg(new Color(229, 229, 229, 50));
        btn.setFadeTo(new Color(208, 233, 250, 90));
        btn.setFg(new Color(0, 61, 79, 200));

        btn.setMaxOffset(6);
        btn.setFont(new Font("Corbel", Font.PLAIN, 25));
        btn.setText(text);

        btn.loadColor();
        btn.loadFontData(28);
        btn.addAction(runnable);
        return btn;
    }

    public static UIButton genBackToMenuBtn(int x, int y, int w, int h, int a) {
        UIButton back = new UIButton(x, y, w, h);
        back.setRenderingStyle(REGULAR_RECT);
        back.setBg(new Color(193, 114, 232, a));
        back.setFadeTo(new Color(178, 66, 235, a));
        back.setFg(new Color(30, 30, 30));

        back.setMaxOffset(5);
        back.setFont(new Font("Corbel", Font.PLAIN, 23));
        back.setText("<-");

        back.loadColor();
        back.loadFontData(30);
        back.setRenderShadow(false);
        back.addAction(new Runnable() {
            public void run() {
                Condition.set(Condition.Type.MENU);
            }
        });
        return back;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getMaxOffset() {
        return maxOffset;
    }

    public void setMaxOffset(int maxOffset) {
        this.maxOffset = maxOffset;
        this.maxOffsetSqrt = (float) Math.sqrt(maxOffset);
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public int getRenderingStyle() {
        return renderingStyle;
    }

    public void setRenderingStyle(int renderingStyle) {
        this.renderingStyle = renderingStyle;
    }

    public int getArcProportionalTo() {
        return arcProportionalTo;
    }

    public void setArcProportionalTo(int arcProportionalTo) {
        this.arcProportionalTo = arcProportionalTo;
    }

    public int getArcProportion() {
        return arcProportion;
    }

    public void setArcProportion(int arcProportion) {
        this.arcProportion = arcProportion;
    }

    public List<Runnable> getAction() {
        return actions;
    }

    public void addAction(Runnable action) {
        this.actions.add(action);
    }

    public double getRf() {
        return rf;
    }

    public double getGf() {
        return gf;
    }

    public double getBf() {
        return bf;
    }

    public Color getFadeTo() {
        return fadeTo;
    }

    public void setFadeTo(Color fadeTo) {
        this.fadeTo = fadeTo;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;

    }

    public boolean doesRenderShadow() {
        return renderShadow;
    }

    public void setRenderShadow(boolean renderShadow) {
        this.renderShadow = renderShadow;
    }
}

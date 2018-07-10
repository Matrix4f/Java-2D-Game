package bz.matrix4f.x10.game.ui;

import bz.matrix4f.x10.game.core.GameLoop;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by Matrix4f on 5/14/2016.
 */
public class UITextField extends UIObj {

    private String text = "";
    private boolean caretVis = false, inFocus = false;
    private KeyListener listener;
    private MouseListener focusListener;
    private int timer = 0;


    public UITextField(int x, int y, int w, int h, final int maxLen) {
        super(x, y, w, h);

        listener = new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                if (!inFocus)
                    return;
                int[] notAllowed = {KeyEvent.VK_BACK_SPACE, KeyEvent.VK_ESCAPE};
                int k = e.getKeyCode();

                boolean valid = true;
                String addition = Character.toString(e.getKeyChar()).replace(Character.toString(KeyEvent.CHAR_UNDEFINED), "");
                for (int i : notAllowed)
                    if (k == i)
                        valid = false;
                if (valid && text.length() < maxLen)
                    text += addition;
                else {
                    if (k == KeyEvent.VK_BACK_SPACE) {
                        if (!text.equals(""))
                            text = text.substring(0, text.length() - 1);
                    } else if (k == KeyEvent.VK_ESCAPE) {
                        focus(false);
                    }
                }
            }

            public void keyReleased(KeyEvent e) {
            }
        };
        focusListener = new MouseListener() {
            public void mouseClicked(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
                boolean intersects = new Rectangle(UITextField.super.x, UITextField.super.y, UITextField.super.w, UITextField.super.h).intersects(new Rectangle(e.getX(), e
                        .getY(), 1, 1));
                if (!intersects)
                    focus(false);
                else
                    focus(true);
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        };
        bindListener(listener);
        bindListener(focusListener);
    }

    public void focus(boolean on) {
        inFocus = on;
        if (!inFocus)
            caretVis = false;
    }

    @Override
    public void tick() {
        timer++;
        timer %= GameLoop.PER_SECOND;
        if (inFocus && caretVis && timer >= GameLoop.PER_SECOND / 2) {
            caretVis = false;
        } else if (inFocus && !caretVis && timer < GameLoop.PER_SECOND / 2) {
            caretVis = true;
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void render(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if(!inFocus)
            g.setColor(bg);
        else
            g.setColor(bg.darker());
        g.fillRect(x, y, w, h);
        g.setStroke(new BasicStroke(1));
        g.setColor(fg);
        g.drawRect(x, y, w, h);

        g.setFont(new Font("Corbel", Font.PLAIN, 16));
        FontMetrics fm = g.getFontMetrics();
        int h = fm.getHeight();
        g.drawString(text, x + 3, y + this.h / 3 + h / 2);

        if (caretVis) {
            int cposX = fm.stringWidth(text) + 2;
            g.drawLine(x + 3 + cposX, y + 3, x + 3 + cposX, y + h);
        }
    }
}

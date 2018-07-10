package bz.matrix4f.x10.game.core;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class Toolbox {

    public static float getAngle(float x1, float y1, float x2, float y2) {
        float angle = (float) Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    public static BufferedImage rotateImage(float deg, BufferedImage img, int type) {
        AffineTransform xform = new AffineTransform();
        xform.rotate(Math.toRadians(deg), img.getWidth() / 2, img.getHeight() / 2);
        AffineTransformOp op = new AffineTransformOp(xform, type);
        return op.filter(img, null);
    }

    public static Rectangle computeBounds(Point2D.Double p1, Point2D.Double p2) {
        double dx = p2.x - p1.x, dy = p2.y - p1.y;
        return new Rectangle((int) (dx < 0 ? p2.x : p1.x),
                (int) (dy < 0 ? p2.y : p1.y), (int) Math.abs(dx), (int) Math.abs(dy));
    }

    /**
     * @return x so that z <= x <= y
     */
    public static double clamp(double x, double y, double z) {
        if (x <= y && x >= z)
            return x;
        if (x > y)
            return y;
        if (x < z)
            return z;
        return -1;
    }

    public static Color opposite(Color color) {
        return new Color(255 - color.getRed(), 255 - color.getBlue(), 255 - color.getGreen());
    }

    public static Rectangle getStringBounds(Graphics2D g2, String str,
                                      float x, float y) {
        FontRenderContext frc = g2.getFontRenderContext();
        GlyphVector gv = g2.getFont().createGlyphVector(frc, str);
        return gv.getPixelBounds(null, x, y);
    }
}

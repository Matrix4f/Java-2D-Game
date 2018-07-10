package bz.matrix4f.x10.game.lighting;

import java.awt.Rectangle;

/**
 * Created by vgsoh_000 on 4/28/2016.
 */
public class LightSource {

    private int cx, cy, origCX, origCY, radius, maxAlpha;

    public LightSource(int cx, int cy, int radius, int maxAlpha) {
        this.cx = origCX = cx;
        this.cy = origCY = cy;
        this.radius = radius;
        this.maxAlpha = maxAlpha;
    }

    public int getColor(int x, int y) {
        double scale = radius / 255d; //The scale of the radius to the range of alpha-values: 0-255
        int origDX = cx - x;          //The x-distance from x to the middle
        int origDY = cy - y;          //The y-distance from y to the middle

        if(Math.abs(Math.sqrt(origDX * origDX + origDY * origDY)) > radius) //It is too far away
            return -1;

        int dx = (int) (origDX / scale);   //Scale the distances
        int dy = (int) (origDY / scale);   //Scale the distances
        double distance = Math.abs(Math.sqrt(dx * dx + dy * dy)); //Calculate the actual distance using the
        // Pythagorean Theorem
        int alpha = (int) (distance * ((double) maxAlpha / 255)); //Use the distance to find the alpha

        if(alpha < 0)
            alpha = 0;
        else if(alpha > 255)
            alpha = 255;

        int r = 0;
        int g = 0;
        int b = 0;

        return ((alpha & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                ((b & 0xFF) //<< 0
                );
    }

    /**
     * Moves the light source in relative space.
     * Example:
     * @<code>cx = 1, cy = 4</code>
     * @<code>relativeTranslation(4,4)</code> will move the light to 5, 8
     * @param cx the relative x-position
     * @param cy the relative y-position
     */
    public void relativeTranslation(int cx, int cy) {
        this.cx = origCX + cx;
        this.cy = origCY + cy;
    }

    public void setMaxAlpha(int maxAlpha) {
        this.maxAlpha = maxAlpha;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getCx() {
        return cx;
    }

    public int getCy() {
        return cy;
    }

    public int getRadius() {
        return radius;
    }

    public Rectangle getBounds() {
        return new Rectangle(origCX - radius, origCY - radius, radius * 2, radius * 2);
    }
}

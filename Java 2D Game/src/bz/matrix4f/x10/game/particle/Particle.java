package bz.matrix4f.x10.game.particle;

public class Particle {

    private double x, y;
    private double vx, vy;
    private int lifeLeft;
    private int lifeTime;

    public Particle(double x, double y, double vx, double vy, int lifetime) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        lifeLeft = lifetime;
        lifeTime = lifetime;
    }

    public boolean tick() {
        x += vx;
        y += vy;
        if(lifeLeft-- <= 0)
            return true;
        return false;
    }

    public int getLifeTime() {
        return lifeTime;
    }

    public int getLifeLeft() {
        return lifeLeft;
    }

    public double getVx() {
        return vx;
    }

    public void setVx(float vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(float vy) {
        this.vy = vy;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}

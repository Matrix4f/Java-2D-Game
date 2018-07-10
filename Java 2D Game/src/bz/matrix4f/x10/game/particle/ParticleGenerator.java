package bz.matrix4f.x10.game.particle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import bz.matrix4f.x10.game.networking.IntegerByte;

public class ParticleGenerator {

    private java.util.List<Particle> particles;

    public ParticleGenerator(int amount, int x, int y, double multiply, int lifetime, IntegerByte encoding) {
        particles = new LinkedList<>();
        for(int i = 0; i < amount; i++) {
            double vx = genRandom(-multiply, multiply);
            double vy = genRandom(-multiply, multiply);
            particles.add(new Particle(x, y, vx, vy, lifetime));
        }
    }

    private double genRandom(double rangeMin, double rangeMax) {
        Random r = new Random();
        return rangeMin + (rangeMax - rangeMin) * r.nextDouble();
    }

    public void tick() {
        Iterator<Particle> iter = particles.iterator();
        while(iter.hasNext()) {
            Particle particle = iter.next();
            if(particle.tick())
                iter.remove();
        }
    }

    public void render(Graphics2D g) {
        for(Particle particle : particles) {
            g.setColor(new Color(1f, 0f, 0f).darker());
            g.fillRect((int) particle.getX(), (int) particle.getY(), 4, 4);
            g.setColor(new Color(1f, 0f, 0f).darker().darker());
            g.drawRect((int) particle.getX(), (int) particle.getY(), 4, 4);
        }
    }
}

package bz.matrix4f.x10.game.particle;

import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;

public class ParticleGenerators {

    private List<ParticleGenerator> particleGenerators;

    public ParticleGenerators() {
        particleGenerators = new LinkedList<>();
    }

    public void tick() {
        for(int i = 0; i < particleGenerators.size(); i++) {
            particleGenerators.get(i).tick();
        }
    }

    public void render(Graphics2D g) {
        for(int i = 0; i < particleGenerators.size(); i++) {
            particleGenerators.get(i).render(g);
        }
    }

    public int size() {
        return particleGenerators.size();
    }

    public boolean add(ParticleGenerator particleGenerator) {
        return particleGenerators.add(particleGenerator);
    }
}

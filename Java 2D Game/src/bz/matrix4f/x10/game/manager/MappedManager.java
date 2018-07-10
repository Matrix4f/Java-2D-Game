package bz.matrix4f.x10.game.manager;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import bz.matrix4f.x10.game.Game;
import bz.matrix4f.x10.game.core.Renderer;
import bz.matrix4f.x10.game.core.Ticker;
import bz.matrix4f.x10.game.entity.SPEntity;

public class MappedManager<T, B> implements Ticker, Renderer {

    protected Map<T, B> data = new HashMap<T, B>();

    @Override
    public void render(Graphics2D g) {
        try {
            Collection<B> values = data.values();
            Object[] array = values.toArray();
            for (int i = 0; i < data.size(); i++) {
                if (i < array.length && array[i] instanceof SPEntity)
                    if (((SPEntity) array[i]).bounds().intersects(Game.game.getCamera().bounds()))
                        ((Renderer) array[i]).render(g);
            }
        } catch (ConcurrentModificationException e) {
        }
    }

    @Override
    public void tick() {
        try {
            Collection<B> values = data.values();
            Object[] array = values.toArray();
            for (int i = 0; i < data.size(); i++) {
                if (i < array.length && array[i] instanceof Ticker)
                    ((Ticker) array[i]).tick();
            }
        } catch (ConcurrentModificationException e) {
        }
    }

    public void add(T key, B value) {
        data.put(key, value);
    }

    @SuppressWarnings("unchecked")
	public void add(SPEntity SPEntity) {
		B obj = (B) SPEntity;
        T val = (T) SPEntity.getUUID();
        add(val, obj);
    }

    public B get(T key) {
        return data.get(key);
    }

    public void removeIndirect(T t) {
        data.remove(t);
    }

    public void removeDirect(B b) {
        data.values().remove(b);
    }

    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public Map<T, B> getData() {
        return data;
    }

}

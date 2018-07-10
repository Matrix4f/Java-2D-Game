package bz.matrix4f.x10.game.lighting;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bz.matrix4f.x10.game.Game;
import bz.matrix4f.x10.game.gfx.Camera;

/**
 * Created by vgsoh_000 on 4/26/2016.
 */
public class LightMap {

    public static int ALPHA_VALUE = 100;

    private BufferedImage img;
    private int[] pxls;

    public List<LightSource> lights;
    private Camera cam;

    private final int ticksBeforeBuffer = 1;
    private volatile int ticksLeft = 0;
    private Thread bufferThread;
    private boolean finishedRendering = false;

    public LightMap() {
        img = new BufferedImage(Game.WIDTH, Game.HEIGHT, BufferedImage.TYPE_INT_ARGB);
        pxls = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
        lights = new ArrayList<>();
        cam = Game.game.getCamera();

        bufferThread = new Thread("LightmapBuffer") {
            @Override
            public void run() {
                bufferThreadRun();
            }
        };
        bufferThread.start();
    }

    private void bufferThreadRun() {
        while (true) {
            if (ticksLeft == 0) {
                bufferPixels();
                ticksLeft = ticksBeforeBuffer;
            }
        }
    }

    public void tick() {
        if (ticksLeft > 0)
            ticksLeft--;
    }

    public void bufferPixels() {
        //Moves the lights with the camera, so that they appear to be part of the world
        Iterator<LightSource> iter = lights.iterator();
        while(iter.hasNext()) {
            LightSource source = iter.next();
            source.relativeTranslation(cam.getX(), cam.getY());
        }

        //Reloads the pixel-values in the light map
        for (int i = 0; i < pxls.length; i++) {
            int r = 0;
            int g = 0;
            int b = 0;

            //Current coordinates of the pixel[]
            int x = i % img.getWidth();
            int y = i / img.getWidth();

            //Whether the pixel is too far away from any light source
            //Because if it is close to one, there is no need
            //To set that pixel to the ALPHA_VALUE
            boolean shouldSkip = false;
            for (int j = 0; j < lights.size(); j++) {
                LightSource light = lights.get(j);
                int dx = (light.getCx() - x);
                int dy = (light.getCy() - y);
                int distance = (int) Math.sqrt(dx * dx + dy * dy);
                if (distance < light.getRadius() - 2)
                    shouldSkip = true;
            }

            if (shouldSkip)
                continue;
            int value = ((ALPHA_VALUE & 0xFF) << 24) |
                    ((r & 0xFF) << 16) |
                    ((g & 0xFF) << 8) |
                    ((b & 0xFF) //<< 0
                    );
            pxls[i] = value;
        }

        for (int i = 0; i < pxls.length; i++) {
            int x = i % img.getWidth();
            int y = i / img.getWidth();
            for (int j = 0; j < lights.size(); j++) {
                LightSource light = lights.get(j);
                if (light.getBounds().intersects(Camera.BOUNDS)) {
                    int value = light.getColor(x, y);
                    if (value != -1)
                        pxls[i] = value;
                }
            }
        }

    }

    public void render(Graphics2D g) {
//        this.g = g;
//        new Thread("LightmapRendering") {
//            @Override
//            public void run() {
//                synchronized (game) {
                    g.drawImage(img, 0, 0, null);
//                    game.notify();
//                }
//            }
//        }.start();
    }

    public boolean isFinishedRendering() {
        return finishedRendering;
    }
}

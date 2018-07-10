package bz.matrix4f.x10.game;

import java.awt.*;

import bz.matrix4f.x10.game.chat.Chat;
import bz.matrix4f.x10.game.cond.*;
import bz.matrix4f.x10.game.cond.Condition.Type;
import bz.matrix4f.x10.game.core.*;
import bz.matrix4f.x10.game.entity.SPEntity;
import bz.matrix4f.x10.game.entity.SPPlayer;
import bz.matrix4f.x10.game.gfx.Camera;
import bz.matrix4f.x10.game.gfx.DebugGfx;
import bz.matrix4f.x10.game.gfx.FogGfx;
import bz.matrix4f.x10.game.inventory.Inv;
import bz.matrix4f.x10.game.inventory.InvItemLoader;
import bz.matrix4f.x10.game.lighting.LightMap;
import bz.matrix4f.x10.game.manager.MappedManager;
import bz.matrix4f.x10.game.map.Map;
import bz.matrix4f.x10.game.map.TileInfo;
import bz.matrix4f.x10.game.networking.client.Client;
import bz.matrix4f.x10.game.networking.client.ClientGui;
import bz.matrix4f.x10.game.networking.packet.Packet0dDisconnect;
import bz.matrix4f.x10.game.particle.ParticleGenerators;

/**
 * The class that represents the main game instance, its clientside data, and
 * all things related to the GameState.
 *
 * @author vgsoh_000
 */
public class Game implements Renderer, Ticker {
    public static final int WIDTH = 800; // Window width
    public static final int HEIGHT = 600; // Window height
    /**
     * The instance used by other classes to access the Game.
     */
    public static Game game;
    public static Type DEFAULT = Type.LOADING_RESOURCES;
    /**
     * In the Log-in system, http://matrix4f.x10.bz/mulgame/function.php returns
     * a SessionID, which the GameServer uses to authenticate the client with,
     * so no Username or Password are being exchanged.
     */
    public static String sessionID = "";
    public static String loginUsername = "";

    /**
     * When the user clicks on a server button to join a server
     * these will be set.
     */
    public static String ip = "localhost";
    public static int port = 1010;

    /**
     * The HashMap binding all entity UUIDs to the actual entity.
     */
    private MappedManager<String, SPEntity> entities;
    private ParticleGenerators particles;
    private SPPlayer player;
    private Client client;
    private Chat chat;
    private Map map;
    private DebugGfx debugGfx;
    private InputHandler input;
    private Camera cam;
    private FogGfx fog;
    private Inv inv;

    private LightMap lightMap;

    private long ticks;
    private boolean isFirstTick = true;

    /**
     * Called as soon as the program starts. Assigns the field game to this
     * because other classes will need to use this very instance, not a new
     * Game().
     */
    public Game() {
        game = this;

        Condition.registerAll(new ConditionGame(this), new ConditionMenu(),
                new ConditionOptions(), new ConditionServerlist(), new ConditionServermaker(),
                new ConditionResources());
        Condition.set(DEFAULT);
    }

    /**
     * A function that is called on the first tick of the game, which
     * initializes all of its data, memory, and its client.
     */
    private void firstTick() {
        /*
		* File initialization
		 */
        TileInfo.load();
        InvItemLoader.load();

		/*
		* Client-side initialization
		 */
        entities = new MappedManager<>();
        player = new SPPlayer(100, 100);
        entities.add(player.getUUID(), player);
        input = new InputHandler();
        client = new Client(new ClientGui(), ip, port);
        particles = new ParticleGenerators();
        new Thread(client, "Client_thread").start();
        inv = new Inv(false);
        chat = new Chat();

		/*
		* Grpahical initialization
		 */
        debugGfx = new DebugGfx();
        fog = new FogGfx();
        cam = new Camera(player);
        lightMap = new LightMap();
    }

    /**
     * When the server sends a Packet0gMapLoader, the map should be passed here
     * to load into the game.
     *
     * @param map The map to be loaded
     */
    public void loadNewMap(Map map) {
        this.map = null;
        this.map = map;
    }

    /**
     * The update function of the game. This function is called by the GameLoop
     * class. It is called GameLoop.PER_SECOND times every second.
     */
    @Override
    public void tick() {
        if (isFirstTick) {
            firstTick();
            isFirstTick = false;
        }
        entities.tick();
        chat.tick();
        particles.tick();
        cam.move();
        if (Settings.LIGHTING_ENABLED) {
            lightMap.tick();
        }
        ticks++;
    }

    /**
     * This method handles all the drawing or rendering onto the Display.
     *
     * @param g The Graphics object used to draw everything in the game.
     */
    @Override
    public void render(Graphics2D g) {
        if (isFirstTick)
            return;
        g.clearRect(0, 0, WIDTH + 10, HEIGHT + 10);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // OFFSETED-RENDERING BEGIN
        g.translate(cam.getX(), cam.getY());

        if (map != null) map.render(g);
        entities.render(g);
        particles.render(g);

        g.translate(-cam.getX(), -cam.getY());
        // OFFSETED-RENDERING END
        if (Settings.LIGHTING_ENABLED) {
            lightMap.render(g);
        }
        chat.render(g);
        debugGfx.render(g);
        inv.render(g);
    }

    /**
     * The program's main method
     *
     * @param args The arguments, such as username and password, passed to the
     *             program
     */
    public static void main(String[] args) {
        ExternalStarter.launch(args);
    }

    /**
     * This method attempts to send a disconnection packet to the server, and
     * then quit the game.
     */
    public static void close() {
        try {
            Packet0dDisconnect packet = new Packet0dDisconnect();
            packet.sendToServer();
        } catch (Exception ex) {
            System.exit(-1);
        }
        System.exit(0);
    }

    /*
     * Getters
     */
    public MappedManager<String, SPEntity> getEntities() {
        return entities;
    }

    public SPPlayer getSPPlayer() {
        return player;
    }

    public static int[] getMouseLocation() {
        PointerInfo info = MouseInfo.getPointerInfo();
        Point point = info.getLocation();
        return new int[]{point.x, point.y};
    }

    public LightMap getLightMap() {
        return lightMap;
    }

    public static int getWidth() {
        return WIDTH;
    }

    public static int getHeight() {
        return HEIGHT;
    }

    public static Game getGame() {
        return game;
    }

    public Client getClient() {
        return client;
    }

    public InputHandler getInput() {
        return input;
    }

    public boolean isFirstTick() {
        return isFirstTick;
    }

    public Inv getInv() {
        return inv;
    }

    public Chat getChat() {
        return chat;
    }

    public Map getMap() {
        return map;
    }

    public DebugGfx getDebugGfx() {
        return debugGfx;
    }

    public long getTicks() {
        return ticks;
    }

    public static String getSessionID() {
        return sessionID;
    }

    public Camera getCamera() {
        return cam;
    }

    public Camera getCam() {
        return cam;
    }

    public static Type getDEFAULT() {
        return DEFAULT;
    }

    public FogGfx getFog() {
        return fog;
    }

    public ParticleGenerators getGenerators() {
        return particles;
    }

    public static int getHEIGHT() {
        return HEIGHT;
    }

    public static String getIp() {
        return ip;
    }

    public static String getLoginUsername() {
        return loginUsername;
    }

    public SPPlayer getPlayer() {
        return player;
    }

    public static int getPort() {
        return port;
    }

    public static int getWIDTH() {
        return WIDTH;
    }
}

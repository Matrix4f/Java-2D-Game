package bz.matrix4f.x10.game.networking.server;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.Map.Entry;

import bz.matrix4f.x10.game.core.Log;
import bz.matrix4f.x10.game.entity.UUID;
import bz.matrix4f.x10.game.inventory.InvItem;
import bz.matrix4f.x10.game.inventory.InvItemLoader;
import bz.matrix4f.x10.game.manager.MappedManager;
import bz.matrix4f.x10.game.map.MapLoader;
import bz.matrix4f.x10.game.networking.packet.*;
import bz.matrix4f.x10.game.networking.server.display.ColorHandler;
import bz.matrix4f.x10.game.networking.server.display.ServerUI;
import bz.matrix4f.x10.game.networking.server.entities.MPChicken;
import bz.matrix4f.x10.game.networking.server.entities.MPEntity;
import bz.matrix4f.x10.game.networking.server.entities.MPPlayer;
import bz.matrix4f.x10.game.networking.server.entities.MPTorch;
import bz.matrix4f.x10.game.timefunc.DaylightCycle;
import org.json.simple.JSONObject;

public class Server implements Runnable {

    private ServerSocket srvSock;
    private ServerUI gui;
    private Map<Integer, ClientConn> connections = new HashMap<>();
    private List<ClientConn> orderedPlayers = new ArrayList<>();
    private BannedPlayers bannedPlayers;

    private int id = 0;
    private volatile boolean stopped = false;
    private Thread currentThread;
    private Thread updateThread;

    //LOGIC-DECLARATIONS BEGIN
    private MappedManager<String, MPEntity> entities;
    private String mapdata;
    private DaylightCycle cycle;

    //LOGIC-DECLARATIONS END

    public Server() {
        InvItemLoader.load("server/serveritem.json");

        entities = new MappedManager<>();
        bannedPlayers = new BannedPlayers();
        cycle = new DaylightCycle(this);
        mapdata = MapLoader.readMapFormat(new File("server/map.txt"));

        ColorHandler.loadColors();

        MPTorch torch = new MPTorch(100, 100, UUID.generate(), this);
        entities.add(torch.getUUID(), torch);

        MPChicken chicken = new MPChicken(200, 200, UUID.generate(), this);
        entities.add(chicken.getUUID(), chicken);

        updateThread = new Thread(new ServerTicker(this));
        updateThread.start();
    }

	/*
     * The following methods deal with incoming packets.
	 */

    public void packet0aLogin(final ClientConn conn, String uuid, final String username,
                              String addr, float x, float y) {
        boolean isBanned = bannedPlayers.isPlayerBanned(username);

        if (isBanned) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    kickPlayer(conn, username, bannedPlayers.getReason(username));
                }
            }, "ClientBanKicker").start();
        } else {
            MPPlayer player = new MPPlayer(0, 0, uuid, username, addr, this);
            player.setX(x);
            player.setY(y);
            entities.add(uuid, player);
            conn.setPlayer(player);
        }
    }

    @SuppressWarnings("unchecked")
	public JSONObject generateItemJSON(InvItem item, String key, String type) {
        JSONObject obj = new JSONObject();
        boolean isNull = item == null;
        obj.put("type", type);
        obj.put("key", key);
        int id = (isNull) ? -1 : item.getId();
        obj.put("id", id);
        int count = (isNull) ? 1 : item.getCount();
        obj.put("count", count);
        return obj;
    }

    public void packet0bMove(float x, float y, float vx, float vy, String uuid) {
        MPEntity ent = entities.get(uuid);
        ent.setX(x);
        ent.setY(y);
        ent.setVx(vx);
        ent.setVy(vy);
    }
	
	/*
	 * The following methods deal with kicking/removing players
	 * from the server.
	 */

    public void kickPlayer(String username, String reason) {
        ClientConn conn = getConnectionByPlayer(getPlayerByUsername(username));
        if (conn != null) {
            kickPlayer(conn, username, reason);
            sendChatMsg(":r:*b0[Server]:bl:*00  Kicked player " + username + ". Reason: '" + reason + "'");
        }
    }

    public void removePlayer(String username, ClientConn conn) {
        connections.remove(getIdByConnection(conn));
        orderedPlayers.remove(conn);
        gui.removePlayer(username);

        try {
            conn.getPrint().close();
            conn.getBr().close();
            conn.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void badSessionIDKick(ClientConn conn) {
        kickPlayer(getPlayerByConnection(conn).getUsername(), "Invalid session_id. :-(");
        connections.remove(getIdByConnection(conn));
        orderedPlayers.remove(conn);

        try {
            conn.getPrint().close();
            conn.getBr().close();
            conn.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void kickPlayer(ClientConn conn, String username, String reason) {
        Packet0dDisconnect disconnect = new Packet0dDisconnect();
        disconnect.setData(conn.getUuid());
        sendPacketToAllClientsExcept(disconnect, conn);

        Packet0eForcedDisconnection packet = new Packet0eForcedDisconnection(reason);
        packet.writeData(conn.getPrint());

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        removePlayer(username, conn);
        entities.removeIndirect(conn.getUuid());
    }

	
	/*
	 * The following methods are ways of sending data to multiple
	 * clients at once.
	 */

    public void sendChatMsg(String msg) {
        sendPacketToAllClients(new Packet0fChatMsg(msg));
        gui.log(":o:[CHAT]:bl:" + msg);
    }

    public void addParticles(int count, int x, int y, double multiply, int lifetime) {
        sendPacketToAllClients(new Packet0mParticle(count, x, y, multiply, lifetime));
    }

    public void sendPacketToAllClients(Packet packet) {
        for (ClientConn conn : clients()) {
            packet.writeData(conn.getPrint());
        }
    }

    public void sendPacketToAllClientsExcept(Packet packet, ClientConn except) {
        for (ClientConn conn : clients()) {
            if (conn != except)
                packet.writeData(conn.getPrint());
        }
    }
	
	/*
	 * The following methods have the server's core procedures
	 * within them, such as adding clients and opening a socket. 
	 */

    @Override
    public void run() {
        currentThread = Thread.currentThread();
        openServer(1010);
        Log.print("Started server on " + srvSock.getInetAddress().getCanonicalHostName() + ":" + 1010 + ".");
        while (!stopped) {
            try {
                //Adding the client
                Socket socket = srvSock.accept();
                Log.print("New connection.");
                ClientConn conn = new ClientConn(this, socket, gui);
                new Thread(conn).start();

                connections.put(id, conn);
                orderedPlayers.add(conn);
                id++;
            } catch (SocketException e) {
                if (!e.getMessage().equals("socket closed"))
                    e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        sendPacketToAllClients(new Packet0eForcedDisconnection("Server closed."));
        stopped = true;
        try {
            srvSock.close();
            currentThread.join();
        } catch (Exception e) {
            Log.err("Unable to stop server! : " + e.getClass().getSimpleName());
        }
    }

    private void openServer(int port) {
        try {
            srvSock = new ServerSocket(port);
        } catch (BindException e) {
            Log.err("Address already bound!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	/*
	 * The following methods are used to convert from one data
	 * type to another, such as ClientConn to player, UUID to player,
	 * and player to ClientConn.
	 */

    public MPPlayer getPlayerByConnection(ClientConn conn) {
        for(int i = 0; i < entities.size(); i++) {
            MPEntity ent = new ArrayList<MPEntity>(entities.getData().values()).get(i);
            if (ent instanceof MPPlayer) {
                MPPlayer p = (MPPlayer) ent;
                if (p.getUUID().equals(conn.getUuid()))
                    return p;
            }
        }
        return null;
    }

    public ClientConn getConnectionByPlayer(MPPlayer player) {
        for (ClientConn conn : orderedPlayers)
            if (conn.getUuid().equals(player.getUUID()))
                return conn;
        return null;
    }

    public MPPlayer getPlayerByUsername(String username) {
        for (MPEntity ent : entities.getData().values()) {
            if (ent instanceof MPPlayer) {
                MPPlayer player = (MPPlayer) ent;
                if (player.getUsername().equals(username)) {
                    for (ClientConn conn : orderedPlayers) {
                        if (conn.getUuid().equals(player.getUUID())) {
                            return player;
                        }
                    }
                }
            }
        }
        return null;
    }

    public MPPlayer getPlayerByUUID(String uuid) {
        for (MPEntity ent : entities.getData().values())
            if (ent instanceof MPPlayer && uuid.equals(ent.getUUID()))
                return (MPPlayer) ent;
        return null;

    }

    public int getIdByConnection(ClientConn conn) {
        Set<Entry<Integer, ClientConn>> set = connections.entrySet();
        Object[] data = set.toArray();
        for (int i = 0; i < set.size(); i++) {
            @SuppressWarnings("unchecked")
            Entry<Integer, ClientConn> entry = (Entry<Integer, ClientConn>) data[i];
            ClientConn value = entry.getValue();
            if (value == conn)
                return entry.getKey();
        }
        return -1;
    }
	
	
	/*
	 * Getters
	 */

    public void setGui(ServerUI gui) {
        this.gui = gui;
        gui.setServer(this);
    }

    public DaylightCycle getCycle() {
        return cycle;
    }

    public Thread thread() {
        return new Thread(this);
    }

    public ServerSocket getSrvSock() {
        return srvSock;
    }

    public ServerUI getGui() {
        return gui;
    }

    public Map<Integer, ClientConn> getConnections() {
        return connections;
    }

    public int getId() {
        return id;
    }

    public boolean isStopped() {
        return stopped;
    }

    public MappedManager<String, MPEntity> getEntities() {
        return entities;
    }

    public List<ClientConn> getOrderedPlayers() {
        return orderedPlayers;
    }

    public BannedPlayers getBannedPlayers() {
        return bannedPlayers;
    }

    public List<ClientConn> clients() {
        return new ArrayList<>(connections.values());
    }

    public String getMapdata() {
        return mapdata;
    }

    public void setMapdata(String mapdata) {
        this.mapdata = mapdata;
    }
}

package bz.matrix4f.x10.game.networking.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import bz.matrix4f.x10.game.core.GameAuth;
import bz.matrix4f.x10.game.core.Log;
import bz.matrix4f.x10.game.entity.UUID;
import bz.matrix4f.x10.game.inventory.Inv;
import bz.matrix4f.x10.game.inventory.InvItem;
import bz.matrix4f.x10.game.networking.packet.Packet;
import bz.matrix4f.x10.game.networking.packet.Packet0aLogin;
import bz.matrix4f.x10.game.networking.packet.Packet0bMove;
import bz.matrix4f.x10.game.networking.packet.Packet0cEntityRequest;
import bz.matrix4f.x10.game.networking.packet.Packet0dDisconnect;
import bz.matrix4f.x10.game.networking.packet.Packet0gMapLoader;
import bz.matrix4f.x10.game.networking.packet.Packet0iInvUpdate;
import bz.matrix4f.x10.game.networking.packet.Packet0jAddEntity;
import bz.matrix4f.x10.game.networking.server.display.ServerUI;
import bz.matrix4f.x10.game.networking.server.entities.EntityName;
import bz.matrix4f.x10.game.networking.server.entities.MPArrow;
import bz.matrix4f.x10.game.networking.server.entities.MPChicken;
import bz.matrix4f.x10.game.networking.server.entities.MPEgg;
import bz.matrix4f.x10.game.networking.server.entities.MPEntity;
import bz.matrix4f.x10.game.networking.server.entities.MPPlayer;
import bz.matrix4f.x10.game.networking.server.entities.MPTorch;

public class ClientConn implements Runnable {

    private Socket socket;
    private Server server;
    private BufferedReader br;
    private PrintStream print;
    private ServerUI gui;
    private Thread currentThread;
    private MPPlayer player;

    private String uuid;

    public ClientConn(Server server, Socket socket, ServerUI gui) {
        this.socket = socket;
        this.gui = gui;
        this.server = server;

        try {
            br = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            print = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        currentThread = Thread.currentThread();
        startReadingTimer();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PlayerInteractor.setItem("quickAccess 0", new InvItem(0), this, server);
        PlayerInteractor.setItem("quickAccess 3", new InvItem(1), this, server);
        sendItemSelectionPacket(player.getInv());
    }

    private void processInput(String input) {
        String id = input.substring(0, 2);
        String data = input.substring(2);
        String[] sec = data.split(Packet.SEPERATOR);
        switch (id) {
            case "0a":
                packet0aLogin(sec);
                break;
            case "0b":
                packet0bMove(sec);
                break;
            case "0c":
                packet0cEntityRequest();
                break;
            case "0d":
                packet0dDisconnect();
                break;
            case "0f":
                packet0fChatMsg(sec);
                break;
            case "0g":
                packet0gMapLoader();
                break;
            case "0h":
                packet0hItemUse(sec);
                break;
            case "0i":
                packet0iInvUpdate(sec);
                break;
            default: {
                gui.log("Data received: '" + id + data + "' from PacketType:'" + id + "'");
            }
        }
    }

    private void packet0iInvUpdate(String[] sec) {
        JSONObject json = (JSONObject) JSONValue.parse(sec[0]);
        Inv inv = player.getInv();
        String type = (String) json.get("type");
        switch (type) {
            case "swap":
                String k1 = (String) json.get("s1");
                String k2 = (String) json.get("s2");
                InvItem i1 = inv.get(k1).getChild();
                InvItem i2 = inv.get(k2).getChild();
                inv.set(i1, k2);
                inv.set(i2, k1);
                break;
            case "select":
                int selindex = Integer.parseInt(json.get("index").toString());
                inv.setSelindex(selindex);
                sendItemSelectionPacket(inv);
                break;
        }
    }

    @SuppressWarnings("unchecked")
	private void sendItemSelectionPacket(Inv inv) {
        InvItem selected = inv.getSelectedItem();
        JSONObject obj = server.generateItemJSON(selected, "", "select");
        obj.put("uuid", uuid);
        server.sendPacketToAllClientsExcept(new Packet0iInvUpdate(obj), this);
    }

    private void packet0hItemUse(String[] sec) {
        Inv inv = player.getInv();
        int selindex = inv.getSelindex();
        InvItem item = inv.getQuickAccess()[selindex].getChild();
        int mouseX = Integer.parseInt(sec[0]);
        int mouseY = Integer.parseInt(sec[1]);

        if (item == null)
            return;
        switch (item.getName()) {
            case "sword": {
                MPPlayer player = server.getPlayerByConnection(this);
                player.registerAttack(mouseX, mouseY, 4);
                break;
            }
            case "bow": {
                if (player.isCanShootArrow()) {
                    MPArrow arrow = new MPArrow(player.getX(), player.getY(), mouseX, mouseY, UUID.generate(),
                            server);
                    JSONObject json = getJSON(arrow);
                    Packet0jAddEntity packet = new Packet0jAddEntity(json.toJSONString());
                    server.sendPacketToAllClients(packet);
                    server.getEntities().add(arrow.getUUID(), arrow);
                    player.setCanShootArrow(false);
                }
                break;
            }
        }
    }

    private void packet0gMapLoader() {
        Packet0gMapLoader packet = new Packet0gMapLoader(server.getMapdata());
        packet.writeData(print);
    }

    private void packet0fChatMsg(String[] sec) {
        String msg = "";
        for (String str : sec)
            msg += str + Packet.SEPERATOR;
        msg = msg.substring(0, msg.length() - Packet.SEPERATOR.length());
        msg = ":g:" + player.getUsername() + ": " + msg;

        server.sendChatMsg(msg);
    }

    private void packet0dDisconnect() {
        String username = ((MPPlayer) server.getEntities().get(uuid))
                .getUsername();

        Packet0dDisconnect packet = new Packet0dDisconnect();
        packet.setData(uuid);
        server.sendPacketToAllClientsExcept(packet, this);

        server.getEntities().removeIndirect(uuid);
        server.removePlayer(username, this);
    }

    private void packet0aLogin(String[] sec) {
        this.uuid = sec[1];
        Packet0aLogin packet = new Packet0aLogin("", "", 0, 0, "");
        //Session id is invalid
        boolean valid = Boolean.parseBoolean(GameAuth.checkValidity(sec[4]));
        if (!valid) {
            server.badSessionIDKick(this);
            return;
        }

        float x = Float.parseFloat(sec[2]);
        float y = Float.parseFloat(sec[3]);
        String addr = socket.getInetAddress().getHostAddress();
        packet.setData(sec[0], addr, sec[1], x, y);

        gui.log("SPPlayer :g:" + sec[0] + ":bl: has joined the game.");
        gui.addPlayer(sec[0]);
        server.packet0aLogin(this, uuid, sec[0], addr, x, y);
        server.sendPacketToAllClientsExcept(packet, this);
    }

    private void packet0bMove(String[] sec) {
        float x = Float.parseFloat(sec[0]);
        float y = Float.parseFloat(sec[1]);
        float vx = Float.parseFloat(sec[2]);
        float vy = Float.parseFloat(sec[3]);
        Packet0bMove packet = new Packet0bMove((int) x, (int) y, vx, vy, uuid);
        server.packet0bMove(x, y, vx, vy, uuid);
        server.sendPacketToAllClientsExcept(packet, this);
    }

    private void packet0cEntityRequest() {
        Packet0cEntityRequest packet = new Packet0cEntityRequest();
        packet.setData(allJSONEntities(true, uuid));
        packet.writeData(print);
    }

    @SuppressWarnings("unchecked")
    private String allJSONEntities(boolean excludePlayer, String uuid) {
        List<MPEntity> ents = new ArrayList<>(
                server.getEntities().getData().values());
        JSONArray object = new JSONArray();

        for (MPEntity ent : ents) {
            JSONObject obj = getJSON(ent);
            if (!uuid.equals(ent.getUUID()))
                object.add(obj);
        }
        return object.toJSONString();
    }

    @SuppressWarnings("unchecked")
    public static JSONObject getJSON(MPEntity ent) {
        JSONObject obj = new JSONObject();
        obj.put("x", ent.getX());
        obj.put("y", ent.getY());
        obj.put("uuid", ent.getUUID());

        if (ent instanceof MPPlayer) {
            MPPlayer player = (MPPlayer) ent;
            obj.put("type", EntityName.PLAYER);
            obj.put("username", player.getUsername());
            obj.put("addr", player.getAddr());
        } else if (ent instanceof MPTorch) {
            obj.put("type", EntityName.TORCH);
        } else if (ent instanceof MPArrow) {
            MPArrow arrow = (MPArrow) ent;
            obj.put("type", EntityName.ARROW);
            obj.put("vx", arrow.getVx());
            obj.put("vy", arrow.getVy());
            obj.put("rot", arrow.getRotation());
        } else if (ent instanceof MPChicken) {
            obj.put("type", EntityName.CHICKEN);
        } else if (ent instanceof MPEgg) {
            obj.put("type", EntityName.EGG);
            obj.put("vx", ent.getVx());
            obj.put("vy", ent.getVy());
        }
        return obj;
    }

    private void startReadingTimer() {
        Thread reader = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String ln = br.readLine();
                        if (ln != null) {
                            processInput(ln);
                        }
                    } catch (IOException e) {
                        Log.err("Client disconnected, so removing him from server.");
                        server.getGui().log(":r:" + player.getUsername() + ":bl: has left the server.");
                        break;
                    }
                }
                try {
                    currentThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Connection_reader_thread");
        reader.start();
    }

    public void writeData(String data) {
        print.println(data);
        print.flush();
    }

    public MPPlayer getPlayer() {
        return player;
    }

    public void setPlayer(MPPlayer player) {
        this.player = player;
    }

    public Socket getSocket() {
        return socket;
    }

    public Server getServer() {
        return server;
    }

    public BufferedReader getBr() {
        return br;
    }

    public PrintStream getPrint() {
        return print;
    }

    public ServerUI getGui() {
        return gui;
    }

    public Thread getCurrentThread() {
        return currentThread;
    }

    public String getUuid() {
        return uuid;
    }
}

package bz.matrix4f.x10.game.networking.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Collection;

import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import bz.matrix4f.x10.game.Game;
import bz.matrix4f.x10.game.chat.Chat;
import bz.matrix4f.x10.game.core.Display;
import bz.matrix4f.x10.game.core.Log;
import bz.matrix4f.x10.game.entity.SPArrow;
import bz.matrix4f.x10.game.entity.SPChicken;
import bz.matrix4f.x10.game.entity.SPEgg;
import bz.matrix4f.x10.game.entity.SPEntity;
import bz.matrix4f.x10.game.entity.SPNetPlayer;
import bz.matrix4f.x10.game.entity.SPPlayer;
import bz.matrix4f.x10.game.entity.SPTorch;
import bz.matrix4f.x10.game.inventory.Inv;
import bz.matrix4f.x10.game.inventory.InvItem;
import bz.matrix4f.x10.game.inventory.InvSlot;
import bz.matrix4f.x10.game.lighting.LightMap;
import bz.matrix4f.x10.game.map.MapLoader;
import bz.matrix4f.x10.game.networking.packet.Packet;
import bz.matrix4f.x10.game.networking.packet.Packet0aLogin;
import bz.matrix4f.x10.game.networking.packet.Packet0cEntityRequest;
import bz.matrix4f.x10.game.networking.packet.Packet0gMapLoader;
import bz.matrix4f.x10.game.networking.server.entities.EntityName;
import bz.matrix4f.x10.game.particle.ParticleGenerator;

public class Client implements Runnable {

	public static String USERNAME;

	private Socket socket;
	private BufferedReader br;
	private PrintStream print;
	private ClientGui gui;
	private Thread currentThread;
	private Game game;
	private SPPlayer player;
	private boolean hasDisconnected = false;

	public Client(ClientGui gui, String host, int port) {
		this.gui = gui;
		loadClientData(host, port);
		game = Game.game;
		player = game.getSPPlayer();
	}
	
	public void loadClientData(String host, int port) {
		try {
			socket = new Socket(host, port);
			br = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			print = new PrintStream(socket.getOutputStream());
		} catch(IOException e) {
			int val = JOptionPane.showOptionDialog(null, "Unable to ping server " + host + ":" + port, "Server " +
					"ClientConn " +
					"Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[] {"Quit", "Retry"}, "Retry");
			Log.err("Unable to ping server " + host + ":" + port);
			if(val != 1)
				System.exit(0);
			else
				loadClientData(host, port);
		}
	}

	@Override
	public void run() {
		startReadingTimer();
		currentThread = Thread.currentThread();

		// Send data to the server that SPPlayer has logged in
		Packet0aLogin loginPacket = new Packet0aLogin(
				USERNAME = Game.loginUsername,
				player.getUUID(), player.getX(), player.getY(), Game.sessionID);
		loginPacket.sendToServer();

		// All the data-requesting packets
		Packet[] dataRequestingPackets = new Packet[] {
				new Packet0cEntityRequest(), new Packet0gMapLoader("")};
		for(Packet packet : dataRequestingPackets)
			packet.sendToServer();

		gui.log("Downloading map and requesting entities...");
		Display.toAWTFrame().setTitle("Warlings of Sparta - " + USERNAME);
		Display.toAWTFrame().requestFocus();
	}

	private void processInput(String input) {
		String id = input.substring(0, 2);
		String data = input.substring(2);
		String[] sec = data.split(Packet.SEPERATOR);
		switch(id) {
			case "0a": // LOGIN
				packet0aLogin(sec);
				break;
			case "0b": // MOVE
				packet0bMove(sec);
				break;
			case "0c": // ENTITY REQEUEST
				packet0cEntityRequest(data);
				break;
			case "0d":// DISCONNECT
				packet0dDisconnect(data);
				break;
			case "0e":// FORCED_DISCONNECT
				packet0eForcedDisconnect(data);
				break;
			case "0f":
				packet0fChatMsg(sec);
				break;
			case "0g":
				packet0gMapLoader(sec);
				break;
			case "0i":
				packet0iInvUpdate(sec);
				break;
			case "0j":
				packet0jAddEntity(sec[0]);
				break;
			case "0k":
				packet0kEntityRemove(sec[0]);
				break;
			case "0l":
				packet0lGameTime(sec[0]);
				break;
			case "0m":
				packet0mParticle(sec);
				break;
			case "0n":
				packet0nEntityHealth(sec);
				break;
			default: {
				gui.log("Data received: " + data + " from " + id);
			}
		}
	}

	private void packet0nEntityHealth(String[] sec) {
		String uuid = sec[0];
		double health = Double.parseDouble(sec[1]);
		SPEntity ent = Game.game.getEntities().get(uuid);
		if(ent != null)
			ent.setHealth(health);
	}

	private void packet0mParticle(String[] sec) {
		int amount = Integer.parseInt(sec[0]);
		int x = Integer.parseInt(sec[1]);
		int y = Integer.parseInt(sec[2]);
		double multiply = Double.parseDouble(sec[3]);
		int lifetime = Integer.parseInt(sec[4]);
		ParticleGenerator pgen = new ParticleGenerator(amount, x, y, multiply, lifetime, null);
		Game.game.getGenerators().add(pgen);
	}

	private void packet0lGameTime(String alphaStr) {
		LightMap.ALPHA_VALUE = Integer.parseInt(alphaStr);
	}

	private void packet0kEntityRemove(String uuid) {
		Game.game.getEntities().removeIndirect(uuid);
	}

	private void packet0jAddEntity(String json) {
		JSONObject obj = (JSONObject) JSONValue.parse(json);
		float x = Float.parseFloat(obj.get("x").toString());
		float y = Float.parseFloat(obj.get("y").toString());
		String uuid = obj.get("uuid").toString();
		String type = obj.get("type").toString();

		switch(type) {
			case EntityName.PLAYER:
				addPlayer(obj.get("username").toString(),
						obj.get("addr").toString(), uuid, x, y);
				break;
			case EntityName.TORCH:
				SPTorch SPTorch = new SPTorch(x, y);
				SPTorch.setUUID(uuid);
				Game.game.getEntities().add(SPTorch);
				break;
			case EntityName.ARROW: {
				float vx = Float.parseFloat(obj.get("vx").toString());
				float vy = Float.parseFloat(obj.get("vy").toString());
				float rotation = Float.parseFloat(obj.get("rot").toString());
				SPArrow arrow = new SPArrow(x, y, vx, vy, rotation);
				arrow.setUUID(uuid);
				Game.game.getEntities().add(arrow);
				break;
			}
			case EntityName.CHICKEN: {
				SPChicken chicken = new SPChicken(x, y);
				chicken.setUUID(uuid);
				Game.game.getEntities().add(chicken);
				break;
			}
			case EntityName.EGG: {
				SPEgg egg = new SPEgg(x, y);
				egg.setUUID(uuid);
				Game.game.getEntities().add(egg);
				break;	
			}
		}
	}

	private void packet0iInvUpdate(String[] sec) {
		JSONObject object = (JSONObject) JSONValue.parse(sec[0]);
		Inv inv = Game.game.getInv();
		String type = (String) object.get("type");
		switch (type) {
			case "set": {
				int id = Integer.parseInt(object.get("id").toString());
				int count = Integer.parseInt(object.get("count").toString());
				String key = (String) object.get("key");
				InvItem item = new InvItem(id);
				item.setCount(count);
				InvSlot newParent = inv.get(key);
				newParent.setChild(item);
				break;
			}
			case "select": {
				int id = Integer.parseInt(object.get("id").toString());
				int count = Integer.parseInt(object.get("count").toString());
				String uuid = (String) object.get("uuid");

				InvItem item = null;
				if(id != -1) {
					item = new InvItem(id);
					item.setCount(count);
				}
				SPEntity hopefullyPlayer = Game.game.getEntities().get(uuid);
				if(hopefullyPlayer instanceof SPNetPlayer) {
					SPNetPlayer player = (SPNetPlayer) hopefullyPlayer;
					player.setSelectedItem(item);
				}
				break;
			}
		}
	}

	private void packet0gMapLoader(String[] sec) {
		String mapdata = "";
		for(String str : sec)
			mapdata += str + Packet.SEPERATOR;
		mapdata = mapdata.substring(0, mapdata.length() - Packet.SEPERATOR.length());
		Game.game.loadNewMap(MapLoader.load(mapdata));
		gui.log("...Map downloaded");
	}
	
	private void packet0fChatMsg(String[] sec) {
		String msg = "";
		for(String str : sec)
			msg += str + Packet.SEPERATOR;
		msg = msg.substring(0, msg.length() - Packet.SEPERATOR.length());
		Chat chat = Game.game.getChat();
		chat.queueMessage(chat.new ChatItem(msg));
	}
	
	private void packet0eForcedDisconnect(String data) {
		showDisconnectDlg(data);
		try {
			br.close();
			print.close();
			socket.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		/*
		 * Remove all multiplayer SPPlayer entities, as they are no longer needed.
		 */
		Collection<SPEntity> entities = Game.game.getEntities().getData()
				.values();
		Object[] entArray = entities.toArray();
		for(Object ent : entArray) {
			if(ent instanceof SPNetPlayer) {
				entities.remove(ent);
			}
		}
	}
	
	/**
	 * A function that displays a JOptionPane dialog
	 * on the screen when it has been forced to disconnect.
	 * Examples: Kicked, Banned, Server closed
	 * 
	 * @param reason The reason to show in the dialog box
	 */
	
	private void showDisconnectDlg(String reason) {
		JOptionPane.showMessageDialog(null,
				"Reason: " + reason + "\n\nNo packets will be received anymore, and expect entities you encounter to be static.",
				"Disconnected from server", JOptionPane.ERROR_MESSAGE);
		hasDisconnected = true;
	}
	
	private void packet0dDisconnect(String uuid) {
		gui.log("Disconnect packet received: " + uuid);
		Game.game.getEntities().removeIndirect(uuid);
	}

	private void packet0aLogin(String[] sec) {
		gui.log("LoginPacket recieved from server- Username: " + sec[0]
				+ ", IP: " + sec[1] + ",UUID:" + sec[2]);
		addPlayer(sec[0], sec[1], sec[2], 0, 0);
	}

	private void addPlayer(String username, String addr, String uuid, float x,
			float y) {
		SPNetPlayer player = new SPNetPlayer(x, y, username, addr, uuid);
		player.setX(x);
		player.setY(y);
		Game.game.getEntities().add(player.getUUID(), player);
	}

	private void packet0bMove(String[] sec) {
		float x = Float.parseFloat(sec[0]);
		float y = Float.parseFloat(sec[1]);
		float vx = Float.parseFloat(sec[2]);
		float vy = Float.parseFloat(sec[3]);
		String uuid = sec[4];
		SPEntity ent = Game.game.getEntities().get(uuid);
		if(ent != null) {
			ent.setX(x);
			ent.setY(y);
			if (!(ent instanceof SPPlayer) || (ent instanceof SPNetPlayer)) {
				ent.setVx(vx);
				ent.setVy(vy);
			}
		}
	}

	private void packet0cEntityRequest(String data) {
		JSONArray array = (JSONArray) JSONValue.parse(data);
		for(int i = 0; i < array.size(); i++) {
			JSONObject obj = (JSONObject) array.get(i);
			packet0jAddEntity(obj.toJSONString());
		}
		gui.log("...All entities successfully retreived");
	}

	private void startReadingTimer() {
		Thread reader = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						String ln = br.readLine();
						if(ln != null)
							processInput(ln);
					} catch(IOException e) {
						if(!hasDisconnected)
							showDisconnectDlg("ClientConn with server aborted.");
						break;
					}
				}
				try {
					currentThread.join();
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, "Client_reader_thread");
		reader.start();
	}

	public void sendData(String data) {
		print.println(data);
		print.flush();
	}

	public Socket getSocket() {
		return socket;
	}

	public BufferedReader getBr() {
		return br;
	}

	public PrintStream getPrint() {
		return print;
	}

	public ClientGui getGui() {
		return gui;
	}

	public Thread getCurrentThread() {
		return currentThread;
	}
}

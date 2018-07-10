package bz.matrix4f.x10.game.networking.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import bz.matrix4f.x10.game.networking.server.entities.MPPlayer;

public class BannedPlayers {
	//Map<Username, BanReason>
	private Map<String, String> bannedPlayers = new HashMap<>();

	public BannedPlayers() {
		try {
			File serverDir = new File("server");
			serverDir.mkdirs();

			File file = new File("server/bans.txt");
			if(!file.exists())
				file.createNewFile();

			load();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void banPlayer(String username, String reason, Server server) {
		bannedPlayers.put(username, reason);
		// If the player is currently in the game, kick it.
		MPPlayer player = server.getPlayerByUsername(username);
		if(player != null)
			server.kickPlayer(server.getConnectionByPlayer(player), username, reason);
		save();
		server.sendChatMsg(":r:*b0[Server]:bl:*0i  Banned player " + username + ".");
	}

	public void unbanPlayer(String username) {
		bannedPlayers.remove(username);
		save();
	}
	
	public String getReason(String username) {
		if(!bannedPlayers.containsKey(username))
			return "";
		return bannedPlayers.get(username);
	}

	public void save() {
		try {
			String text = "";
			for(Entry<String, String> entry : bannedPlayers.entrySet())
				text += entry.getKey() + " " + entry.getValue()
						+ System.lineSeparator();
			if(text.length() >= System.lineSeparator().length())
				text = text.substring(0,
						text.length() - System.lineSeparator().length());

			BufferedWriter writer = new BufferedWriter(
					new FileWriter("server/bans.txt"));
			writer.write(text);
			writer.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	// Loads the banned players
	private void load() throws IOException {
		bannedPlayers.clear();
		BufferedReader reader = new BufferedReader(
				new FileReader("server/bans.txt"));
		String line;
		while((line = reader.readLine()) != null) {
			if(line.trim() != "") {
				String[] data = line.split("[ ]");

				// Reason may have spaces in it
				String reason = "";
				for(String str : data)
					reason += str + " ";
				reason = reason.substring(data[0].length() + 1, reason.length());

				bannedPlayers.put(data[0], reason);
			}
		}
		reader.close();
	}

	public boolean isPlayerBanned(String username) {
		return bannedPlayers.keySet().contains(username);
	}
}

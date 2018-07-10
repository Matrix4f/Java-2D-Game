package bz.matrix4f.x10.game.map;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class TileInfo {
	
	private static HashMap<String, Integer> nameIDs = new HashMap<>();
	private static HashMap<Integer, String> idNames = new HashMap<>();
	
	public static void load() {
		JSONObject object = null;
		try {
			object = (JSONObject) JSONValue.parse(new FileReader("data/res/tiles.json"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		assert object != null;
		JSONArray array = (JSONArray) object.get("data");
		for(int i = 0; i < array.size(); i++) {
			JSONObject obj = (JSONObject) array.get(i);
			String name = obj.get("name").toString();
			int id = Integer.parseInt(obj.get("id").toString());
			bind(name, id);
		}
	}
	
	public static void bind(String name, Integer id) {
		nameIDs.put(name, id);
		idNames.put(id, name);
	}
	
	public static int get(String name) {
		return nameIDs.get(name);
	}
	
	public static String get(int id) {
		return idNames.get(id);
	}
}

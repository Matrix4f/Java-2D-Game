package main.map;

import java.io.FileReader;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class TileInfo {

	public static HashMap<String, Integer> nameIDs = new HashMap<>();
	public static HashMap<Integer, String> idNames = new HashMap<>();
	public static HashMap<String, Boolean> nameCollision = new HashMap<>();
	public static int MAX_ID = 0;

	public static void loadFromFile(String jsonFile) {
		try {

			JSONObject object = (JSONObject) JSONValue
					.parse(new FileReader(jsonFile));
			JSONArray array = (JSONArray) object.get("data");
			for(int i = 0; i < array.size(); i++) {
				JSONObject obj = (JSONObject) array.get(i);
				// Extracts the properties from the JSONObject
				String name = obj.get("name").toString();
				int id = Integer.parseInt(obj.get("id").toString());
				boolean collision = Boolean
						.parseBoolean(obj.get("coll").toString());
				// Stores it in all hashmaps
				bind(name, id, collision);
			}

		} catch(Exception e) {
			JOptionPane.showMessageDialog(null,
					"Unable to load tile configuration: " + jsonFile + "\nType:"
							+ e.getClass().getSimpleName(),
					"Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	public static void bind(String name, Integer id, boolean collision) {
		nameIDs.put(name, id);
		idNames.put(id, name);
		nameCollision.put(name, collision);
		if(id > MAX_ID)
			MAX_ID = id;
	}

	public static boolean isCollision(String name) {
		return nameCollision.get(name);
	}

	public static int getId(String name) {
		return nameIDs.get(name);
	}

	public static String getName(int id) {
		return idNames.get(id);
	}
}

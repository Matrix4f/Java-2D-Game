package bz.matrix4f.x10.game.inventory;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Created by Matrix4f on 5/22/2016.
 */
public class InvItemLoader {

    public static void load() {
        load("data/res/item.json");
    }

    public static void load(String file) {
        try {
            JSONArray array = (JSONArray) JSONValue.parse(new FileReader(file));
            for(int i = 0; i < array.size(); i++) {
                JSONObject object = (JSONObject) array.get(i);
                String name = (String) object.get("name");
                int id = Integer.parseInt(object.get("id").toString());
                ItemRegistry.registerItem(id, name);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

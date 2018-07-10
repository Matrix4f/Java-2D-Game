package bz.matrix4f.x10.game.inventory;

import java.util.HashMap;

/**
 * Created by Matrix4f on 5/23/2016.
 */
public class ItemRegistry {

    public static HashMap<String, Integer> nameIDs = new HashMap<>();
    public static HashMap<Integer, String> idNames = new HashMap<>();

    public static void registerItem(int id, String name) {
        nameIDs.put(name, id);
        idNames.put(id, name);
    }
}

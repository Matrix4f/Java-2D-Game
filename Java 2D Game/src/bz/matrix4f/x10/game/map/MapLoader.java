package bz.matrix4f.x10.game.map;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MapLoader {

	public static String readMapFormat(File file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String all = "";
			String line = "";
			while((line = br.readLine()) != null) {
				all += line + "\n";
			}
			br.close();
			all = all.substring(0, all.length() - 1);
			return all.replace('\n', ' ');
		} catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Map load(String fullMap) {
		String[] tilesStr = fullMap.split(" ");

		int width = i(tilesStr[0]);
		int height = i(tilesStr[1]);

		Map map = new Map(width, height);

		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				String full = tilesStr[y * width + x + 2];
				String data = "{}";
				int indDatBegin = full.indexOf('{');
				int id = i(full);
				if(indDatBegin >= 0) {
					data = full.substring(indDatBegin);
					id = i(full.substring(0, indDatBegin));
				}

				Tile tile = new Tile(x, y, map, id);
				tile.setData(data);
				map.setTile(x, y, tile);
			}
		}
		return map;
	}

	public static Map load(File file) {
		return load(readMapFormat(file));
	}

	private static int i(Object x) {
		return Integer.parseInt(x.toString());
	}
}

package bz.matrix4f.x10.game.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UUID {
	
	public static final String possibilities = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static List<String> usedIDs = new ArrayList<>();
	
	
	public static String generate() {
		String data = "";
		byte length = 16;
		do {
			Random random = new Random();
			for(int i = 0; i < length; i++) {
				int index = random.nextInt(possibilities.length());
				data += possibilities.charAt(index);
			}
		} while(usedIDs.contains(data));
		usedIDs.add(data);
		return data;
	}

	public String getPossibilities() {
		return possibilities;
	}

}
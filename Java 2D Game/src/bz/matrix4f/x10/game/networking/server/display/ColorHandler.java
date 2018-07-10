package bz.matrix4f.x10.game.networking.server.display;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ColorHandler {
	
	public static Map<String, String> colorKey = new HashMap<>();
	
	public static void loadColors() {
		colorKey.put(":r:", "red");
		colorKey.put(":o:", "orange");
		colorKey.put(":y:", "yellow");
		colorKey.put(":g:", "green");
		colorKey.put(":b:", "blue");
		colorKey.put(":v:", "purple");
		colorKey.put(":p:", "pink");
		colorKey.put(":bl:", "black");
		colorKey.put(":w:", "white");
		colorKey.put(":br:", "brown");
		colorKey.put(":gy:", "gray");
	}
	
	public static String convertToHTML(String colorMsg) {
		int closingDivs = amountOfColorCodesUsed(colorMsg);
		
		for(Entry<String, String> entry : colorKey.entrySet()) {
			String colorname = entry.getValue();
			String code = entry.getKey();

			String htmlColor = "<span style=\"color:" + colorname + ";\">";
			
			colorMsg = colorMsg.replace(code, htmlColor);
		}
		
		for(int i = 0; i < closingDivs; i++)
			colorMsg += "</span>";
		return colorMsg;
	}
	
	private static int amountOfColorCodesUsed(String str) {
		int count = 0;
		for(Entry<String, String> entry : colorKey.entrySet()) {
			count += occurencesOfString(str, entry.getKey());
		}
		return count;
	}
	
	//from http://stackoverflow.com/questions/767759/occurrences-of-substring-in-a-string
	private static int occurencesOfString(String str, String findStr) {
		int lastIndex = 0;
		int count = 0;

		while(lastIndex != -1){
		    lastIndex = str.indexOf(findStr,lastIndex);
		    if(lastIndex != -1){
		        count ++;
		        lastIndex += findStr.length();
		    }
		}
		return count;
	}
}

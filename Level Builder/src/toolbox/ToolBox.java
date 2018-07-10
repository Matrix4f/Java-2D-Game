package toolbox;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class ToolBox {

	public static final int RED = 0;
	public static final int GREEN = 1;
	public static final int BLUE = 2;
	public static final Random RANDOM = new Random();
	
	public static String readFileAsString(String file) {
		String all = "";
		String current = "";
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while((current = reader.readLine()) != null)
				all += current + "\n";
			reader.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return all.substring(0, all.length() - 1).replace("\r", "");
	}
	
	public static String[] readFileAsArray(String file) {
		return readFileAsString(file).split("\n");
	}
	
	public static float getAngle(Point target, Point original) {
	    float angle = (float) Math.toDegrees(Math.atan2(target.y - original.y, target.x - original.x));

	    if(angle < 0){
	        angle += 360;
	    }

	    return angle;
	}
	
	public static Point toPoint(float x, float y) {
		return new Point((int) x, (int) y);
	}
	
	public static Point origin() {
		return new Point(0, 0);
	}
	
	public static void filterImage(BufferedImage src, int type, int[] rgb) {

		for(int y = 0; y < src.getHeight(); y++) {
			for(int x = 0; x < src.getWidth(); x++) {
				int clr = src.getRGB(x, y);

				int alpha = (clr >> 24) & 0xff;
				int red = (clr >> 16) & 0xff;
				int green = (clr >> 8) & 0xff;
				int blue = clr & 0xff;

				if(alpha != 0) {
					switch(type) {
						case RED:
							red = rgb[RED];
							green += rgb[GREEN];
							blue += rgb[BLUE];
							break;
						case GREEN:
							red += rgb[RED];
							green = rgb[GREEN];
							blue += rgb[BLUE];
							break;
						case BLUE:
							red += rgb[RED];
							green += rgb[GREEN];
							blue = rgb[BLUE];
							break;
						default:
							throw new IllegalArgumentException("int type must be one of three: ToolBox.RED, ToolBox.GREEN, or ToolBox.BLUE");
					}
					
					int a = (alpha & 0xff) << 24;
					int r = (red & 0xff) << 16;
					int g = (green & 0xff) << 8;
					int b = (blue & 0xff);
					
					src.setRGB(x, y, a | r | g | b);
				}	
			}
		}
	}
}

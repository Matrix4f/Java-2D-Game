package bz.matrix4f.x10.game.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class Loader {
	
	public static int resourcesLoaded = 0;
	
	public static BufferedImage texture(String file) {
		String path = "data/res/texture/" + file + ".png";
		Resources.lastLoaded = path;
		Log.print("Loading resource: " + file);
		try {
			resourcesLoaded++;
			return ImageIO.read(new File(path));
		} catch (IOException e) {
			resourcesLoaded--;
			JOptionPane.showMessageDialog(null, "Unable to load texture: '" + path + "'", "Resource error", JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}
	
	public static BufferedImage[][] textureAtlas(String file, int indivW, int indivH) {
		BufferedImage parent = texture(file);
		int aw = parent.getWidth() / indivW;
		int ah = parent.getHeight() / indivH;
		BufferedImage[][] array = new BufferedImage[aw][ah];
		for(int x = 0; x < array.length; x++) {
			for(int y = 0; y < array[x].length; y++) {
				array[x][y] = parent.getSubimage(x * indivW, y * indivH, indivW, indivH);
			}
		}
		return array;
	}
	
	
}

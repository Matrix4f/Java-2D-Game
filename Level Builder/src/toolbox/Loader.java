package toolbox;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class Loader {
	public static BufferedImage createImage(String file) {
		String path = file;
		try {
			return ImageIO.read(new File(path));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Unable to load resource: '" + path + "'", "Resource error- LevelCreator", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		return null;
	}
	
	public static BufferedImage[][] createAtlas(String file, int indivW, int indivH) {
		BufferedImage parent = createImage(file);
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
	
	public static void loadFonts(String... files) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		for(String file: files)
			try {
				ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("resource" + File.separator + "fonts" + File.separator + file)));
			} catch (FontFormatException | IOException e) {
				e.printStackTrace();
			}
	}
}

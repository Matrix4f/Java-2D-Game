package bz.x10.matrix4f.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings {
	
	public static String VERSION = "";
	
	public static void save() {
		Properties prop = new Properties();
		prop.setProperty("version", VERSION);
		try {
			prop.store(new FileOutputStream("launcher.properties"), null);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void load() {
		File file = new File("launcher.properties");
		if(!file.exists())
			return;
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(file));
		} catch(IOException e) {
			e.printStackTrace();
		}
		VERSION = prop.getProperty("version");
	}
}

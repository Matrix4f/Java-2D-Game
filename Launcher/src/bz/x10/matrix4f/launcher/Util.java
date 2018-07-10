package bz.x10.matrix4f.launcher;

import javax.swing.JOptionPane;

public class Util {

	public static void error(String title, String msg) {
		JOptionPane.showMessageDialog(Main.This.getFrame(), msg, title,
				JOptionPane.ERROR_MESSAGE);
	}
}

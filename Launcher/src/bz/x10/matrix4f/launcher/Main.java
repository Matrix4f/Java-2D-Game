package bz.x10.matrix4f.launcher;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {

	public static final int WIDTH = 655, HEIGHT = WIDTH * 3 / 4;

	private static JFrame frame;
	public static Main This;

	public Main() {
		This = this;

		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(
				new ImageIcon(Class.class.getResource("/resource/winicon.png"))
						.getImage());
		frame.setSize(WIDTH, HEIGHT);
		frame.setVisible(true);

		frame.setContentPane(new PanelLogin());
//		frame.setContentPane(new PanelMain());
		frame.validate();
	}

	public static void main(String[] args) {
		Settings.load();
		for(LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
			if(laf.getName().equals("Metal")) {
				try {
					UIManager.setLookAndFeel(laf.getClassName());
				} catch(ClassNotFoundException | InstantiationException
						| IllegalAccessException
						| UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
			}
		}
		new Main();
//		new Thread() {
//			public void run() {
//				while(true) {
//					System.out.println(frame.getWidth());
//				}
//			}
//		}.start();
	}

	public JFrame getFrame() {
		return frame;
	}

	public static Main getThis() {
		return This;
	}
}

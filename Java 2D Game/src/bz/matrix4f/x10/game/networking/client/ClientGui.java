package bz.matrix4f.x10.game.networking.client;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import bz.matrix4f.x10.game.Game;

public class ClientGui extends Frame {

	private static final long serialVersionUID = 1268924259688945559L;
	private JTextArea console;

	public ClientGui() {
		super("Client console");
		JScrollPane scroll = new JScrollPane(new JTextArea());
		console = (JTextArea) scroll.getViewport().getView();
		console.setFont(new Font("Consolas", Font.PLAIN, 12));
		console.setText("- - C L I E N T - -\n");
		console.setEditable(false);
		add(scroll);
		
		setSize(new Dimension(300, 200));
		setAlwaysOnTop(true);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Game.close();
			}
		});
		setVisible(true);
	}

	public void log(String x) {
		console.append(x + "\n");
	}
}

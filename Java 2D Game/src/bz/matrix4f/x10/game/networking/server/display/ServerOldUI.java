package bz.matrix4f.x10.game.networking.server.display;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import bz.matrix4f.x10.game.map.MapLoader;
import bz.matrix4f.x10.game.networking.packet.Packet0gMapLoader;
import bz.matrix4f.x10.game.networking.server.Server;

public class ServerOldUI extends JFrame {

	private static final long serialVersionUID = 1268924259688945559L;
	public static final String STARTING_CONSOLE_TEXT = "<html>- - - ServerConsole - - -<br>";

	private JEditorPane console;
	private String consoleText = STARTING_CONSOLE_TEXT, consoleHTMLText = STARTING_CONSOLE_TEXT;

	private JList<String> clientList;
	private DefaultListModel<String> clientListModel;
	private JTextField commandTF;
	private Server server;

	public ServerOldUI() {
		super("Server");
		setLayout(new GridBagLayout());
		setIconImage(new ImageIcon("server/winicon.png").getImage());
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				server.stop();
			}
		});
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = c.gridy = 0;
		c.ipadx = 100;
		c.ipady = 100;
		c.insets = new Insets(10, 10, 10, 10);

		JScrollPane scroll = new JScrollPane(
				new JEditorPane("text/html", consoleText));
		console = (JEditorPane) scroll.getViewport().getView();
		console.setText(STARTING_CONSOLE_TEXT);
		console.setEditable(false);
		scroll.setMinimumSize(new Dimension(100, 200));
		add(scroll, c);

		c.gridx++;
		c.ipadx = 100;
		clientListModel = new DefaultListModel<>();
		clientList = new JList<>(clientListModel);
		clientList.setBorder(BorderFactory.createTitledBorder("Players"));
		clientList.setMinimumSize(new Dimension(100, 200));
		add(clientList, c);

		c.ipady = c.ipady = c.gridx = 0;
		c.gridy++;
		commandTF = new JTextField(10);
		commandTF.setBorder(BorderFactory.createTitledBorder("Command"));
		commandTF.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
					processCmd(commandTF.getText());
			}
		});
		add(commandTF, c);

		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	private void processCmd(String text) {
		try {
			String[] tokens = text.split("[ ]");
			commandTF.setText("");

			if(tokens[0].equalsIgnoreCase("ban")) {
				// Parse the reason (including spaces) out of the tokens
				String reason = text
						.substring(tokens[0].length() + tokens[1].length() + 2);
				server.getBannedPlayers().banPlayer(tokens[1], reason, server);
				log("SPPlayer " + tokens[1] + " was succesfully banned.");

			} else if(tokens[0].equalsIgnoreCase("unban")) {
				server.getBannedPlayers().unbanPlayer(tokens[1]);
				log("SPPlayer " + tokens[1] + " was succesfully unbanned.");
			} else if(tokens[0].equalsIgnoreCase("kick")) {
				// Parse the reason (including spaces) out of the tokens
				String reason = "Kicked by console";
				if(tokens.length >= 3)
					reason = text.substring(
							tokens[0].length() + tokens[1].length() + 2);
				server.kickPlayer(tokens[1], reason);
				log("SPPlayer " + tokens[1] + " was kicked.");
			} else if(tokens[0].equalsIgnoreCase("setmap")) {
				// Parse the filename (including spaces) out of the tokens
				String file = text.substring(tokens[0].length() + 1);
				String mapdata = MapLoader.readMapFormat(new File(file));
				server.setMapdata(mapdata);
				server.sendPacketToAllClients(new Packet0gMapLoader(mapdata));
			} else if(tokens[0].equalsIgnoreCase("clearlog")) {
				console.setText(STARTING_CONSOLE_TEXT);
				consoleText = STARTING_CONSOLE_TEXT;
				consoleHTMLText = STARTING_CONSOLE_TEXT;
				pack();
			} else if(tokens[0].equals("chat")) {
				log(tokens[1]);
			}
		} catch(Exception e) {
		}
	}

	public void addClient(String name) {
		clientListModel.addElement(name);
	}

	public void removeClient(String name) {
		clientListModel.removeElement(name);
		pack();
	}

	public void log(String x) {
		consoleText += x + "<br>";
		consoleHTMLText = ColorHandler.convertToHTML(consoleText);
		console.setText(consoleHTMLText);
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}
}

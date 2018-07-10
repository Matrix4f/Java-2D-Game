package bz.x10.matrix4f.launcher;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import bz.x10.matrix4f.launcher.auth.AuthService;
import bz.x10.matrix4f.launcher.auth.AuthVar;
import security.SecurityIO;

public class PanelLogin extends JPanel {

	private static final long serialVersionUID = -2436264373964934853L;

	private JTextField usernameField;
	private JPasswordField passwordField;
	private JButton loginBtn;
	private JProgressBar progressBar;
	private Font labelFont = new Font("Corbel", Font.PLAIN, 15);
	private Font textFieldFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
	private Border textFieldBorder = BorderFactory.createLineBorder(Color.GRAY);
	
	public PanelLogin() {
		Main.This.getFrame().setTitle("Sign-in to Launcher");
		setLayout(new GridBagLayout());
		setBackground(new Color(144, 195, 212));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = c.gridy = 0;

		int columns = 15;
		
		usernameField = new JTextField(columns);
		usernameField.setOpaque(false);
		usernameField.setBackground(getBackground());
		usernameField.setBorder(textFieldBorder);
		usernameField.setFont(textFieldFont);

		passwordField = new JPasswordField(columns);
		passwordField.setOpaque(false);
		passwordField.setBackground(getBackground());
		passwordField.setBorder(textFieldBorder);
		passwordField.setFont(textFieldFont);

		loginBtn = new JButton("Log-in");
		loginBtn.setBackground(getBackground().darker());
		loginBtn.setFont(labelFont.deriveFont(Font.BOLD, 13));
		loginBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					public void run() {
						progressBar.setVisible(true);
						logIn();
						progressBar.setVisible(false);
					}
				}.start();
			}
		});
		
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setForeground(getBackground().darker());
		progressBar.setOpaque(false);
		progressBar.setBorder(BorderFactory.createLineBorder(getBackground().darker()));
		progressBar.setPreferredSize(new Dimension(200, 25));
		progressBar.setVisible(false);
		
		int padding = 5;
		c.insets = new Insets(padding, padding, padding, padding);

		add(createLabel("Username"), c);
		c.gridx++;
		add(usernameField, c);
		c.gridy++;
		c.gridx = 0;
		add(createLabel("Password"), c);
		c.gridx++;
		add(passwordField, c);
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;
		add(loginBtn, c);
		c.gridy++;
		add(progressBar, c);
		
		loadFileData();
	}
	
	private void loadFileData() {
		File file = new File("auth.dat");
		if(!file.exists())
			return;
		try {
			JSONObject array = (JSONObject) JSONValue.parse(SecurityIO.read(file, 153, 3, 2835, 2));
			String username = array.get("username").toString();
			String passwordStr = array.get("password").toString();
			
			usernameField.setText(username);
			passwordField.setText(passwordStr);
			passwordStr = null;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setToMainPanel() {
		setVisible(false);
		Main.This.getFrame().remove(this);
		Main.This.getFrame().setContentPane(new PanelMain());
		Main.This.getFrame().validate();
	}
	
	private void logIn() {
		String username = usernameField.getText();
		String password = new String(passwordField.getPassword());
		if(username.equals("")) {
			Util.error("Form error", "Enter a username!");
			return;
		}
		if(password.equals("")) {
			Util.error("Form error", "Enter a password!");
			return;
		}

		String returnVal = AuthService.login(username, password);
		if(returnVal == null) {
			Util.error("Connection error",
					"Could not contact the authentication servers.");
			return;
		}
		JSONObject obj = (JSONObject) JSONValue.parse(returnVal);
		boolean validLogin = obj.get("valid").toString().equals("true");

		if(validLogin == false) {
			Util.error("Sign-in error", "Invalid log-in credentials!");
			return;
		} else {
			String sessionID = obj.get("session_id").toString();
			JOptionPane.showMessageDialog(Main.This.getFrame(),
					"Signed in as " + username + ".", "Sign-in success",
					JOptionPane.PLAIN_MESSAGE);

			AuthVar.Password = passwordField.getPassword();
			AuthVar.SessionID = sessionID;
			AuthVar.Username = username;
			AuthVar.Valid = true;
			AuthVar.flushData();
			setToMainPanel();
		}
	}

	private JLabel createLabel(String text) {
		JLabel lbl = new JLabel(text);
		lbl.setFont(labelFont);
		return lbl;
	}
}

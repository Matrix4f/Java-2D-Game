package bz.x10.matrix4f.launcher;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import bz.x10.matrix4f.launcher.auth.AuthVar;

public class PanelMain extends JPanel {

	private static final long serialVersionUID = -4888622923352781120L;

	private JButton selVersionBtn, playBtn, saveSettingsBtn, snooperBtn;
	private JTextPane pane;
	private JScrollPane scroll;
	private String version = "1.0.0.jar";

	private Font btnFont = new Font("Corbel", Font.BOLD, 13);

	public PanelMain() {
		Main.This.getFrame().setTitle("Launcher");
		setLayout(new GridBagLayout());

		Dimension btnSize = new Dimension(200, 60);

		version = Settings.VERSION;
		String simpleVersion = "";
		if(Settings.VERSION.length() > 4)
			simpleVersion = version.substring(0, version.length() - 4);
		else {
			simpleVersion = "1.0.0";
			version = Settings.VERSION = "1.0.0.jar";
		}
		selVersionBtn = new JButton("Version "
				+ simpleVersion + " (change)");
		selVersionBtn.setFont(btnFont);
		selVersionBtn.setPreferredSize(btnSize);

		playBtn = new JButton("Play");
		playBtn.setFont(btnFont.deriveFont((float) 19));
		playBtn.setPreferredSize(btnSize);

		saveSettingsBtn = new JButton("Force-Save settings", new ImageIcon(
				Class.class.getResource("/resource/settings.png")));
		saveSettingsBtn.setFont(btnFont);
		saveSettingsBtn.setPreferredSize(btnSize);

		snooperBtn = new JButton("Snooper", new ImageIcon(
				Class.class.getResource("/resource/snooper.png")));
		snooperBtn.setFont(btnFont);
		snooperBtn.setPreferredSize(btnSize);

		try {
			pane = new JTextPane();
			pane.setContentType("text/html");
			pane.setPage(new URL("http://matrix4f.x10.bz/launcher.html"));
		} catch(IOException e) {
			e.printStackTrace();
		}
		pane.setEditable(false);
		pane.setPreferredSize(new Dimension(500, 250));

		scroll = new JScrollPane(pane);
		scroll.setPreferredSize(pane.getPreferredSize());

		addAllComponents();
		addActionListeners();
	}

	private void addActionListeners() {
		snooperBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JDialog dlg = new JDialog();
				dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dlg.setTitle("SnooperMode");
				dlg.setLayout(new GridBagLayout());

				GridBagConstraints c = new GridBagConstraints();
				c.gridx = c.gridy = 0;
				int padding = 5;
				c.insets = new Insets(padding, padding, padding, padding);

				JPanel authPanel = new JPanel();
				authPanel
						.setBorder(BorderFactory.createTitledBorder("Sign-in"));
				authPanel.setLayout(new GridBagLayout());
				authPanel.add(label("Username"), c);
				JTextField username = labelTextField(AuthVar.Username);
				JTextField password = labelTextField(
						AuthVar.genObfuscatedPassword('*'));
				JTextField sessionId = labelTextField(AuthVar.SessionID);
				c.gridx++;
				authPanel.add(username, c);
				c.gridx = 0;
				c.gridy++;
				authPanel.add(label("Password"), c);
				c.gridx++;
				authPanel.add(password, c);
				c.gridx = 0;
				c.gridy++;
				authPanel.add(label("SessionID"), c);
				c.gridx++;
				authPanel.add(sessionId, c);

				JButton close = new JButton("Close");
				close.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dlg.dispose();
					}
				});

				c.gridx = c.gridy = 0;
				c.gridwidth = c.gridheight = 1;
				dlg.add(authPanel, c);
				c.gridy++;
				dlg.add(close, c);

				dlg.setIconImage(((ImageIcon) snooperBtn.getIcon()).getImage());
				dlg.setVisible(true);
				dlg.pack();
				dlg.setLocationRelativeTo(PanelMain.this);
			}

			private JTextField labelTextField(String text) {
				JTextField field = new JTextField(10);
				field.setText(text);
				field.addKeyListener(new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent e) {
						e.consume();
						field.setText(text);
						field.setCaretPosition(0);
						Toolkit.getDefaultToolkit().beep();
					}
				});
				field.setFont(new Font("Corbel", Font.PLAIN, 13));
				return field;
			}

			private JLabel label(String text) {
				JLabel lbl = new JLabel(text);
				lbl.setFont(new Font("Corbel", Font.BOLD, 13));
				return lbl;
			}
		});
		
		saveSettingsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Settings.save();
			}
		});
		
		playBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String path = "versions/" + version;
				File file = new File(path);
				if(!file.exists()) {
					return;
				}
				try {
					Runtime.getRuntime()
							.exec("java -jar " + path + " " + AuthVar.Username
									+ " " + new String(AuthVar.Password));
				} catch(IOException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});

		selVersionBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JDialog dlg = new JDialog();
				dlg.setIconImage(((ImageIcon) saveSettingsBtn.getIcon()).getImage());
				dlg.setTitle("Select version");
				dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dlg.setLayout(new GridBagLayout());
				GridBagConstraints c = new GridBagConstraints();
				c.gridx = c.gridy = 0;
				c.insets = new Insets(10, 10, 10, 10);

				JList<String> list = new JList<>(
						new File("versions/").list(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.endsWith(".jar");
					}
				}));
				list.setSelectedValue(Settings.VERSION, true);

				JButton ok = new JButton("OK");
				ok.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dlg.dispose();
						if(list.getSelectedIndex() != -1) {
							version = list.getSelectedValue();
							Settings.VERSION = version;
							selVersionBtn
									.setText(
											"Version "
													+ version.substring(0,
															version.length()
																	- 4)
											+ " (change)");
							Settings.save();
						}
					}
				});

				dlg.add(new JScrollPane(list), c);
				c.gridy++;
				dlg.add(ok, c);

				dlg.pack();
				dlg.setLocationRelativeTo(PanelMain.this);
				dlg.setVisible(true);
			}
		});
	}

	private void addAllComponents() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = c.gridy = 0;
		int padding = 5;
		c.insets = new Insets(padding, padding, padding, padding);
		add(selVersionBtn, c);
		c.gridx++;
		add(saveSettingsBtn, c);
		c.gridx++;
		add(snooperBtn, c);

		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 3;
		add(scroll, c);
		c.gridy++;
		c.gridx = 0;
		add(playBtn, c);
	}
}

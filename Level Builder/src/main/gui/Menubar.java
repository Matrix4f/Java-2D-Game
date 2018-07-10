package main.gui;

import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;

import main.DisplayFrame;
import main.LevelCreator;
import main.map.Block;
import main.map.Level;

public class Menubar extends JMenuBar implements ActionListener {

	private static final long serialVersionUID = 1L;

	private Level lvl;
	private String currentFile = "";

	public Menubar(Level lvl) {
		this.lvl = lvl;
		JMenu file = new JMenu("File");
		add(file);
		item(file, "New Map", "newlevel", KeyEvent.VK_N, Event.CTRL_MASK);
		item(file, "Load", "open", KeyEvent.VK_L, Event.CTRL_MASK);
		item(file, "Save", "save", KeyEvent.VK_S, Event.CTRL_MASK);
		item(file, "Save As", "saveas", KeyEvent.VK_F, Event.CTRL_MASK);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String x = e.getActionCommand();
		if(x.equals("save")) {
			saveLevel();
		} else if(x.equals("saveas")) {
			saveAs();
		} else if(x.equals("newlevel")) {
			createNewLevel();
		} else if(x.equals("open")) {
			open();
		}
	}

	private void open() {
		if(lvl == null)
			return;
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setApproveButtonText("Open level");
		if(jfc.showOpenDialog(
				DisplayFrame.getFrame()) == JFileChooser.APPROVE_OPTION) {
			currentFile = jfc.getSelectedFile().getAbsolutePath();
			String mapdata = "";
			try {
				BufferedReader br = new BufferedReader(new FileReader(currentFile));
				String all = "";
				String line = "";
				while((line = br.readLine()) != null) {
					all += line + "\n";
				}
				br.close();
				all = all.substring(0, all.length() - 1);
				all = all.replace('\n', ' ');
				mapdata = all;
				all = null;
			} catch(IOException e) {
				JOptionPane.showMessageDialog(null,
						"Unable to read map file: " + currentFile
								+ "\nError: IOException",
						"Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				return;
			}

			String[] tilesStr = mapdata.split(" ");

			int width = i(tilesStr[0]);
			int height = i(tilesStr[1]);
			Level lvl = new Level(width, height);

			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					String full = tilesStr[y * width + x + 2];
					String data = "{}";
					int indDatBegin = full.indexOf('{');
					int id = i(full);
					if(indDatBegin >= 0) {
						data = full.substring(indDatBegin);
						id = i(full.substring(0, indDatBegin));
					}

					// TODO: Use data tags
					Block bl = new Block(lvl, x * Level.BLOCK_SIZE,
							y * Level.BLOCK_SIZE, id);
					lvl.getBlocks()[x][y] = bl;
				}
			}
			LevelCreator.This.setLvl(lvl);
		}
	}

	private int i(Object x) {
		return Integer.parseInt(x.toString());
	}

	private void createNewLevel() {
		if(lvl != null && JOptionPane.showOptionDialog(DisplayFrame.getFrame(),
				"If this map is unsaved, the data will be permanently erased!\nDo you wish to continue?",
				"New Map - Confirmation", JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE, null, null, 1) == 1)
			return;
		JDialog dlg = new JDialog();
		dlg.setLayout(new GridBagLayout());
		dlg.setTitle("Map size");

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = c.gridy = 0;

		JSpinner wspin = new JSpinner();
		wspin.setModel(new SpinnerNumberModel(50, 1, 100, 1));

		JSpinner hspin = new JSpinner();
		hspin.setModel(new SpinnerNumberModel(50, 1, 100, 1));

		JButton btn = new JButton("Create");

		int p = 5;
		c.insets = new Insets(p, p, p, p);
		dlg.add(wspin, c);
		c.gridx++;
		dlg.add(new JLabel("by"), c);
		c.gridx++;
		dlg.add(hspin, c);
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 3;
		dlg.add(btn, c);

		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LevelCreator.This.setLvl(
						new Level(Integer.parseInt(wspin.getValue().toString()),
								Integer.parseInt(hspin.getValue().toString())));
				currentFile = "";
				dlg.dispose();
			}
		});

		dlg.pack();
		dlg.setLocationRelativeTo(DisplayFrame.getFrame());
		dlg.setVisible(true);
	}

	private void saveAs() {
		if(lvl == null)
			return;
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setApproveButtonText("Save level");
		if(jfc.showSaveDialog(
				DisplayFrame.getFrame()) == JFileChooser.APPROVE_OPTION) {
			currentFile = jfc.getSelectedFile().getAbsolutePath();
			saveLevel();
		}
	}

	private void saveLevel() {
		if(lvl == null)
			return;
		if(currentFile.equals(""))
			saveAs();
		String filedata = "";
		String sep = System.lineSeparator();
		Block[][] blocks = lvl.getBlocks();
		int width = blocks.length;
		int height = blocks[0].length;
		filedata += width + " " + height + sep;
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				Block b = blocks[x][y];
				filedata += b.getId() + " ";
			}
			filedata = filedata.substring(0, filedata.length() - 1);
			filedata += sep;
		}

		try {
			BufferedWriter writer = new BufferedWriter(
					new FileWriter(currentFile));
			writer.write(filedata);
			writer.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void item(JMenu parent, String name, String action, int key,
			int modifiers) {
		JMenuItem item = new JMenuItem(name);
		item.setActionCommand(action);
		item.addActionListener(this);
		if(key != -1)
			item.setAccelerator(KeyStroke.getKeyStroke(key, modifiers));
		parent.add(item);
	}

	
	public void setLvl(Level lvl) {
		this.lvl = lvl;
	}
}

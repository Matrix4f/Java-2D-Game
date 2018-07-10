package main.gui.tooloption;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import main.DisplayFrame;
import main.map.Block;
import main.map.Level;
import main.map.TileInfo;

@SuppressWarnings("serial")
public class FillAllToolOption extends JPanel {

	private JButton use;

	public FillAllToolOption(Level lvl) {
		use = new JButton("Fill all");
		add(use);

		use.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int id = lvl.getSelectedID();
				if(id == -1 || id > TileInfo.MAX_ID) {
					JOptionPane.showMessageDialog(DisplayFrame.getFrame(), "No block selected!", "Error", JOptionPane.PLAIN_MESSAGE);
					return;
				}
				int val = JOptionPane.showOptionDialog(DisplayFrame.getFrame(),
						"Are you sure you want to replace the\nentire level with the selected block?",
						"Confirm", JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE, null, null, 1);
				if(val == 1)
					return;
				
				Block[][] tiles = lvl.getBlocks();
				Block example = new Block(lvl, 0, 0, id);
				for(int x = 0; x < tiles.length; x++) {
					for(int y = 0; y < tiles[x].length; y++) {
						tiles[x][y] = example.copy(x * Level.BLOCK_SIZE,
								y * Level.BLOCK_SIZE);
					}
				}
			}
		});
	}
}

package main.gui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import main.DisplayFrame;
import main.map.Level;
import main.map.TileInfo;

@SuppressWarnings("serial")
public class BlockSelectionDialog extends JDialog {

	public static JTable table;
	private DefaultTableModel model;
	
	public BlockSelectionDialog(Level lvl) {
		Object[][] rowData = getRowData(lvl);
		Object[] columns = new Object[] {"", "", "", ""};
		model = new DefaultTableModel(rowData, columns) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table = new JTable(model) {
			/**
			 * Use the default renderer for images- images will have their image
			 * representation, not toString()
			 */
			@Override
			public Class<?> getColumnClass(int column) {
				return getValueAt(0, column).getClass();
			}
		};
		table.setCellSelectionEnabled(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowHeight(34);
		table.setPreferredScrollableViewportSize(new Dimension(240, 150));
		initContextMenu();
		add(new JScrollPane(table));
		pack();
		setTitle("Select a tile...");
		setAlwaysOnTop(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLocation(DisplayFrame.getSize().width - getWidth() - 10, 40);
		setResizable(false);
	}

	private void initContextMenu() {
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int r = table.rowAtPoint(e.getPoint());
				if(r >= 0 && r < table.getRowCount()) {
					table.setRowSelectionInterval(r, r);
				} else {
					table.clearSelection();
				}

				int rowindex = table.getSelectedRow();
				if(rowindex < 0)
					return;
				if(e.isPopupTrigger() && e.getComponent() instanceof JTable) {
					int row = table.getSelectedRow();
					int col = table.getSelectedColumn();
					int id = col + row * 4;
					if(id > TileInfo.MAX_ID || row == -1 || col == -1)
						return;
					JPopupMenu popup = new JPopupMenu();
					popup.add(new JMenuItem("Properties"));
					popup.addSeparator();
					
					popup.add(new JMenuItem("Name: " + TileInfo.getName(id)));
					popup.add(new JMenuItem("ID: " + id));
					popup.add(new JMenuItem("TextureCoords: {" + id % 8 + "," + id / 8 + "}"));
					popup.add(new JMenuItem("Collision: " + TileInfo.isCollision(TileInfo.getName(id))));
					
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}
	
	private Object[][] getRowData(Level lvl) {
		Object[][] rowData;
		int blockSize = TileInfo.idNames.size();
		int h = (int) Math.ceil(blockSize / 4d);
		int w = 4;
		System.out.println(h);
		rowData = new Object[h][w];
		int index = 0;
		for(int y = 0; y < h; y++) {
			for(int x = 0; x < w; x++) {
				if(index <= TileInfo.MAX_ID)
					rowData[y][x] = new ImageIcon(lvl
							.imgAt(new int[] {index % 8, index / 8})
							.getScaledInstance(32, 32, Image.SCALE_SMOOTH));
				else
					rowData[y][x] = new ImageIcon();
				index++;
			}
		}
		return rowData;
	}

	public JTable getTable() {
		return table;
	}

	public DefaultTableModel getModel() {
		return model;
	}
}

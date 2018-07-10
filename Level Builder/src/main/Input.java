package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import main.gui.Toolbar;
import main.gui.tooloption.BrushToolOption;
import main.map.Block;
import main.map.Level;

public class Input implements KeyListener, MouseWheelListener,
		MouseMotionListener, MouseListener {
	private boolean isCtrlDown = false;

	public static Level lvl;
	private byte current = 0;
	private int x1, y1, x2, y2;

	public void mouseWheelMoved(MouseWheelEvent e) {
		int rot = e.getWheelRotation();
		Camera cam = LevelCreator.This.getLvl().getCam();
		if(!isCtrlDown)
			cam.setX(cam.getX() - Level.BLOCK_SIZE * rot);
		else {
			cam.setY(cam.getY() - Level.BLOCK_SIZE * rot);
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_CONTROL || e.isControlDown())
			isCtrlDown = true;
	}

	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_CONTROL || e.isControlDown())
			isCtrlDown = false;
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if(Toolbar.CURRENT_TOOL.equals("brush")) {
			useBrushTool(e);
		} else if(Toolbar.CURRENT_TOOL.equals("line")) {
			/*
				if(current == 0) {
					x1 = e.getX() / Level.BLOCK_SIZE;
					y1 = e.getY() / Level.BLOCK_SIZE;
					current = 1;
				} else {
					x2 = e.getX() / Level.BLOCK_SIZE;
					y2 = e.getY() / Level.BLOCK_SIZE;
					int[][] pts = findPointsInLine(x1, y1, x2, y2);
					for(int[] coords : pts) {
						if(lvl.getSelectedID() != -1)
							lvl.getBlocks()[coords[0]][coords[1]] = new Block(lvl,
									coords[0] * Level.BLOCK_SIZE,
									coords[1] * Level.BLOCK_SIZE,
									lvl.getSelectedID());
					}
					current = 0;
				}
			*/
		}
	}

	public static int[][] findPointsInLine(int x1, int y1, int x2, int y2) {
		int dx = x2 - x1;
		int dy = y2 - y1;
		double slope = (double) dy / dx;
		List<String> ls = new ArrayList<>();
		for(double x = 0; x < dx; x += 0.1) {
			int y = (int) (x * slope);
			ls.add((x + x1) + "," + (y + y1));
		}
		HashSet<String> hs = new HashSet<>(ls);
		String[] strs = (String[]) hs.toArray(new String[hs.size()]);
		int[][] pts = new int[strs.length][2];
		for(int i = 0; i < strs.length; i++) {
			String[] secs = strs[i].split(",");
			pts[i][0] = (int) Double.parseDouble(secs[0]);
			pts[i][1] = (int) Double.parseDouble(secs[1]);
		}
		return pts;
	}

	private void useBrushTool(MouseEvent e) {
		BrushToolOption brush = (BrushToolOption) lvl.getTod().getOptionPanel();
		int size = brush.getSlider().getValue();
		int id = lvl.getSelectedID();
		if(id == -1)
			return;
		Camera cam = lvl.getCam();
		int xLvlPos = (int) ((e.getX() - cam.getX()) / Level.BLOCK_SIZE);
		int yLvlPos = (int) ((e.getY() - cam.getY()) / Level.BLOCK_SIZE);

		Block b = new Block(lvl, xLvlPos * Level.BLOCK_SIZE,
				yLvlPos * Level.BLOCK_SIZE, id);
		try {
			Block[][] bl = lvl.getBlocks();
			bl[xLvlPos][yLvlPos] = b;
			List<int[]> ab = new ArrayList<>();
			switch(size) {
				case 2:
					ab.add(new int[] {0, 1});
					ab.add(new int[] {1, 0});
					ab.add(new int[] {1, 1});
					break;
				case 3:
					ab.add(new int[] {-1, -1});
					ab.add(new int[] {0, -1});
					ab.add(new int[] {1, -1});

					ab.add(new int[] {-1, 0});
					ab.add(new int[] {1, 0});

					ab.add(new int[] {1, 1});
					ab.add(new int[] {-1, 1});
					ab.add(new int[] {0, 1});
					break;
				case 4:
					ab.add(new int[] {0, -1});
					ab.add(new int[] {1, -1});

					ab.add(new int[] {-1, 0});
					ab.add(new int[] {1, 0});
					ab.add(new int[] {2, 0});

					ab.add(new int[] {-1, 1});
					ab.add(new int[] {0, 1});
					ab.add(new int[] {1, 1});
					ab.add(new int[] {2, 1});

					ab.add(new int[] {0, 2});
					ab.add(new int[] {1, 2});
					break;
				case 5:
					ab.add(new int[] {-1, -1});
					ab.add(new int[] {0, -1});
					ab.add(new int[] {1, -1});

					ab.add(new int[] {-1, 0});
					ab.add(new int[] {1, 0});

					ab.add(new int[] {1, 1});
					ab.add(new int[] {-1, 1});
					ab.add(new int[] {0, 1});

					ab.add(new int[] {-2, 0});
					ab.add(new int[] {0, -2});
					ab.add(new int[] {2, 0});
					ab.add(new int[] {0, 2});
					break;
			}
			if(size != 1) {
				for(int i = 0; i < ab.size(); i++) {
					int[] coords = ab.get(i);
					bl[xLvlPos + coords[0]][yLvlPos + coords[1]] = b.copy(
							Level.BLOCK_SIZE * (xLvlPos + coords[0]),
							Level.BLOCK_SIZE * (yLvlPos + coords[1]));
					coords = null;
				}
			}
			ab = null;
		} catch(ArrayIndexOutOfBoundsException ex) {

		}
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		if(Toolbar.CURRENT_TOOL.equals("brush")) {
			useBrushTool(e);
		}
	}

	public void mouseMoved(MouseEvent e) {
	}
}

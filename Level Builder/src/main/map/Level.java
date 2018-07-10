package main.map;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JTable;

import main.Camera;
import main.DisplayFrame;
import main.gui.BlockSelectionDialog;
import main.gui.tooloption.ToolOptionDialog;
import toolbox.Loader;

public class Level {

	public static final String TILES_IMG = "C:/Users/vgsoh_000/workspace/w2/Networking/res/res/texture/tilemap.png";
	public static final String TILES_CONFIG = "C:/Users/vgsoh_000/workspace/w2/Networking/res/res/tiles.json";
	public static final int BLOCK_SIZE = 24;

	private BufferedImage[][] atlas;
	private Block[][] blocks;
	private Camera cam;

	private BlockSelectionDialog sel;
	private ToolOptionDialog tod;

	public Level(int w, int h) {
		cam = new Camera();
		initMap();
		atlas = Loader.createAtlas(TILES_IMG, 8, 8);

		blocks = new Block[w][h];
		for(int x = 0; x < blocks.length; x++) {
			for(int y = 0; y < blocks[x].length; y++) {
				blocks[x][y] = new Block(this, x * BLOCK_SIZE, y * BLOCK_SIZE,
						0);
			}
		}

		(sel = new BlockSelectionDialog(this)).setVisible(true);
		(tod = new ToolOptionDialog()).setVisible(true);
		DisplayFrame.lvl = this;
	}

	public int getSelectedID() {
		JTable table = sel.getTable();
		int srow = table.getSelectedRow();
		int scol = table.getSelectedColumn();
		if(srow == -1 || scol == -1)
			return -1;
		return srow * 4 + scol;
	}

	private void initMap() {
		TileInfo.loadFromFile(TILES_CONFIG);
	}

	public void render(Graphics2D g) {
		g.translate(cam.getX(), cam.getY());
		for(Block[] column : blocks) {
			for(Block b : column) {
				if(b == null)
					continue;
				
				Rectangle bounds = new Rectangle(b.getX(), b.getY(), BLOCK_SIZE,
						BLOCK_SIZE);
				Rectangle cambounds = new Rectangle((int) -cam.getX(),
						(int) -cam.getY(), DisplayFrame.getWidth(),
						DisplayFrame.getHeight());
				if(bounds.intersects(cambounds))
					b.render(g);
			}
		}
		g.translate(-cam.getX(), -cam.getY());
	}

	public BufferedImage imgAt(int[] textureCoords) {
		return atlas[textureCoords[0]][textureCoords[1]];
	}

	public BufferedImage[][] getAtlas() {
		return atlas;
	}

	public Block[][] getBlocks() {
		return blocks;
	}

	public Camera getCam() {
		return cam;
	}

	public static String getTilesImg() {
		return TILES_IMG;
	}

	public static String getTilesConfig() {
		return TILES_CONFIG;
	}

	public static int getBlockSize() {
		return BLOCK_SIZE;
	}

	public BlockSelectionDialog getSel() {
		return sel;
	}

	public ToolOptionDialog getTod() {
		return tod;
	}
}

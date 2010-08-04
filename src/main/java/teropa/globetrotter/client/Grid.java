package teropa.globetrotter.client;

import java.util.ArrayList;
import java.util.List;

import teropa.globetrotter.client.common.Rectangle;
import teropa.globetrotter.client.common.Size;

public class Grid {

	private final Size fullSize;
	private final int tileWidth;
	private final int tileHeight;
	
	private final int numCols;
	private final int numRows;
	private final int[] tileXs;
	private final int[] tileYs;
	
	public Grid(Size tileSize, Size fullSize) {
		this.fullSize = fullSize;
		this.tileWidth = tileSize.getWidth();
		this.tileHeight = tileSize.getHeight();
		
		numCols = fullSize.getWidth() / tileWidth;
		numRows = fullSize.getHeight() / tileHeight;
		
		tileXs = initTileXs();
		tileYs = initTileYs();
	}

	public int getNumCols() {
		return numCols;
	}

	public int getNumRows() {
		return numRows;
	}

	private int[] initTileXs() {
		int[] res = new int[numCols];
		for (int i=0 ; i<numCols ; i++) {
			res[i] = i * tileWidth;
		}
		return res;
	}

	private int[] initTileYs() {
		int[] res = new int[numRows];
		for (int i=0 ; i<numRows ; i++) {
			res[i] = i * tileHeight;
		}
		return res;
	}
	
	public List<Tile> getTiles(Rectangle area) {
		final List<Tile> result = new ArrayList<Tile>();
		
		
		int xIdx = 0;
		while (xIdx < tileXs.length && tileXs[xIdx] < area.x)
			xIdx++;
		if (xIdx > 0) xIdx--;
		
		int yIdx = 0;
		while (yIdx < tileYs.length && tileYs[yIdx] < area.y)
			yIdx++;
		if (yIdx > 0) yIdx--;
		
		
		while (xIdx < tileXs.length && tileXs[xIdx] < area.x + area.width) {
			int innerYIdx = yIdx;
			while (innerYIdx < tileYs.length && tileYs[yIdx] < area.y + area.height) {
				int x = tileXs[xIdx];
				int y = tileYs[innerYIdx];
				int width = Math.min(tileWidth, fullSize.getWidth() - x);
				int height = Math.min(tileHeight, fullSize.getHeight() - y);
				
				Tile tile = new Tile(new Rectangle(x, y, width, height), xIdx, innerYIdx);
					result.add(tile);
				innerYIdx++;
			}
			xIdx++;
		}
		return result;
	}

	public class Tile {
		
		private final Rectangle rect;
		private final int col;
		private final int row;
		
		public Tile(Rectangle rect, int col, int row) {
			this.rect = rect;
			this.col = col;
			this.row = row;
		}
		
		public Rectangle getRect() {
			return rect;
		}
		
		public int getCol() {
			return col;
		}

		public int getRow() {
			return row;
		}
		
	}
	
}

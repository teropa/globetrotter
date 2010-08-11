package teropa.globetrotter.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import teropa.globetrotter.client.common.Rectangle;
import teropa.globetrotter.client.common.Size;
import teropa.globetrotter.client.event.internal.ViewPanEndEvent;
import teropa.globetrotter.client.event.internal.ViewPanEvent;
import teropa.globetrotter.client.event.internal.ViewPanHandler;
import teropa.globetrotter.client.event.internal.ViewPanStartEvent;

public class Grid implements ViewPanHandler {

	private final Size fullSize;
	private final int tileWidth;
	private final int tileHeight;
	private final ViewContext ctx;
	
	private final int numCols;
	private final int numRows;
	private final int[] tileXs;
	private final int[] tileYs;

	private int[] coords;
	
	public Grid(Size tileSize, Size fullSize, ViewContext ctx) {
		this.fullSize = fullSize;
		this.tileWidth = tileSize.getWidth();
		this.tileHeight = tileSize.getHeight();
		this.ctx = ctx;
		
		numCols = fullSize.getWidth() / tileWidth;
		numRows = fullSize.getHeight() / tileHeight;
		
		tileXs = initTileXs();
		tileYs = initTileYs();
		
		ctx.getView().addViewPanHandler(this);
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

	public void init() {
		coords = getVisibleCoords(ctx.getVisibleRectangle());
		Set<Tile> tiles = new HashSet<Grid.Tile>();
		for (int xIdx = coords[0] ; xIdx <= coords[1] ; xIdx++) {
			for (int yIdx = coords[2] ; yIdx <= coords[3] ; yIdx++) {
				tiles.add(makeTile(xIdx, yIdx));
			}
		}
		notifyNewTiles(tiles);
	}

	public void onViewPanStarted(ViewPanStartEvent event) {
		coords = getVisibleCoords(ctx.getVisibleRectangle());
	}
	
	public void onViewPanned(ViewPanEvent event) {
		final int[] newCoords = getVisibleCoords(ctx.getVisibleRectangle());
		final Set<Tile> newTiles = new HashSet<Tile>();		
		for (int xIdx = coords[0] - 1 ; xIdx >= newCoords[0] ; xIdx--) {
			for (int yIdx = newCoords[2] ; yIdx <= newCoords[3] ; yIdx++) {
				newTiles.add(makeTile(xIdx, yIdx));
			}
		}
		for (int xIdx = coords[1] + 1 ; xIdx <= newCoords[1] ; xIdx++) {
			for (int yIdx = newCoords[2] ; yIdx <= newCoords[3] ; yIdx++) {
				newTiles.add(makeTile(xIdx, yIdx));
			}
		}
		for (int yIdx = coords[2] - 1 ; yIdx >= newCoords[2] ; yIdx--) {
			for (int xIdx = newCoords[0] ; xIdx <= newCoords[1] ; xIdx++) {
				newTiles.add(makeTile(xIdx, yIdx));
			}
		}
		for (int yIdx = coords[3] + 1 ; yIdx <= newCoords[3] ; yIdx++) {
			for (int xIdx = newCoords[0] ; xIdx <= newCoords[1] ; xIdx++) {
				newTiles.add(makeTile(xIdx, yIdx));
			}
		}
		notifyNewTiles(newTiles);
		coords = newCoords;
	}

	private void notifyNewTiles(final Set<Tile> newTiles) {
		for (Layer each : ctx.getLayers()) {
			each.addTiles(newTiles);
		}
	}

	public void onViewPanEnded(ViewPanEndEvent event) {
	}
	
	private Tile makeTile(int xIdx, int yIdx) {
		int x = tileXs[xIdx];
		int y = tileYs[yIdx];
		return new Tile(xIdx, yIdx, x, y);
	}
	

	private int[] getVisibleCoords(Rectangle area) {
		int xStart = 0;
		while (xStart < tileXs.length && tileXs[xStart] < area.x)
			xStart++;
		if (xStart > 0) xStart--;

		int xEnd = xStart;
		while (xEnd < tileXs.length && tileXs[xEnd] < area.x + area.width)
			xEnd++;
		if (xEnd > xStart) xEnd--;
		
		int yStart = 0;
		while (yStart < tileYs.length && tileYs[yStart] < area.y)
			yStart++;
		if (yStart > 0) yStart--;

		int yEnd = yStart;
		while (yEnd < tileYs.length && tileYs[yEnd] < area.y + area.height)
			yEnd++;
		if (yEnd > yStart) yEnd--;
		
		return new int[] { xStart, xEnd, yStart, yEnd };
	}
	
	public class Tile {
		
		private final int col;
		private final int row;
		private final int leftX;
		private final int topY;
		
		public Tile(int col, int row, int leftX, int topY) {
			this.col = col;
			this.row = row;
			this.leftX = leftX;
			this.topY = topY;
		}
		
		public int getCol() {
			return col;
		}

		public int getRow() {
			return row;
		}
		
		public int getLeftX() {
			return leftX;
		}
		
		public int getTopY() {
			return topY;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			Tile o = (Tile)obj;
			return col == o.col && row == o.row;
		}
		
		@Override
		public int hashCode() {
			int hash = 42;
			hash = 37 * hash + col;
			hash = 37 * hash + row;
			return hash;
		}
		
		@Override
		public String toString() {
			return "Tile ["+col+","+row+"]";
		}
	}

	
}

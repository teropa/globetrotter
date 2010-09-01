package teropa.globetrotter.client.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import teropa.globetrotter.client.Layer;
import teropa.globetrotter.client.Map;
import teropa.globetrotter.client.common.Direction;
import teropa.globetrotter.client.common.Rectangle;
import teropa.globetrotter.client.common.Size;
import teropa.globetrotter.client.event.internal.ViewPanEvent;

import com.google.gwt.event.shared.HandlerRegistration;

public class Grid implements ViewPanEvent.Handler {

	private static final int BUFFER = 2;

	private final int tileWidth;
	private final int tileHeight;
	public final Map map;
	private final HandlerRegistration panRegistration;

	private Size fullSize;
	private int numCols;
	private int numRows;
	private int[] tileXs;
	private int[] tileYs;

	private int[] coords;
	
	public Grid(Size tileSize, Map map) {
		this.tileWidth = tileSize.getWidth();
		this.tileHeight = tileSize.getHeight();
		this.map = map;		
		this.panRegistration = map.getView().addViewPanHandler(this);
	}

	public int getNumCols() {
		return numCols;
	}

	public int getNumRows() {
		return numRows;
	}

	public void init(Size fullSize) {
		this.fullSize = fullSize;
		notifyAllTilesRemoved();
		
		numCols = fullSize.getWidth() / tileWidth;
		numRows = fullSize.getHeight() / tileHeight;
		
		tileXs = initTileXs();
		tileYs = initTileYs();

		coords = getVisibleCoords(map.getVisibleRectangle());

		notifyNewTiles(getTileSpiral());
	}

	// Following adopted from http://trac.openlayers.org/browser/trunk/openlayers/lib/OpenLayers/Layer/Grid.js
	private List<Tile> getTileSpiral() {
		List<Tile> spiral = new ArrayList<Tile>();
		
		int xFrom = coords[0], xTo = coords[1], yFrom = coords[2], yTo = coords[3];
		
		int row = yFrom;
		int col = xFrom - 1;
		int directionsTried = 0;
		Direction dir = Direction.RIGHT;
		
		while (directionsTried < 4) {
			int testRow = row;
			int testCol = col;
			
			switch (dir) {
			case RIGHT: testCol++; break;
			case DOWN: testRow++; break;
			case LEFT: testCol--; break;
			case UP: testRow--; break;
			}

			Tile tile = null;
			if (testRow <= yTo && testRow >= yFrom && testCol <= xTo && testCol >= xFrom) {
				tile = makeTile(testCol, testRow);
			}
			
			if (tile != null && !spiral.contains(tile)) {
				spiral.add(0, tile);
				directionsTried = 0;
				row = testRow;
				col = testCol;
			} else {
				dir = dir.nextClockwise();
				directionsTried++;
			}
		}
		return spiral;
	}
	
	public void destroy() {
		panRegistration.removeHandler();
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

	public int getTileWidth() {
		return tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public void onViewPanned(ViewPanEvent event) {
		final int[] newCoords = getVisibleCoords(map.getVisibleRectangle());
		if (Arrays.equals(newCoords, coords)) {
			return;
		}
		
		final int oldXFrom = coords[0], oldXTo = coords[1], oldYFrom = coords[2], oldYTo = coords[3];
		final int newXFrom = newCoords[0], newXTo = newCoords[1], newYFrom = newCoords[2], newYTo = newCoords[3];
		
		final Set<Tile> removedTiles = new HashSet<Tile>();
		removedTiles.addAll(makeTiles(oldXFrom, newXFrom - 1, oldYFrom, oldYTo));
		removedTiles.addAll(makeTiles(newXTo + 1, oldXTo, oldYFrom, oldYTo));
		removedTiles.addAll(makeTiles(oldXFrom, oldXTo, oldYFrom, newYFrom - 1));
		removedTiles.addAll(makeTiles(oldXFrom, oldXTo, newYTo + 1, oldYTo));
		notifyRemovedTiles(removedTiles);
		
		final Set<Tile> newTiles = new HashSet<Tile>();
		newTiles.addAll(makeTiles(newXFrom, oldXFrom - 1, newYFrom, newYTo));
		newTiles.addAll(makeTiles(oldXTo + 1, newXTo, newYFrom, newYTo));
		newTiles.addAll(makeTiles(newXFrom, newXTo, newYFrom, oldYFrom - 1));
		newTiles.addAll(makeTiles(newXFrom, newXTo, oldYTo + 1, newYTo));
		notifyNewTiles(newTiles);

		coords = newCoords;
	}

	private void notifyRemovedTiles(final Set<Tile> removedTiles) {
		List<Layer> layers = map.getLayers();
		int sz = layers.size();
		for (int i=0 ; i<sz ; i++) {
			layers.get(i).onTilesDeactivated(removedTiles);
		}
	}

	private void notifyAllTilesRemoved() {
		List<Layer> layers = map.getLayers();
		int sz = layers.size();
		for (int i=0 ; i<sz ; i++) {
			layers.get(i).onAllTilesDeactivated();
		}
	}

	private void notifyNewTiles(final Collection<Tile> newTiles) {
		List<Layer> layers = map.getLayers();
		int sz = layers.size();
		for (int i=0 ; i<sz ; i++) {
			layers.get(i).onTilesActivated(newTiles);
		}
	}

	private List<Tile> makeTiles(int fromX, int toX, int fromY, int toY) {
		List<Tile> result = new ArrayList<Tile>();
		for (int xIdx = fromX ; xIdx <= toX ; xIdx++) {
			for (int yIdx = fromY ; yIdx <= toY ; yIdx++) {
				result.add(makeTile(xIdx, yIdx));
			}
		}
		return result;
	}
	
	private Tile makeTile(int xIdx, int yIdx) {
		int x = getTileX(xIdx);
		int wrappedX = x % fullSize.getWidth();
		int y = tileYs[yIdx];
		return new Tile(getIndexWrapping(tileXs, xIdx % tileXs.length), yIdx, x, y, wrappedX);
	}

	private int getTileX(int xIdx) {
		if (xIdx < 0) {
			int idx = -xIdx;
			return -fullSize.getWidth() * (idx / tileXs.length) - getWrapping(tileXs, idx % tileXs.length);
		} else {
			return fullSize.getWidth() * (xIdx / tileXs.length) + getWrapping(tileXs, xIdx % tileXs.length);
		}
	}
	

	private int[] getVisibleCoords(Rectangle area) {
		int xStart = getVisibleStartX(area);
		int xEnd = getVisibleEndX(area, xStart);
		int yStart = getVisibleStartY(area);
		int yEnd = getVisibleEndY(area, yStart);
		return new int[] { xStart, xEnd, yStart, yEnd };
	}

	private int getVisibleStartX(Rectangle area) {
		int xStart = 0;
		if (map.shouldWrapDateLine()) {
			xStart = ((area.x - fullSize.getWidth()) / fullSize.getWidth()) * tileXs.length;
			while (getTileX(xStart) < area.x)
				xStart++;
			xStart -= BUFFER + 1;			
		} else {
			while (xStart < tileXs.length && tileXs[xStart] < area.x)
				xStart++;
			if (xStart > 0) xStart--;
			xStart = Math.max(0, xStart - BUFFER);
		}
		return xStart;
	}

	private int getVisibleEndX(Rectangle area, int xStart) {
		int xEnd = xStart;
		if (map.shouldWrapDateLine()) {
			while (getTileX(xEnd) < area.x + area.width)
				xEnd++;
			if (xEnd > xStart) xEnd--;
			xEnd += BUFFER;
		} else {
			while (xEnd < tileXs.length && tileXs[xEnd] < area.x + area.width)
				xEnd++;
			if (xEnd > xStart) xEnd--;
			xEnd = Math.min(numCols - 1, xEnd + BUFFER);
		}
		return xEnd;
	}

	private int getVisibleStartY(Rectangle area) {
		int yStart = 0;
		while (yStart < tileYs.length && tileYs[yStart] < area.y)
			yStart++;
		if (yStart > 0) yStart--;
		yStart = Math.max(0, yStart - BUFFER);
		return yStart;
	}

	private int getVisibleEndY(Rectangle area, int yStart) {
		int yEnd = yStart;
		while (yEnd < tileYs.length && tileYs[yEnd] < area.y + area.height)
			yEnd++;
		if (yEnd > yStart) yEnd--;
		yEnd = Math.min(numRows - 1, yEnd + BUFFER);
		return yEnd;
	}
	
	private int getWrapping(int[] arr, int idx) {
		return arr[getIndexWrapping(arr, idx)];
	}
	
	private int getIndexWrapping(int[] arr, int idx) {
		return idx < 0 ? arr.length + idx : idx;
	}
}

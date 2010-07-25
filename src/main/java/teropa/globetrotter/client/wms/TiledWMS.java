package teropa.globetrotter.client.wms;

import java.util.List;

import teropa.globetrotter.client.Grid;
import teropa.globetrotter.client.ImagePool;
import teropa.globetrotter.client.common.Bounds;
import teropa.globetrotter.client.common.Calc;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Size;
import teropa.globetrotter.client.event.MapViewChangedEvent;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class TiledWMS extends WMSBase {

	private static class TileAndImage {

		public TileAndImage(Grid.Tile tile, Image image) {
			this.tile = tile;
			this.image = image;
		}

		public final Grid.Tile tile;
		public final Image image;
		
	}

	private final AbsolutePanel container = new AbsolutePanel();
	private TileAndImage[][] imageGrid;	
	private int buffer = 2;
	private double ignoreEventsBuffer = buffer / 4.0;
	private Point lastDrawnAtPoint = null;
	
	public TiledWMS(String name, String url) {
		super(name, url);
	}

	protected void onVisibilityChanged() {
		if (visible && initialized && context.isDrawn()) {
			maybeInitGrid();
			addNewTiles();
		}
	}
	
	@Override
	public void onMapViewChanged(MapViewChangedEvent evt) {
		if (evt.effectiveExtentChanged) {
			repositionTiles();
		}
		if (evt.zoomed) {
			removeTiles(true);
			imageGrid = null;
			lastDrawnAtPoint = null;
		}
		if ((evt.zoomed || evt.panned) && visible) {
			maybeInitGrid();
			if (shouldDraw()) {
				addNewTiles();
			}			
		}
		if (evt.panEnded) {
			removeTiles(false);
		}
	}
	
	private void maybeInitGrid() {
		if (imageGrid == null) {
			imageGrid = new TileAndImage[context.getGrid().getNumCols()][context.getGrid().getNumRows()];
		}
	}

	private boolean shouldDraw() {
		Point newCenter = context.getViewCenterPoint();
		if (lastDrawnAtPoint == null || distanceExceedsBuffer(newCenter, lastDrawnAtPoint)) {
			lastDrawnAtPoint = newCenter;
			return true;
		}
		return false;
	}

	private boolean distanceExceedsBuffer(Point lhs, Point rhs) {
		int xDist = Math.abs(lhs.getX() - rhs.getX());
		if (xDist > ignoreEventsBuffer * context.getTileSize().getWidth()) return true;
		int yDist = Math.abs(lhs.getY() - rhs.getY());
		if (yDist > ignoreEventsBuffer * context.getTileSize().getHeight()) return true;
		return false;
	}

	private void removeTiles(boolean removeAll) {
		if (imageGrid == null) return;
		
		Bounds bufferedExtent = widenToBuffer(context.getVisibleExtent());
		for (int i=0 ; i<imageGrid.length ; i++) {
			for (int j=0 ; j<imageGrid[i].length ; j++) {
				TileAndImage entry = imageGrid[i][j];
				if (entry == null) continue;
				if (removeAll || !Calc.intersect(bufferedExtent, entry.tile.getExtent())) {
					container.remove(entry.image);
					ImagePool.release(entry.image);
					imageGrid[i][j] = null;
				}
			}
		}
	}

	private void addNewTiles() {
		Grid grid = context.getGrid();
		Bounds extent = widenToBuffer(context.getVisibleExtent());
		List<Grid.Tile> tiles = grid.getTiles(extent);
		int length = tiles.size();
		for (int i=0 ; i<length ; i++) {
			Grid.Tile eachTile = tiles.get(i);
			TileAndImage[] col = imageGrid[eachTile.getCol()];
			if (col[eachTile.getRow()] == null) {
				Image image = ImagePool.get();
				image.setUrl(constructUrl(eachTile.getExtent(), eachTile.getSize()));
				col[eachTile.getRow()] = new TileAndImage(eachTile, image);
				container.add(image);
				Point topLeft = eachTile.getTopLeft();
				fastSetElementPosition(image.getElement(), topLeft.getX(), topLeft.getY());				
			}
		}
	}

	private Bounds widenToBuffer(Bounds extent) {
		if (buffer > 0) {
			Size viewportSize = context.getViewportSize();
			Size widenedSize = new Size(viewportSize.getWidth() + 2 * buffer * context.getTileSize().getWidth(), viewportSize.getHeight() + 2 * buffer * context.getTileSize().getHeight());
			Bounds widenedExtent = Calc.getExtent(context.getCenter(), context.getResolution(), widenedSize);
			return Calc.narrow(widenedExtent, context.getEffectiveExtent());
		} else {
			return extent;
		}
	}

	private void repositionTiles() {
		if (imageGrid == null) return;
		
		for (int i=0 ; i<imageGrid.length ; i++) {
			for (int j=0 ; j<imageGrid[i].length ; j++) {
				TileAndImage entry = imageGrid[i][j];
				if (entry != null) {
					Point topLeft = entry.tile.getTopLeft();
					fastSetElementPosition(entry.image.getElement(), topLeft.getX(), topLeft.getY());
				}
			}
		}
	}

	private void fastSetElementPosition(Element elem, int left, int top) {
		Style style = elem.getStyle();
		style.setProperty("position", "absolute");
		style.setPropertyPx("left", left);
		style.setPropertyPx("top", top);
	}

	@Override
	public Widget asWidget() {
		return container;
	}
}

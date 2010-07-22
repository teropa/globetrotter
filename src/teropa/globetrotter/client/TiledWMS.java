package teropa.globetrotter.client;

import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;

public class TiledWMS extends WMSBase {

	private static class TileAndImage {

		public TileAndImage(Grid.Tile tile, Image image) {
			this.tile = tile;
			this.image = image;
		}

		public Grid.Tile tile;
		public Image image;

	}

	private final AbsolutePanel container = new AbsolutePanel();
	private TileAndImage[][] imageGrid;	
	private int buffer = 2;
	private double ignoreEventsBuffer = buffer / 4.0;
	private Point lastDrawnAtPoint = null;
	
	public TiledWMS(Map map, String name, String url) {
		super(map, name, url);
		initWidget(container);
	}

	protected void onVisibilityChanged() {
		if (visible) {
			maybeInitGrid();
			addNewTiles();
		}
	}
	
	public void onMapZoomed(ViewZoomedEvent evt) {
		removeTiles(true);
		imageGrid = null;
		lastDrawnAtPoint = null;
	}
	
	public void onMapPanned(ViewPannedEvent evt) {
		if (!visible) return;
		
		maybeInitGrid();
		if (shouldDraw(evt)) {
			addNewTiles();
		}
	}

	private void maybeInitGrid() {
		if (imageGrid == null) {
			imageGrid = new TileAndImage[map.getCurrentGrid().getNumCols()][map.getCurrentGrid().getNumRows()];
		}
	}

	private boolean shouldDraw(ViewPannedEvent evt) {
		Point newCenter = evt.newCenterPoint;
		if (lastDrawnAtPoint == null || distanceExceedsBuffer(newCenter, lastDrawnAtPoint)) {
			lastDrawnAtPoint = newCenter;
			return true;
		}
		return false;
	}

	private boolean distanceExceedsBuffer(Point lhs, Point rhs) {
		int xDist = Math.abs(lhs.getX() - rhs.getX());
		if (xDist > ignoreEventsBuffer * map.getTileSize().getWidth()) return true;
		int yDist = Math.abs(lhs.getY() - rhs.getY());
		if (yDist > ignoreEventsBuffer * map.getTileSize().getHeight()) return true;
		return false;
	}

	public void onMapPanEnded(ViewPanEndedEvent evt) {
		removeTiles(false);
	}

	private void removeTiles(boolean removeAll) {
		if (imageGrid == null) return;
		
		Bounds bufferedExtent = widenToBuffer(map.getExtent());
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
		Grid grid = map.getCurrentGrid();
		Bounds extent = widenToBuffer(map.getExtent());
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
				fastSetElementPosition(image.getElement(), eachTile.getTopLeft().getX(), eachTile.getTopLeft().getY());				
			}
		}
	}

	private Bounds widenToBuffer(Bounds extent) {
		if (buffer > 0) {
			Size viewportSize = map.getViewportSize();
			Size widenedSize = new Size(viewportSize.getWidth() + 2 * buffer * map.getTileSize().getWidth(), viewportSize.getHeight() + 2 * buffer * map.getTileSize().getHeight());
			Bounds widenedExtent = Calc.getExtent(map.getCenter(), map.getResolution(), widenedSize);
			return Calc.narrow(widenedExtent, map.getMaxExtent());
		} else {
			return extent;
		}
	}

	private void fastSetElementPosition(Element elem, int left, int top) {
		elem.getStyle().setProperty("position", "absolute");
		elem.getStyle().setPropertyPx("left", left);
		elem.getStyle().setPropertyPx("top", top);
	}

}

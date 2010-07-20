package teropa.globetrotter.client;

import java.util.ArrayList;
import java.util.List;

public class Grid {

	private final Size viewSize;
	private final Size tileSize;
	private final Bounds maxExtent;
	private final double resolution;
	private final List<Tile> grid = new ArrayList<Tile>();
	
	public Grid(Size viewSize, Size tileSize, Bounds maxExtent, double resolution) {
		this.viewSize = viewSize;
		this.tileSize = tileSize;
		this.maxExtent = maxExtent;
		this.resolution = resolution;
		for (int x=0 ; x < viewSize.getWidth() ; x += tileSize.getWidth()) {
			for (int y=0 ; y < viewSize.getHeight() ; y += tileSize.getHeight()) {
				final Point topLeft = new Point(x, y);
				final Size thisTileSize = new Size(getTileWidth(x), getTileHeight(y));
				final LonLat centerLonLat = Calc.getLonLat(Calc.getCenterPoint(topLeft, thisTileSize), maxExtent, viewSize);
				final Bounds tileBounds = Calc.getExtent(centerLonLat, resolution, thisTileSize);
				grid.add(new Tile(tileBounds, thisTileSize, topLeft));
			}
		}
	}
	
	public List<Tile> getTiles(Bounds extent) {
		final List<Tile> result = new ArrayList<Tile>();
		for (Tile each : grid) {
			if (Calc.intersect(extent, each.getExtent())) {
				result.add(each);
			}
		}
		return result;
	}

	private int getTileWidth(int x) {
		return (x + tileSize.getWidth() > viewSize.getWidth()) ? viewSize.getWidth() - x : tileSize.getWidth();
	}

	private int getTileHeight(int y) {
		return (y + tileSize.getHeight() > viewSize.getHeight()) ? viewSize.getHeight() - y : tileSize.getHeight();
	}

	public static class Tile {
		
		private final Bounds extent;
		private final Size size;
		private final Point topLeft;
		
		public Tile(Bounds extent, Size size, Point topLeft) {
			this.extent = extent;
			this.size = size;
			this.topLeft = topLeft;
		}
		
		public Bounds getExtent() {
			return extent;
		}
		
		public Size getSize() {
			return size;
		}
		
		public Point getTopLeft() {
			return topLeft;
		}
		
	}
	
}

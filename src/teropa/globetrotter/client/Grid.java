package teropa.globetrotter.client;

import java.util.HashSet;
import java.util.Set;

public class Grid {

	private final Size viewSize;
	private final Size tileSize;
	private final Bounds maxExtent;
	private final double resolution;
	private final double tileCoordWidth;
	private final double tileCoordHeight;
	
	private final double[] tileXs;
	private final double[] tileYs;
	private final Tile[][] tileCache;
	
	public Grid(Size viewSize, Size tileSize, Bounds maxExtent, double resolution) {
		this.viewSize = viewSize;
		this.tileSize = tileSize;
		this.maxExtent = maxExtent;
		this.resolution = resolution;
		this.tileCoordWidth = Calc.getCoordinateWidth(tileSize, resolution);
		this.tileCoordHeight = Calc.getCoordinateHeight(tileSize, resolution);
		
		tileXs = initTileXs(maxExtent);
		tileYs = initTileYs(maxExtent);
		tileCache = new Tile[tileXs.length][tileYs.length];
	}

	private double[] initTileXs(Bounds maxExtent) {
		double[] res = new double[(int)(maxExtent.getWidth() / tileCoordWidth) + 1];
		int idx = 0;
		for (double x = maxExtent.getLowerLeftX() ; x < maxExtent.getUpperRightX() ; x += tileCoordWidth) {
			res[idx++] = x;
		}
		return res;
	}

	private double[] initTileYs(Bounds maxExtent) {
		double[] res =  new double[(int)(maxExtent.getHeight() / tileCoordHeight) + 1];
		int idx = 0;
		for (double y = maxExtent.getLowerLeftY() ; y < maxExtent.getUpperRightY() ; y += tileCoordHeight) {
			res[idx++] = y;
		}
		return res;
	}

	public Set<Tile> getTiles(Bounds extent) {
		final Set<Tile> result = new HashSet<Tile>();
		
		int xIdx = 0;
		while (tileXs[xIdx] < extent.getLowerLeftX())
			xIdx++;

		int yIdx = 0;
		while (tileYs[yIdx] < extent.getLowerLeftY())
			yIdx++;
		
		while (xIdx < tileXs.length && tileXs[xIdx] < extent.getUpperRightX()) {
			int innerYIdx = yIdx;
			while (innerYIdx < tileYs.length && tileYs[innerYIdx] < extent.getUpperRightY()) {
				Tile cached = tileCache[xIdx][innerYIdx];
				if (cached != null) {
					result.add(cached);
				} else {
					double lowerLeftX = tileXs[xIdx];
					double lowerLeftY = tileYs[innerYIdx];
					double upperRightX = Math.min(lowerLeftX + tileCoordWidth, maxExtent.getUpperRightX());
					double upperRightY = Math.min(lowerLeftY + tileCoordHeight, maxExtent.getUpperRightY());
					Bounds tileBounds = new Bounds(lowerLeftX, lowerLeftY, upperRightX, upperRightY);
					Size tileSize = this.tileSize;
					if (upperRightX >= maxExtent.getUpperRightX() || upperRightY >= maxExtent.getUpperRightY()) {
						tileSize =  Calc.getPixelSize(tileBounds, resolution);
					}
					Point topLeft = Calc.getPoint(new LonLat(lowerLeftX, upperRightY), maxExtent, viewSize);
					Tile tile = new Tile(tileBounds, tileSize, topLeft);
					result.add(tile);
					tileCache[xIdx][innerYIdx] = tile;
				}
				innerYIdx++;
			}
			xIdx++;
		}
		return result;
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

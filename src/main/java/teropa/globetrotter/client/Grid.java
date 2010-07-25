package teropa.globetrotter.client;

import java.util.ArrayList;
import java.util.List;

import teropa.globetrotter.client.common.Bounds;
import teropa.globetrotter.client.common.Calc;
import teropa.globetrotter.client.common.LonLat;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Size;

public class Grid {

	private final ViewContext viewContext;
	private final Size tileSize;
	private final Bounds maxExtent;
	private Bounds effectiveExtent;
	private final double resolution;
	private final double tileCoordWidth;
	private final double tileCoordHeight;
	
	private final double[] tileXs;
	private final double[] tileYs;
	private final Tile[][] tileCache;
	
	public Grid(ViewContext ctx, Size tileSize, Bounds maxExtent, Bounds effectiveExtent, double resolution) {
		this.viewContext = ctx;
		this.tileSize = tileSize;
		this.maxExtent = maxExtent;
		this.effectiveExtent = effectiveExtent;
		this.resolution = resolution;
		this.tileCoordWidth = Calc.getCoordinateWidth(tileSize, resolution);
		this.tileCoordHeight = Calc.getCoordinateHeight(tileSize, resolution);
		
		tileXs = initTileXs(maxExtent);
		tileYs = initTileYs(maxExtent);
		tileCache = new Tile[tileXs.length][tileYs.length];
	}

	public int getNumCols() {
		return tileXs.length;
	}

	public int getNumRows() {
		return tileYs.length;
	}
	
	public void setEffectiveExtent(Bounds effectiveExtent) {
		this.effectiveExtent = effectiveExtent;
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

	public List<Tile> getTiles(Bounds extent) {
		final List<Tile> result = new ArrayList<Tile>();
		
		int xIdx = 0;
		while (xIdx < tileXs.length && tileXs[xIdx] < extent.getLowerLeftX())
			xIdx++;
		if (xIdx > 0) xIdx--;

		int yIdx = 0;
		while (yIdx < tileYs.length && tileYs[yIdx] < extent.getLowerLeftY())
			yIdx++;
		if (yIdx > 0) yIdx--;
		
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
					Tile tile = new Tile(tileBounds, xIdx, innerYIdx);
					result.add(tile);
					tileCache[xIdx][innerYIdx] = tile;
				}
				innerYIdx++;
			}
			xIdx++;
		}
		return result;
	}

	public class Tile {
		
		private final Bounds extent;
		private final int col;
		private final int row;
		
		public Tile(Bounds extent, int col, int row) {
			this.extent = extent;
			this.col = col;
			this.row = row;
		}
		
		public Bounds getExtent() {
			return extent;
		}
		
		public Size getSize() {
			return Calc.getPixelSize(extent, resolution);
		}
		
		public Point getTopLeft() {
			return Calc.getPoint(new LonLat(extent.getLowerLeftX(), extent.getUpperRightY()), effectiveExtent, viewContext.getViewSize());
		}

		public int getCol() {
			return col;
		}

		public int getRow() {
			return row;
		}
		
	}
	
}

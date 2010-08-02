package teropa.globetrotter.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;

import teropa.globetrotter.client.common.Bounds;
import teropa.globetrotter.client.common.Calc;
import teropa.globetrotter.client.common.LonLat;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Size;

public class Grid {

	private final ViewContext viewContext;
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
		this.maxExtent = maxExtent;
		this.effectiveExtent = effectiveExtent;
		this.resolution = resolution;
		this.tileCoordWidth = Calc.getUnitWidth(tileSize, resolution);
		this.tileCoordHeight = Calc.getUnitHeight(tileSize, resolution);
		
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
		double[] res = new double[(int)Math.round(maxExtent.getWidth() / tileCoordWidth)];
		GWT.log("initialized xs: "+res.length);
		for (int i=0 ; i<res.length ; i++) {
			res[i] = maxExtent.getLowerLeftX() + i * tileCoordWidth * viewContext.getProjection().leftToRight();
		}
		return res;
	}

	private double[] initTileYs(Bounds maxExtent) {
		double[] res =  new double[(int)Math.round(maxExtent.getHeight() / tileCoordHeight)];
		GWT.log("initialized y	s: "+res.length);
		for (int i=0 ; i<res.length ; i++) {
			res[i] = maxExtent.getLowerLeftY() - i * tileCoordHeight * viewContext.getProjection().topToBottom();
		}
		return res;
	}

	private boolean xLeftFrom(double x, double comp) {
		if (viewContext.getProjection().leftToRight() < 0) {
			return x > comp;
		} else {
			return x < comp;
		}
	}

	private boolean yBelow(double y, double comp) {
		if (viewContext.getProjection().topToBottom() < 0) {
			return y < comp;	
		} else {
			return y > comp;
		}
		
	}
	
	public List<Tile> getTiles(Bounds extent) {
		final List<Tile> result = new ArrayList<Tile>();
		
		int xIdx = 0;
		while (xIdx < tileXs.length && xLeftFrom(tileXs[xIdx], extent.getLowerLeftX()))
			xIdx++;
		if (xIdx > 0) xIdx--;

		int yIdx = 0;
		while (yIdx < tileYs.length && yBelow(tileYs[yIdx], extent.getLowerLeftY()))
			yIdx++;
		if (yIdx > 0) yIdx--;
		
		while (xIdx < tileXs.length && xLeftFrom(tileXs[xIdx], extent.getUpperRightX())) {
			int innerYIdx = yIdx;
			while (innerYIdx < tileYs.length && yBelow(tileYs[innerYIdx], extent.getUpperRightY())) {
				Tile cached = tileCache[xIdx][innerYIdx];
				if (cached != null) {
					result.add(cached);
				} else {
					double lowerLeftX = tileXs[xIdx];
					double lowerLeftY = tileYs[innerYIdx];
					double upperRightX = lowerLeftX + (tileCoordWidth * viewContext.getProjection().leftToRight());
					if (viewContext.getProjection().leftToRight() < 0) {
						upperRightX = Math.max(upperRightX, maxExtent.getUpperRightX());
					} else {
						upperRightX = Math.min(upperRightX, maxExtent.getUpperRightX());
					}
					double upperRightY = lowerLeftY - (tileCoordHeight * viewContext.getProjection().topToBottom());
					if (viewContext.getProjection().topToBottom() < 0) {
						upperRightY = Math.min(upperRightY, maxExtent.getUpperRightY());
					} else {
						upperRightY = Math.max(upperRightY, maxExtent.getUpperRightY());
					}
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
			LonLat topLeft = new LonLat(extent.getLowerLeftX(), extent.getUpperRightY());
			return Calc.getPoint(topLeft, effectiveExtent, viewContext.getViewSize(), viewContext.getProjection());
		}

		public int getCol() {
			return col;
		}

		public int getRow() {
			return row;
		}
		
	}
	
}

package teropa.globetrotter.client;

import java.util.ArrayList;
import java.util.List;

public class Grid {

	private final Bounds extent;
	private final Size size;
	private final Size tileSize;
	
	public Grid(Bounds extent, Size size, Size tileSize) {
		this.extent = extent;
		this.size = size;
		this.tileSize = tileSize;
	}
	
	public List<Tile> getTiles(Bounds extent) {
		final List<Tile> result = new ArrayList<Tile>();
		
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

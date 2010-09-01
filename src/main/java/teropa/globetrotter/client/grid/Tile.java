package teropa.globetrotter.client.grid;

public class Tile {
	
	private final int col;
	private final int row;
	private final int leftX;
	private final int topY;
	private final int wrappedLeftX;
	
	public Tile(int col, int row, int leftX, int topY, int wrappedX) {
		this.col = col;
		this.row = row;
		this.leftX = leftX;
		this.topY = topY;
		this.wrappedLeftX = wrappedX;
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
	
	public int getWrappedLeftX() {
		return wrappedLeftX;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		Tile o = (Tile)obj;
		return leftX == o.leftX && topY == o.topY;
	}
	
	@Override
	public int hashCode() {
		int hash = 42;
		hash = 37 * hash + leftX;
		hash = 37 * hash + topY;
		return hash;
	}
	
	@Override
	public String toString() {
		return "Tile ["+col+","+row+"]["+leftX+","+topY+"]";
	}
}
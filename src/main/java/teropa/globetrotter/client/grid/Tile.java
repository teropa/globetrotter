package teropa.globetrotter.client.grid;

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
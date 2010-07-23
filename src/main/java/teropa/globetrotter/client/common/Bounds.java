package teropa.globetrotter.client.common;

public class Bounds {

	private final double lowerLeftX;
	private final double lowerLeftY;
	private final double upperRightX;
	private final double upperRightY;
	
	public Bounds(double lowerLeftX,
			double lowerLeftY,
			double upperRightX,
			double upperRightY) {
		this.lowerLeftX = lowerLeftX;
		this.lowerLeftY = lowerLeftY;
		this.upperRightX = upperRightX;
		this.upperRightY = upperRightY;
	}
	
	public double getLowerLeftX() {
		return lowerLeftX;
	}
	
	public double getLowerLeftY() {
		return lowerLeftY;
	}
	
	public double getUpperRightX() {
		return upperRightX;
	}
	
	public double getUpperRightY() {
		return upperRightY;
	}
	
	public double getWidth() {
		return Math.abs(upperRightX - lowerLeftX);
	}
	
	public double getHeight() {
		return Math.abs(upperRightY - lowerLeftY);
	}
	
	@Override
	public boolean equals(Object obj) {
		Bounds o = (Bounds)obj;
		return hashDouble(lowerLeftX) == hashDouble(o.lowerLeftX) &&
			hashDouble(lowerLeftY) == hashDouble(o.lowerLeftY) &&
			hashDouble(upperRightX) == hashDouble(o.upperRightX) &&
			hashDouble(upperRightY) == hashDouble(o.upperRightY);
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash += hashDouble(lowerLeftX) * 31;
		hash += hashDouble(lowerLeftY) * 31;
		hash += hashDouble(upperRightX) * 31;
		hash += hashDouble(upperRightY) * 31;
		return hash;
	}
	
	private int hashDouble(double val) {
		return (int)(val * 1000000);
	}
	
	@Override
	public String toString() {
		return "[" + getLowerLeftX() + "," + getLowerLeftY() + "," + getUpperRightX() + "," + getUpperRightY() + "]";
	}
}

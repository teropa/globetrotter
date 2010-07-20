package teropa.globetrotter.client;

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
	
}

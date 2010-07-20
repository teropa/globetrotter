package teropa.globetrotter.client;

public class Point {

	private final int x;
	private final int y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Point(double x, double y) {
		this.x = (int)Math.floor(x);
		this.y = (int)Math.floor(y);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	@Override
	public String toString() {
		return "[x: " + x + ", y: " + y + "]";
	}
}

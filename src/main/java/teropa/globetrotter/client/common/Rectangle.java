package teropa.globetrotter.client.common;

public class Rectangle {

	public final int x;
	public final int y;
	public final int width;
	public final int height;
	
	public Rectangle(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public String toString() {
		return "x: "+x+", y: "+y+", w: "+width+", h: "+height;
	}
	
}

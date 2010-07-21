package teropa.globetrotter.client;

public class Size {

	private final int width;
	private final int height;
	
	public Size(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	@Override
	public boolean equals(Object obj) {
		Size o = (Size)obj;
		return o.width == width && o.height == height;
	}
	
	@Override
	public int hashCode() {
		int hash = 51;
		hash += width * 31;
		hash += height * 31;
		return hash;
	}
	
	@Override
	public String toString() {
		return "[width: " + width + ", height: " + height + "]";
	}
	
}

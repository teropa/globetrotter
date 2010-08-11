package teropa.globetrotter.client;

import com.google.gwt.dom.client.ImageElement;

public class ImageAndCoords {
	
	private final ImageElement image;
	private final double x;
	private final double y;
	
	public ImageAndCoords(ImageElement image, double x, double y) {
		this.image = image;
		this.x = x;
		this.y = y;
	}
	
	public ImageElement getImage() {
		return image;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
}
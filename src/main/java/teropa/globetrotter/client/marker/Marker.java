package teropa.globetrotter.client.marker;

import teropa.globetrotter.client.Images;
import teropa.globetrotter.client.common.LonLat;
import teropa.globetrotter.client.common.Position;
import teropa.globetrotter.client.common.Size;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

public class Marker {

	private static Images DEFAULT_IMAGES = GWT.create(Images.class);
	
	private final LonLat loc;
	private final ImageResource imageResource;
	private final Size size;
	private final Position pinPosition;
	private Position popupPosition;
	
	public Marker(LonLat loc) {
		this(loc, DEFAULT_IMAGES.markerRed(), new Size(32, 32), Position.BOTTOM_LEFT);
	}
	
	public Marker(LonLat loc, ImageResource image, Size size, Position pinPosition) {
		this.loc = loc;
		this.imageResource = image;
		this.size = size;
		this.pinPosition = pinPosition;
	}

	public LonLat getLoc() {
		return loc;
	}

	public Position getPinPosition() {
		return pinPosition;
	}
	
	public Position getPopupPosition() {
		return popupPosition;
	}

	public Size getSize() {
		return size;
	}
	
	public ImageResource getImage() {
		return imageResource;
	}
}

package teropa.globetrotter.client.marker;

import teropa.globetrotter.client.Images;
import teropa.globetrotter.client.common.LonLat;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Size;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;

public class Marker {

	private static Images DEFAULT_IMAGES = GWT.create(Images.class);
	
	public static enum PinPosition {
		TOP_LEFT,
		TOP_CENTER,
		TOP_RIGHT,
		MIDDLE_LEFT,
		MIDDLE_CENTER,
		MIDDLE_RIGHT,
		BOTTOM_LEFT,
		BOTTOM_CENTER,
		BOTTOM_RIGHT
	};
	
	private final LonLat loc;
	private final Image image;
	private final Size size;
	private final PinPosition poinPosition;
	
	public Marker(LonLat loc) {
		this(loc, DEFAULT_IMAGES.markerRed().createImage(), new Size(32, 32), PinPosition.BOTTOM_LEFT);
	}
	
	public Marker(LonLat loc, Image image, Size size, PinPosition pinPosition) {
		this.loc = loc;
		this.image = image;
		this.size = size;
		this.poinPosition = pinPosition;
	}

	public LonLat getLoc() {
		return loc;
	}
	
	public Image getImage() {
		return image;
	}
	
	public Point translate(Point point) {
		switch (poinPosition) {
		case TOP_LEFT: return point;
		case TOP_CENTER: return new Point(toCenter(point), point.getY());
		case TOP_RIGHT: return new Point(toRight(point), point.getY());
		case MIDDLE_LEFT: return new Point(point.getX(), toMiddle(point));
		case MIDDLE_CENTER: return new Point(toCenter(point), toMiddle(point));
		case MIDDLE_RIGHT: return new Point(toRight(point), toMiddle(point));
		case BOTTOM_LEFT: return new Point(point.getX(), toBottom(point));
		case BOTTOM_CENTER: return new Point(toCenter(point), toBottom(point));
		case BOTTOM_RIGHT: return new Point(toRight(point), toBottom(point));
		default: return point;
		}
	}

	private int toBottom(Point point) {
		return point.getY() - size.getHeight();
	}

	private int toMiddle(Point point) {
		return point.getY() - size.getHeight() / 2;
	}

	private int toRight(Point point) {
		return point.getX() - size.getWidth();
	}

	private int toCenter(Point point) {
		return point.getX() - size.getWidth() / 2;
	}
	
}

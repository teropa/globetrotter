package teropa.globetrotter.client.marker;

import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Size;

public enum Position {
	TOP_LEFT,
	TOP_CENTER,
	TOP_RIGHT,
	MIDDLE_LEFT,
	MIDDLE_CENTER,
	MIDDLE_RIGHT,
	BOTTOM_LEFT,
	BOTTOM_CENTER,
	BOTTOM_RIGHT;
	
	public Point translateAroundPoint(Point point, Size sz) {
		switch (this) {
		case TOP_LEFT: return point;
		case TOP_CENTER: return new Point(toCenter(point, sz, 1), point.getY());
		case TOP_RIGHT: return new Point(toRight(point, sz, 1), point.getY());
		case MIDDLE_LEFT: return new Point(point.getX(), toMiddle(point, sz, 1));
		case MIDDLE_CENTER: return new Point(toCenter(point, sz, 1), toMiddle(point, sz, 1));
		case MIDDLE_RIGHT: return new Point(toRight(point, sz, 1), toMiddle(point, sz, 1));
		case BOTTOM_LEFT: return new Point(point.getX(), toBottom(point, sz, 1));
		case BOTTOM_CENTER: return new Point(toCenter(point, sz, 1), toBottom(point, sz, 1));
		case BOTTOM_RIGHT: return new Point(toRight(point, sz, 1), toBottom(point, sz, 1));
		default: return point;
		}
	}

	public Point translateAroundSize(Point point, Size sz) {
		switch (this) {
		case TOP_LEFT: return point;
		case TOP_CENTER: return new Point(toCenter(point, sz, -1), point.getY());
		case TOP_RIGHT: return new Point(toRight(point, sz, -1), point.getY());
		case MIDDLE_LEFT: return new Point(point.getX(), toMiddle(point, sz, -1));
		case MIDDLE_CENTER: return new Point(toCenter(point, sz, -1), toMiddle(point, sz, -1));
		case MIDDLE_RIGHT: return new Point(toRight(point, sz, -1), toMiddle(point, sz, -1));
		case BOTTOM_LEFT: return new Point(point.getX(), toBottom(point, sz, -1));
		case BOTTOM_CENTER: return new Point(toCenter(point, sz, -1), toBottom(point, sz, -1));
		case BOTTOM_RIGHT: return new Point(toRight(point, sz, -1), toBottom(point, sz, -1));
		default: return point;
		}
	}

	private int toBottom(Point point, Size size, int r) {
		return point.getY() - size.getHeight() * r;
	}

	private int toMiddle(Point point, Size size, int r) {
		return point.getY() - size.getHeight() / 2 * r;
	}

	private int toRight(Point point, Size size, int r) {
		return point.getX() - size.getWidth() * r;
	}

	private int toCenter(Point point, Size size, int r) {
		return point.getX() - size.getWidth() / 2 * r;
	}
	
}
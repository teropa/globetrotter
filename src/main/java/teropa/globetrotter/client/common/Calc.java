package teropa.globetrotter.client.common;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

public class Calc {

	public static Size getPixelSize(Bounds bounds, double resolution) {
		return new Size(getPixelWidth(bounds, resolution), getPixelHeight(bounds, resolution));
	}
	
	public static int getPixelWidth(Bounds bounds, double resolution) {
		return (int)round(bounds.getWidth() / resolution);		
	}
	
	public static int getPixelHeight(Bounds bounds, double resolution) {
		return (int)round(bounds.getHeight() / resolution);
	}
	
	public static double getCoordinateWidth(Size size, double resolution) {
		return size.getWidth() * resolution;
	}
	
	public static double getCoordinateHeight(Size size, double resolution) {
		return size.getHeight() * resolution;
	}
	
	public static Bounds getExtent(LonLat center, double resolution, Size viewportSize) {
		final double halfWidth = getCoordinateWidth(viewportSize, resolution) / 2.0;
		final double halfHeight = getCoordinateHeight(viewportSize, resolution) / 2.0;
		
		return new Bounds(
				center.getLon() - halfWidth,
				center.getLat() - halfHeight,
				center.getLon() + halfWidth,
				center.getLat() + halfHeight);
	}
	
	public static Bounds narrow(Bounds bounds, Bounds to) {
		double fromWidth = bounds.getWidth();
		double fromHeight = bounds.getHeight();
		double toWidth = to.getWidth();
		double toHeight = to.getHeight();
		boolean narrowWidth = fromWidth > toWidth;
		boolean narrowHeight = fromHeight > toHeight;
		
		if (narrowWidth || narrowHeight) {
			double widthDiff = narrowWidth ? (fromWidth - toWidth) / 2 : 0;
			double heightDiff = narrowHeight ? (fromHeight - toHeight) / 2 : 0;
			return new Bounds(
					bounds.getLowerLeftX() + widthDiff,
					bounds.getLowerLeftY() + heightDiff,
					bounds.getUpperRightX() - widthDiff,
					bounds.getUpperRightY() - heightDiff);
		} else {
			return bounds;
		}
	}
	
	public static LonLat getLonLat(Point point, Bounds extent, Size area) {
		double ratioFromLeft = ((double)point.getX()) / ((double)area.getWidth());
		double fromBottom = area.getHeight() - point.getY();
		double ratioFromBottom = fromBottom / ((double)area.getHeight());
		
		double lon = extent.getLowerLeftX() + ratioFromLeft * extent.getWidth();
		double lat = extent.getLowerLeftY() + ratioFromBottom * extent.getHeight();
		
		return new LonLat(lon, lat);
	}
	
	public static Point getPoint(LonLat lonLat, Bounds extent, Size area) {
		double fromLeft = lonLat.getLon() - extent.getLowerLeftX();
		double fromBottom = lonLat.getLat() - extent.getLowerLeftY();
		double fromTop = extent.getHeight() - fromBottom;
		
		double ratioFromLeft = fromLeft / extent.getWidth();
		double ratioFromTop = fromTop / extent.getHeight();
		
		int x = (int)round(ratioFromLeft * area.getWidth());
		int y = (int)round(ratioFromTop * area.getHeight());
		
		return new Point(x, y);
	}

	public static Point getCenterPoint(Point topLeft, Size areaSize) {
		return new Point(
				topLeft.getX() + areaSize.getWidth() / 2,
				topLeft.getY() + areaSize.getHeight() / 2);
	}

	public static boolean intersect(Bounds lhs, Bounds rhs) {
		return
			lhs.getLowerLeftX() < rhs.getUpperRightX() &&
			lhs.getUpperRightX() > rhs.getLowerLeftX() &&
			lhs.getLowerLeftY() < rhs.getUpperRightY() &&
			lhs.getUpperRightY() > rhs.getLowerLeftY();
	}
	
	public static Bounds getEffectiveExtent(Bounds maxExtent, double resolution, LonLat center) {
		Size vSize = getPixelSize(maxExtent, resolution);
		if (vSize.getWidth() <= 10000 && vSize.getHeight() <= 10000) {
			return maxExtent;
		}
		Size effectiveSize = new Size(min(vSize.getWidth(), 10000), min(vSize.getHeight(), 10000));
		double halfWidth = getCoordinateWidth(effectiveSize, resolution) / 2.0;
		double halfHeight = getCoordinateHeight(effectiveSize, resolution) / 2.0;

		double lowerX = max(center.getLon() - halfWidth, maxExtent.getLowerLeftX());
		double lowerY = max(center.getLat() - halfHeight, maxExtent.getLowerLeftY());
		double upperX = min(center.getLon() + halfWidth, maxExtent.getUpperRightX());
		double upperY = min(center.getLat() + halfHeight, maxExtent.getUpperRightY());
		return new Bounds(lowerX, lowerY, upperX, upperY);

	}

	public static int getDistance(Point lhs, Point rhs) {
		int distX = rhs.getX() - lhs.getX();
		int distY = rhs.getY() - lhs.getY();
		return (int)round(sqrt(pow(distX, 2) + pow(distY, 2)));
	}
	
	public static double getAngle(Point p) {
		return atan2(p.getY(), p.getX());
	}

	public static Point addToPoint(Point centerPoint, int amountPx, Direction dir) {
		switch (dir) {
		case UP: 	return new Point(centerPoint.getX(), centerPoint.getY() - amountPx);
		case RIGHT: return new Point(centerPoint.getX() + amountPx, centerPoint.getY());
		case DOWN: 	return new Point(centerPoint.getX(), centerPoint.getY() + amountPx);
		case LEFT: 	return new Point(centerPoint.getX() - amountPx, centerPoint.getY());
		default: 	return centerPoint;
		}
	}

	public static Bounds keepInBounds(Bounds extent, Bounds maxExtent) {
		double lowX = extent.getLowerLeftX() - maxExtent.getLowerLeftX();
		double lowY = extent.getLowerLeftY() - maxExtent.getLowerLeftY();
		double highX = extent.getUpperRightX() - maxExtent.getUpperRightX();
		double highY = extent.getUpperRightY() - maxExtent.getUpperRightY();
		
		double xOffset = 0;
		if (lowX < 0) xOffset = -lowX;
		else if (highX > 0) xOffset = -highX;
		
		double yOffset = 0;
		if (lowY < 0) yOffset = -lowY;
		if (highY > 0) yOffset = -highY;
		
		return new Bounds(
				extent.getLowerLeftX() + xOffset,
				extent.getLowerLeftY() + yOffset,
				extent.getUpperRightX() + xOffset,
				extent.getUpperRightY() + yOffset);
	}
	
	public static double getLonDegreeLengthMeters(LonLat at) {
		return cos(toRadians(at.getLat())) * 111325;
	}
	
}

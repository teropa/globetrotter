package teropa.globetrotter.client.common;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;
import teropa.globetrotter.client.Map;
import teropa.globetrotter.client.proj.Projection;

public class Calc {

	private final Map map;
	
	public Calc(Map map) {
		this.map = map;
	}
	
	public Size getPixelSize(Bounds bounds, double resolution) {
		return new Size(getPixelWidth(bounds, resolution), getPixelHeight(bounds, resolution));
	}
	
	public int getPixelWidth(Bounds bounds, double resolution) {
		return (int)round(bounds.getWidth() / resolution);		
	}
	
	public int getPixelHeight(Bounds bounds, double resolution) {
		return (int)round(bounds.getHeight() / resolution);
	}
	
	public double getUnitWidth(Size size, double resolution) {
		return size.getWidth() * resolution;
	}
	
	public double getUnitHeight(Size size, double resolution) {
		return size.getHeight() * resolution;
	}
	
	public Bounds getExtent(LonLat center, double resolution, Size size, Projection proj) {
		final double halfWidth = getUnitWidth(size, resolution) / 2.0;
		final double halfHeight = getUnitHeight(size, resolution) / 2.0;
		
		return new Bounds(
				center.getLon() - halfWidth * proj.leftToRight(),
				center.getLat() + halfHeight * proj.topToBottom(),
				center.getLon() + halfWidth * proj.leftToRight(),
				center.getLat() - halfHeight * proj.topToBottom());
	}
	
	public Bounds narrow(Bounds bounds, Bounds to, Projection proj) {
		double lowX, lowY, highX, highY;
		if (proj.leftToRight() < 0) {
			lowX = Math.min(bounds.getLowerLeftX(), to.getLowerLeftX());
			highX = Math.max(bounds.getUpperRightX(), to.getUpperRightX());
		} else {
			lowX = Math.max(bounds.getLowerLeftX(), to.getLowerLeftX());
			highX = Math.min(bounds.getUpperRightX(), to.getUpperRightX());
		}
		if (proj.topToBottom() < 0) {
			lowY = Math.max(bounds.getLowerLeftY(), to.getLowerLeftY());
			highY = Math.min(bounds.getUpperRightY(), to.getUpperRightY());
		} else {
			lowY = Math.min(bounds.getLowerLeftY(), to.getLowerLeftY());
			highY = Math.max(bounds.getUpperRightY(), to.getUpperRightY());
		}
		return new Bounds(lowX, lowY, highX, highY);
	}
	
	public LonLat getLonLat(Point point, Bounds extent, Size area, Projection proj) {
		double ratioFromLeft = ((double)point.getX()) / ((double)area.getWidth());
		double fromBottom = area.getHeight() - point.getY();
		double ratioFromBottom = fromBottom / ((double)area.getHeight());
		
		double lon = extent.getLowerLeftX() + ratioFromLeft * extent.getWidth() * proj.leftToRight();
		double lat = extent.getLowerLeftY() - ratioFromBottom * extent.getHeight() * proj.topToBottom();
		
		return new LonLat(lon, lat);
	}
	
	public Point getPoint(LonLat lonLat, Bounds extent, Size area, Projection proj) {
		double fromLeft = lonLat.getLon() - extent.getLowerLeftX();
		double fromBottom = lonLat.getLat() - extent.getLowerLeftY();
		double fromTop = extent.getHeight() - fromBottom;
		
		double ratioFromLeft = fromLeft / extent.getWidth() * proj.leftToRight();
		double ratioFromTop = fromTop / extent.getHeight() * proj.topToBottom() * -1;
		
		int x = (int)round(ratioFromLeft * area.getWidth());
		int y = (int)round(ratioFromTop * area.getHeight());
		
		return new Point(x, y);
	}

	public Point getCenterPoint(Point topLeft, Size areaSize) {
		return new Point(
				topLeft.getX() + areaSize.getWidth() / 2,
				topLeft.getY() + areaSize.getHeight() / 2);
	}

	public boolean intersect(Bounds lhs, Bounds rhs, Projection proj) {
		boolean xIntersect, yIntersect;
		if (proj.leftToRight() > 0) {
			xIntersect = lhs.getLowerLeftX() < rhs.getUpperRightX() && lhs.getUpperRightX() > rhs.getLowerLeftX();
		} else {
			xIntersect = lhs.getLowerLeftX() > rhs.getUpperRightX() && lhs.getUpperRightX() < rhs.getLowerLeftX();
		}
		if (proj.topToBottom() > 0) {
			yIntersect = lhs.getLowerLeftY() > rhs.getUpperRightY() && lhs.getUpperRightY() < rhs.getLowerLeftY();
		} else {
			yIntersect = lhs.getLowerLeftY() < rhs.getUpperRightY() && lhs.getUpperRightY() > rhs.getLowerLeftY();
		}
		return xIntersect && yIntersect;
	}

	public boolean intersect(Rectangle lhs, Rectangle rhs) {
		return lhs.x < rhs.x + rhs.width && lhs.x + lhs.width > rhs.x &&
			lhs.y < rhs.y + rhs.height && lhs.y + lhs.height > rhs.y;
	}

	public Bounds getEffectiveExtent(Bounds maxExtent, double resolution, LonLat center, Projection proj) {
		Size vSize = getPixelSize(maxExtent, resolution);
		if (vSize.getWidth() <= 10000 && vSize.getHeight() <= 10000) {
			return maxExtent;
		}
		Size effectiveSize = new Size(min(vSize.getWidth(), 10000), min(vSize.getHeight(), 10000));
		double halfWidth = getUnitWidth(effectiveSize, resolution) / 2.0;
		double halfHeight = getUnitHeight(effectiveSize, resolution) / 2.0;

		double lowerX, lowerY, upperX, upperY;
		if (proj.leftToRight() > 0) {
			lowerX = max(center.getLon() - halfWidth, maxExtent.getLowerLeftX());
			upperX = min(center.getLon() + halfWidth, maxExtent.getUpperRightX());
		} else {
			lowerX = min(center.getLon() + halfWidth, maxExtent.getLowerLeftX());
			upperX = max(center.getLon() - halfWidth, maxExtent.getUpperRightX());
		}
		if (proj.topToBottom() > 0) {
			lowerY = min(center.getLat() + halfHeight, maxExtent.getLowerLeftY());
			upperY = max(center.getLat() - halfHeight, maxExtent.getUpperRightY());			
		} else {
			lowerY = max(center.getLat() - halfHeight, maxExtent.getLowerLeftY());
			upperY = min(center.getLat() + halfHeight, maxExtent.getUpperRightY());
		}
		return new Bounds(lowerX, lowerY, upperX, upperY);

	}

	public int getDistance(Point lhs, Point rhs) {
		int distX = rhs.getX() - lhs.getX();
		int distY = rhs.getY() - lhs.getY();
		return (int)round(sqrt(pow(distX, 2) + pow(distY, 2)));
	}
	
	public double getAngle(Point p) {
		return atan2(p.getY(), p.getX());
	}

	public Point addToPoint(Point centerPoint, int amountPx, Direction dir) {
		switch (dir) {
		case UP: 	return new Point(centerPoint.getX(), centerPoint.getY() - amountPx);
		case RIGHT: return new Point(centerPoint.getX() + amountPx, centerPoint.getY());
		case DOWN: 	return new Point(centerPoint.getX(), centerPoint.getY() + amountPx);
		case LEFT: 	return new Point(centerPoint.getX() - amountPx, centerPoint.getY());
		default: 	return centerPoint;
		}
	}

	public Bounds keepInBounds(Bounds extent, Bounds maxExtent, Projection p) {
		double lowX = extent.getLowerLeftX() - maxExtent.getLowerLeftX();
		double lowY = extent.getLowerLeftY() - maxExtent.getLowerLeftY();
		double highX = extent.getUpperRightX() - maxExtent.getUpperRightX();
		double highY = extent.getUpperRightY() - maxExtent.getUpperRightY();
		
		double xOffset = 0;
		if (p.leftToRight() > 0) {
			if (lowX < 0) xOffset = -lowX;
			else if (highX > 0) xOffset = -highX;
		} else {
			if (lowX > 0) xOffset = -lowX;
			else if (highX < 0) xOffset = -highX;			
		}
		
		double yOffset = 0;
		if (p.topToBottom() > 0) {
			if (lowY > 0) yOffset = -lowY;
			if (highY < 0) yOffset = -highY;
		} else {
			if (lowY < 0) yOffset = -lowY;
			if (highY > 0) yOffset = -highY;			
		}
		
		return new Bounds(
				extent.getLowerLeftX() + xOffset,
				extent.getLowerLeftY() + yOffset,
				extent.getUpperRightX() + xOffset,
				extent.getUpperRightY() + yOffset);
	}
	
	public double getLonDegreeLengthMeters(LonLat at) {
		return cos(toRadians(at.getLat())) * 111325;
	}
	
}

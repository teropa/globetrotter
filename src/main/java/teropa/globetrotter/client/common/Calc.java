package teropa.globetrotter.client.common;

import static java.lang.Math.atan2;
import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import teropa.globetrotter.client.Map;
import teropa.globetrotter.client.proj.Projection;

public class Calc {

	private final Map map;
	
	public Calc(Map map) {
		this.map = map;
	}
	
	public Size getVirtualPixelSize() {
		return getVirtualPixelSize(map.getResolution());
	}

	public Size getVirtualPixelSize(double resolution) {
		Bounds bounds = map.getMaxExtent();
		int width = (int)round(bounds.getWidth() / resolution);
		int height = (int)round(bounds.getHeight() / resolution);
		return new Size(width, height);
	}

	public LonLat getLonLat(Point point) {
		Bounds extent = map.getMaxExtent();
		Size area = getVirtualPixelSize();
		Projection proj = map.getProjection();
		
		double ratioFromLeft = ((double)point.getX()) / ((double)area.getWidth());
		double fromBottom = area.getHeight() - point.getY();
		double ratioFromBottom = fromBottom / ((double)area.getHeight());
		
		double lon = extent.getLowerLeftX() + ratioFromLeft * extent.getWidth() * proj.leftToRight();
		double lat = extent.getLowerLeftY() - ratioFromBottom * extent.getHeight() * proj.topToBottom();
		
		return new LonLat(lon, lat);
	}
	
	public Point getPoint(LonLat lonLat) {
		Bounds extent = map.getMaxExtent();
		Size area = getVirtualPixelSize();
		Projection proj = map.getProjection();
		
		double fromLeft = lonLat.getLon() - extent.getLowerLeftX();
		double fromBottom = lonLat.getLat() - extent.getLowerLeftY();
		double fromTop = extent.getHeight() - fromBottom;
		
		double ratioFromLeft = fromLeft / extent.getWidth() * proj.leftToRight();
		double ratioFromTop = fromTop / extent.getHeight() * proj.topToBottom() * -1;
		
		int x = (int)round(ratioFromLeft * area.getWidth());
		int y = (int)round(ratioFromTop * area.getHeight());
		
		return new Point(x, y);
	}

	public boolean intersect(Rectangle lhs, Rectangle rhs) {
		return lhs.x < rhs.x + rhs.width && lhs.x + lhs.width > rhs.x &&
			lhs.y < rhs.y + rhs.height && lhs.y + lhs.height > rhs.y;
	}

	public int getDistance(Point lhs, Point rhs) {
		int distX = rhs.getX() - lhs.getX();
		int distY = rhs.getY() - lhs.getY();
		return (int)round(sqrt(pow(distX, 2) + pow(distY, 2)));
	}
	
	public double getAngle(Point p) {
		return atan2(p.getY(), p.getX());
	}
	
}

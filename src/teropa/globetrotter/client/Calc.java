package teropa.globetrotter.client;



public class Calc {

	public static Size getPixelSize(Bounds bounds, double resolution) {
		return new Size(getPixelWidth(bounds, resolution), getPixelHeight(bounds, resolution));
	}
	
	public static int getPixelWidth(Bounds bounds, double resolution) {
		return (int)Math.floor(bounds.getWidth() / resolution);		
	}
	
	public static int getPixelHeight(Bounds bounds, double resolution) {
		return (int)Math.floor(bounds.getHeight() / resolution);
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
		final double fromWidth = bounds.getWidth();
		final double fromHeight = bounds.getHeight();
		final double toWidth = to.getWidth();
		final double toHeight = to.getHeight();
		final boolean narrowWidth = fromWidth > toWidth;
		final boolean narrowHeight = fromHeight > toHeight;
		
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
		
		int x = (int)Math.floor(ratioFromLeft * area.getWidth());
		int y = (int)Math.floor(ratioFromTop * area.getHeight());
		
		return new Point(x, y);
	}
	
	// getLonLat(getPoint(center, maxExtent, view.getSize()), maxExtent, view.getSize())
}

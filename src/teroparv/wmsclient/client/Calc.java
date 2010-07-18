package teroparv.wmsclient.client;

import com.google.gwt.core.client.GWT;

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
	
	public static int getCoordinateWidth(Size size, double resolution) {
		return (int)Math.floor(size.getWidth() * resolution);
	}
	
	public static int getCoordinateHeight(Size size, double resolution) {
		return (int)Math.floor(size.getHeight() * resolution);
	}
	
	public static Bounds getExtent(LonLat center, double resolution, Size viewportSize) {
		final int halfWidth = getCoordinateWidth(viewportSize, resolution) / 2;
		final int halfHeight = getCoordinateHeight(viewportSize, resolution) / 2;
		
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
	
}

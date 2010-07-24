package teropa.globetrotter.client;

import teropa.globetrotter.client.common.Bounds;
import teropa.globetrotter.client.common.LonLat;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Size;

public interface ViewContext {

	String getSRS();

	boolean isDrawn();

	Grid getGrid();

	Size getTileSize();

	Bounds getExtent();

	Size getViewportSize();

	LonLat getCenter();

	double getResolution();

	Bounds getMaxExtent();

	Point getViewportLocation();

	Size getViewSize();

	Point getViewCenterPoint();

}

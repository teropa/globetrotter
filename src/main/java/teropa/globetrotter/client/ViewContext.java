package teropa.globetrotter.client;

import java.util.List;

import teropa.globetrotter.client.common.Bounds;
import teropa.globetrotter.client.common.LonLat;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Rectangle;
import teropa.globetrotter.client.common.Size;
import teropa.globetrotter.client.proj.Projection;

public interface ViewContext {

	boolean isDrawn();

	Projection getProjection();
	
	Grid getGrid();

	Size getTileSize();
	
	Bounds getVisibleExtent();

	Rectangle getVisibleRectangle();
	
	Size getViewportSize();

	LonLat getCenter();

	double getResolution();

	int getResolutionIndex();
	
	Point getViewportLocation();

	Size getViewSize();

	Point getViewCenterPoint();
	
	Bounds getMaxExtent();

	CanvasView getView();

	List<Layer> getLayers();


}

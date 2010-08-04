package teropa.globetrotter.client;

import com.google.gwt.event.shared.HandlerRegistration;

import teropa.globetrotter.client.common.Bounds;
import teropa.globetrotter.client.common.LonLat;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Rectangle;
import teropa.globetrotter.client.common.Size;
import teropa.globetrotter.client.event.MapViewChangedEvent;
import teropa.globetrotter.client.proj.Projection;

public interface ViewContext {

	Projection getProjection();

	boolean isDrawn();

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
	
	HandlerRegistration addMapViewChangedHandler(MapViewChangedEvent.Handler handler);

	Bounds getMaxExtent();


}

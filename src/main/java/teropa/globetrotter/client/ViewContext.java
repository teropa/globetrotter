package teropa.globetrotter.client;

import com.google.gwt.event.shared.HandlerRegistration;

import teropa.globetrotter.client.common.Bounds;
import teropa.globetrotter.client.common.LonLat;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Size;
import teropa.globetrotter.client.event.MapViewChangedEvent;

public interface ViewContext {

	String getSRS();

	boolean isDrawn();

	Grid getGrid();

	Size getTileSize();

	Bounds getEffectiveExtent();
	
	Bounds getVisibleExtent();

	Size getViewportSize();

	LonLat getCenter();

	double getResolution();

	Point getViewportLocation();

	Size getViewSize();

	Point getViewCenterPoint();
	
	HandlerRegistration addMapViewChangedHandler(MapViewChangedEvent.Handler handler);

}

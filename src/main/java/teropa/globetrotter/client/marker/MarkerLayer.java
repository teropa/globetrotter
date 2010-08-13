package teropa.globetrotter.client.marker;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import teropa.globetrotter.client.CanvasView;
import teropa.globetrotter.client.Grid;
import teropa.globetrotter.client.Grid.Tile;
import teropa.globetrotter.client.Layer;
import teropa.globetrotter.client.common.Calc;
import teropa.globetrotter.client.common.LonLat;
import teropa.globetrotter.client.common.Point;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.widgetideas.graphics.client.ImageLoader;
import com.google.gwt.widgetideas.graphics.client.ImageLoader.CallBack;

public class MarkerLayer extends Layer {
	
	private final HashMap<Marker, ImageElement> markers = new HashMap<Marker, ImageElement>();
	private final HandlerManager handlers = new HandlerManager(this);
	
	public MarkerLayer(String name) {
		super(name, false);
	}
	
	public void addMarker(final Marker marker) {
		final ImageResource img = marker.getImage();
		ImageLoader.loadImages(new String[] { img.getURL() }, new CallBack() {
			public void onImagesLoaded(ImageElement[] imageElements) {
				ImageElement imgEl = imageElements[0];
				drawMarker(marker, imgEl);
				markers.put(marker, imgEl);
			}
		});
	}
	
	public void addMarkers(Collection<? extends Marker> newMarkers) {
		for (final Marker each : newMarkers) {
			addMarker(each);
		}
	}
	
	public void removeMarker(Marker marker) {
		markers.remove(marker);
	}
		
	public HandlerRegistration addMarkerClickHandler(MarkerClickEvent.Handler handler) {
		return handlers.addHandler(MarkerClickEvent.TYPE, handler);
	}
	
	public HandlerRegistration addMarkerDoubleClickHandler(MarkerDoubleClickEvent.Handler handler) {
		return handlers.addHandler(MarkerDoubleClickEvent.TYPE, handler);
	}
	
	@Override
	public void drawOn(CanvasView canvasView) {
		for (Map.Entry<Marker, ImageElement> eachEntry : markers.entrySet()) {
			Marker marker = eachEntry.getKey();
			ImageElement imgEl = eachEntry.getValue();
			drawMarker(marker, imgEl);
		}
	}

	private void drawMarker(Marker marker, ImageElement imgEl) {
		ImageResource img = marker.getImage();
		LonLat normalizedLoc = getProjection().from(marker.getLoc());
		LonLat projectedLoc = context.getProjection().to(normalizedLoc);
		Point point = Calc.getPoint(projectedLoc, context.getMaxExtent(), context.getViewSize(), context.getProjection());
		Point translatedPoint = marker.getPinPosition().translateAroundPoint(point, marker.getSize());
		context.getView().getCanvas().drawImage(imgEl, img.getLeft(), img.getTop(), img.getWidth(), img.getHeight(), translatedPoint.getX(), translatedPoint.getY(), img.getWidth(), img.getHeight());		
	}
	
	@Override
	public void tilesActivated(Collection<Grid.Tile> newTiles) {
	}
	
	@Override
	public void tilesDeactivated(Collection<Tile> removedTiles) {
	}
	
	@Override
	public void allTilesDeactivated() {
	}

}

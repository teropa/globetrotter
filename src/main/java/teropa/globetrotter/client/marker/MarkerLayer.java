package teropa.globetrotter.client.marker;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import teropa.globetrotter.client.Grid;
import teropa.globetrotter.client.Grid.Tile;
import teropa.globetrotter.client.Layer;
import teropa.globetrotter.client.View;
import teropa.globetrotter.client.ViewContext;
import teropa.globetrotter.client.common.Calc;
import teropa.globetrotter.client.common.LonLat;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Rectangle;
import teropa.globetrotter.client.event.internal.ViewClickEvent;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.widgetideas.graphics.client.ImageLoader;
import com.google.gwt.widgetideas.graphics.client.ImageLoader.CallBack;

public class MarkerLayer extends Layer implements ViewClickEvent.Handler {
	
	private final HashMap<Marker, ImageElement> markers = new HashMap<Marker, ImageElement>();
	private final HandlerManager handlers = new HandlerManager(this);
	
	public MarkerLayer(String name) {
		super(name, false);
	}

	@Override
	public void init(ViewContext ctx) {
		super.init(ctx);
		ctx.getView().addViewClickHandler(this);
	}
	
	public void addMarkers(Collection<? extends Marker> newMarkers) {
		for (final Marker each : newMarkers) {
			addMarker(each);
		}
	}

	private void addMarker(final Marker marker) {
		final ImageResource img = marker.getImage();
		ImageLoader.loadImages(new String[] { img.getURL() }, new CallBack() {
			public void onImagesLoaded(ImageElement[] imageElements) {
				ImageElement imgEl = imageElements[0];
				drawMarker(marker, imgEl);
				markers.put(marker, imgEl);
			}
		});
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
	public void drawOn(View canvasView) {
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
	
	
	public void onViewClicked(ViewClickEvent event) {
		Point p = event.point;
		for (Marker each : markers.keySet()) {
			LonLat normalizedLoc = getProjection().from(each.getLoc());
			LonLat projectedLoc = context.getProjection().to(normalizedLoc);
			Point point = Calc.getPoint(projectedLoc, context.getMaxExtent(), context.getViewSize(), context.getProjection());
			Point translatedPoint = each.getPinPosition().translateAroundPoint(point, each.getSize());
			if (p.getX() >= translatedPoint.getX() && p.getX() <= translatedPoint.getX() + each.getSize().getWidth() &&
				p.getY() >= translatedPoint.getY() && p.getY() <= translatedPoint.getY() + each.getSize().getHeight()) {
				Window.alert(each.toString());
				break;
			}
		}
	}
	
	@Override
	public void onTilesActivated(Collection<Grid.Tile> newTiles) {
	}
	
	@Override
	public void onTilesDeactivated(Collection<Tile> removedTiles) {
	}
	
	@Override
	public void onAllTilesDeactivated() {
	}
	
	@Override
	public void updateTile(Tile tile) {
		Rectangle tileRect = new Rectangle(tile.getLeftX(), tile.getTopY(), 256, 256);
		for (Marker each : markers.keySet()) {
			LonLat normalizedLoc = getProjection().from(each.getLoc());
			LonLat projectedLoc = context.getProjection().to(normalizedLoc);
			Point point = Calc.getPoint(projectedLoc, context.getMaxExtent(), context.getViewSize(), context.getProjection());
			Point translatedPoint = each.getPinPosition().translateAroundPoint(point, each.getSize());
			Rectangle markerRect = new Rectangle(translatedPoint.getX(), translatedPoint.getY(), each.getSize().getWidth(), each.getSize().getHeight());
			if (Calc.intersect(tileRect, markerRect)) {
				ImageElement imgEl = markers.get(each);
				ImageResource img = each.getImage();
				context.getView().getCanvas().drawImage(imgEl, img.getLeft(), img.getTop(), img.getWidth(), img.getHeight(), translatedPoint.getX(), translatedPoint.getY(), img.getWidth(), img.getHeight());				
			}
		}
	}

}

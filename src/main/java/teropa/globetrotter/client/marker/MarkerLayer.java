package teropa.globetrotter.client.marker;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import teropa.globetrotter.client.Grid;
import teropa.globetrotter.client.Grid.Tile;
import teropa.globetrotter.client.Layer;
import teropa.globetrotter.client.View;
import teropa.globetrotter.client.ViewContext;
import teropa.globetrotter.client.common.LonLat;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Rectangle;
import teropa.globetrotter.client.event.MapZoomedEvent;
import teropa.globetrotter.client.event.internal.ViewClickEvent;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.widgetideas.graphics.client.ImageLoader;
import com.google.gwt.widgetideas.graphics.client.ImageLoader.CallBack;

public class MarkerLayer extends Layer implements ViewClickEvent.Handler, MapZoomedEvent.Handler {
	
	private static class MarkerData {
		public final ImageElement image;
		public final LonLat projectedLoc;
		public Point pointInCurrentSize;
		
		public MarkerData(ImageElement image, LonLat projectedPos) {
			this.image = image;
			this.projectedLoc = projectedPos;
		}
	}
	
	private final HashMap<Marker, MarkerData> markers = new HashMap<Marker, MarkerData>();
	private final HandlerManager handlers = new HandlerManager(this);
	
	public MarkerLayer(String name) {
		super(name, false);
	}

	@Override
	public void init(ViewContext ctx) {
		super.init(ctx);
		ctx.getView().addViewClickHandler(this);
		ctx.addMapZoomedHandler(this);
	}
	
	public void addMarkers(final Collection<? extends Marker> newMarkers) {
		HashSet<String> urls = collectImageUrls(newMarkers);
		
		ImageLoader.loadImages(urls.toArray(new String[urls.size()]), new CallBack() {

			public void onImagesLoaded(ImageElement[] imageElements) {
				Rectangle visibleRect = context.getVisibleRectangle();
				for (int i=0 ;i<imageElements.length ; i++) {
					drawMatchingMarkers(imageElements[i], visibleRect);
				}
			}

			private void drawMatchingMarkers(ImageElement img, Rectangle visibleRect) {
				String src = img.getSrc();
				for (Marker eachMarker : newMarkers) {
					if (src.equals(eachMarker.getImage().getURL())) {
						MarkerData data = new MarkerData(img, projectLocation(eachMarker));
						drawMarker(eachMarker, data, visibleRect);
						markers.put(eachMarker, data);							
					}
				}
			}
			
		});
	}

	private HashSet<String> collectImageUrls(Collection<? extends Marker> markers) {
		HashSet<String> urls = new HashSet<String>();
		for (Marker each : markers) {
			urls.add(each.getImage().getURL());
		}
		return urls;
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
		Rectangle visibleRect = context.getVisibleRectangle();
		for (Map.Entry<Marker, MarkerData> eachEntry : markers.entrySet()) {
			Marker marker = eachEntry.getKey();
			MarkerData data = eachEntry.getValue();
			drawMarker(marker, data, visibleRect);
		}
	}

	private void drawMarker(Marker marker, MarkerData data, Rectangle visibleRect) {
		ImageResource imgResource = marker.getImage();
		int imgWidth = imgResource.getWidth();
		int imgHeight = imgResource.getHeight();
		Point pt = getMarkerPoint(marker, data);
		Rectangle markerRect = new Rectangle(pt.getX(), pt.getY(), imgWidth, imgHeight);
		if (context.calc().intersect(visibleRect, markerRect)) {
			context.getView().getCanvas().drawImage(
					data.image,
					imgResource.getLeft(),
					imgResource.getTop(),
					imgWidth,
					imgHeight,
					pt.getX(),
					pt.getY(),
					imgWidth,
					imgHeight);
		}
	}

	private LonLat projectLocation(Marker marker) {
		LonLat normalizedLoc = getProjection().from(marker.getLoc());
		LonLat projectedLoc = context.getProjection().to(normalizedLoc);
		return projectedLoc;
	}
	
	
	public void onViewClicked(ViewClickEvent event) {
		Point p = event.point;
		for (Map.Entry<Marker, MarkerData> eachEntry : markers.entrySet()) {
			Marker marker = eachEntry.getKey();
			MarkerData data = eachEntry.getValue();
			Point pt = getMarkerPoint(marker, data);
			if (p.getX() >= pt.getX() && p.getX() <= pt.getX() + marker.getSize().getWidth() &&
				p.getY() >= pt.getY() && p.getY() <= pt.getY() + marker.getSize().getHeight()) {
				Window.alert(marker.toString());
				break;
			}
		}
	}

	private Point getMarkerPoint(Marker marker, MarkerData data) {
		LonLat projectedLoc = data.projectedLoc;
		if (data.pointInCurrentSize == null) {
			Point point = context.calc().getPoint(projectedLoc);
			data.pointInCurrentSize = marker.getPinPosition().translateAroundPoint(point, marker.getSize());			
		}
		Point pt = data.pointInCurrentSize;
		return pt;
	}

	public void onMapZoomed(MapZoomedEvent event) {
		for (MarkerData each : markers.values()) {
			each.pointInCurrentSize = null;
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
		for (Map.Entry<Marker, MarkerData> eachEntry : markers.entrySet()) {
			Marker marker = eachEntry.getKey();
			MarkerData data = eachEntry.getValue();
			Point pt = getMarkerPoint(marker, data);
			Rectangle markerRect = new Rectangle(pt.getX(), pt.getY(), marker.getSize().getWidth(), marker.getSize().getHeight());
			if (context.calc().intersect(tileRect, markerRect)) {
				ImageResource img = marker.getImage();
				context.getView().getCanvas().drawImage(data.image, img.getLeft(), img.getTop(), img.getWidth(), img.getHeight(), pt.getX(), pt.getY(), img.getWidth(), img.getHeight());				
			}
		}
	}

}

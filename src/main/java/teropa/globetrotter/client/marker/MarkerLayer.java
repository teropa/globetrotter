package teropa.globetrotter.client.marker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import teropa.globetrotter.client.Layer;
import teropa.globetrotter.client.Map;
import teropa.globetrotter.client.View;
import teropa.globetrotter.client.common.LonLat;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Rectangle;
import teropa.globetrotter.client.event.MapZoomedEvent;
import teropa.globetrotter.client.event.internal.ViewClickEvent;
import teropa.globetrotter.client.grid.Tile;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.widgetideas.graphics.client.ImageLoader;
import com.google.gwt.widgetideas.graphics.client.ImageLoader.CallBack;

public class MarkerLayer extends Layer implements ViewClickEvent.Handler, MapZoomedEvent.Handler {
	
	private static class MarkerData {
		public final Marker marker;
		public final ImageElement image;
		public final LonLat projectedLoc;
		public Point pointInCurrentSize;
		
		public MarkerData(Marker marker, ImageElement image, LonLat projectedPos) {
			this.marker = marker;
			this.image = image;
			this.projectedLoc = projectedPos;
		}
	}
	
	private final List<MarkerData> markers = new ArrayList<MarkerData>();
	private final HandlerManager handlers = new HandlerManager(this);
	
	public MarkerLayer(String name) {
		super(name, false);
	}

	@Override
	public void init(Map map) {
		super.init(map);
		map.getView().addViewClickHandler(this);
		map.addMapZoomedHandler(this);
	}
	
	public void addMarkers(final Collection<? extends Marker> newMarkers) {
		HashSet<String> urls = collectImageUrls(newMarkers);
		
		ImageLoader.loadImages(urls.toArray(new String[urls.size()]), new CallBack() {

			public void onImagesLoaded(ImageElement[] imageElements) {
				Rectangle visibleRect = map.getVisibleRectangle();
				for (int i=0 ;i<imageElements.length ; i++) {
					drawMatchingMarkers(imageElements[i], visibleRect);
				}
			}

			private void drawMatchingMarkers(ImageElement img, Rectangle visibleRect) {
				String src = img.getSrc();
				for (Marker eachMarker : newMarkers) {
					if (src.equals(eachMarker.getImage().getURL())) {
						MarkerData data = new MarkerData(eachMarker, img, projectLocation(eachMarker));
						drawMarker(data, visibleRect);
						markers.add(data);					
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
		Rectangle visibleRect = map.getVisibleRectangle();
		int sz = markers.size();
		for (int i=0 ; i<sz ; i++) {
			drawMarker(markers.get(i), visibleRect);
		}
	}

	private void drawMarker(MarkerData data, Rectangle visibleRect) {
		ImageResource imgResource = data.marker.getImage();
		int imgWidth = imgResource.getWidth();
		int imgHeight = imgResource.getHeight();
		Point pt = getMarkerPoint(data);
		Rectangle markerRect = new Rectangle(pt.getX(), pt.getY(), imgWidth, imgHeight);
		if (map.calc().intersect(visibleRect, markerRect)) {
			map.getView().getCanvas().drawImage(
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
		LonLat projectedLoc = map.getProjection().to(normalizedLoc);
		return projectedLoc;
	}
	
	
	public void onViewClicked(ViewClickEvent event) {
		Point p = event.point;
		int sz = markers.size();
		for (int i=0 ; i<sz ; i++) {
			MarkerData data = markers.get(i);
			Point pt = getMarkerPoint(data);
			if (p.getX() >= pt.getX() && p.getX() <= pt.getX() + data.marker.getSize().getWidth() &&
				p.getY() >= pt.getY() && p.getY() <= pt.getY() + data.marker.getSize().getHeight()) {
				handlers.fireEvent(new MarkerClickEvent(data.marker, this));
				break;
			}
		}
	}

	private Point getMarkerPoint(MarkerData data) {
		if (data.pointInCurrentSize == null) {
			Point point = map.calc().getPoint(data.projectedLoc);
			data.pointInCurrentSize = data.marker.getPinPosition().translateAroundPoint(point, data.marker.getSize());			
		}
		Point pt = data.pointInCurrentSize;
		return pt;
	}

	public void onMapZoomed(MapZoomedEvent event) {
		int sz = markers.size();
		for (int i=0 ; i<sz ; i++) {
			markers.get(i).pointInCurrentSize = null;
		}
	}
	
	@Override
	public void onTilesActivated(Collection<Tile> newTiles) {
	}
	
	@Override
	public void onTilesDeactivated(Collection<Tile> removedTiles) {
	}
	
	@Override
	public void onAllTilesDeactivated() {
	}
	
	@Override
	public void updateTile(Tile tile) {
		int tileWidth = map.getGrid().getTileWidth();
		int tileHeight = map.getGrid().getTileHeight();
		Rectangle tileRect = new Rectangle(tile.getLeftX(), tile.getTopY(), tileWidth, tileHeight);
		int sz = markers.size();
		for (int i=0 ; i<sz ; i++) {
			MarkerData data = markers.get(i);
			Point pt = getMarkerPoint(data);
			Rectangle markerRect = new Rectangle(pt.getX(), pt.getY(), data.marker.getSize().getWidth(), data.marker.getSize().getHeight());
			if (map.calc().intersect(tileRect, markerRect)) {
				ImageResource img = data.marker.getImage();
				map.getView().getCanvas().drawImage(data.image, img.getLeft(), img.getTop(), img.getWidth(), img.getHeight(), pt.getX(), pt.getY(), img.getWidth(), img.getHeight());				
			}
		}
	}

}

package teropa.globetrotter.client.marker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import teropa.globetrotter.client.Layer;
import teropa.globetrotter.client.Map;
import teropa.globetrotter.client.View;
import teropa.globetrotter.client.common.LonLat;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Rectangle;
import teropa.globetrotter.client.common.Size;
import teropa.globetrotter.client.event.internal.ViewClickEvent;
import teropa.globetrotter.client.grid.Tile;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.widgetideas.graphics.client.ImageLoader;
import com.google.gwt.widgetideas.graphics.client.ImageLoader.CallBack;

public class MarkerLayer extends Layer implements ViewClickEvent.Handler {
	
	private static class MarkerData {
		public final Marker marker;
		public final ImageElement image;
		public final LonLat projectedLoc;
		
		public MarkerData(Marker marker, ImageElement image, LonLat projectedPos) {
			this.marker = marker;
			this.image = image;
			this.projectedLoc = projectedPos;
		}
	}
	
	private final List<MarkerData> markers = new ArrayList<MarkerData>();
	private final HandlerManager handlers = new HandlerManager(this);
	
	private HashMap<Tile, ArrayList<MarkerData>> markersByTile = new HashMap<Tile, ArrayList<MarkerData>>();
	
	public MarkerLayer(String name) {
		super(name, false);
	}

	@Override
	public void init(Map map) {
		super.init(map);
		map.getView().addViewClickHandler(this);
	}
	
	public void addMarkers(final Collection<? extends Marker> newMarkers) {
		HashSet<String> urls = collectImageUrls(newMarkers);
		
		ImageLoader.loadImages(urls.toArray(new String[urls.size()]), new CallBack() {

			public void onImagesLoaded(ImageElement[] imageElements) {
				for (int i=0 ;i<imageElements.length ; i++) {
					drawMatchingMarkers(imageElements[i]);
				}
			}

			private void drawMatchingMarkers(ImageElement img) {
				String src = img.getSrc();
				for (Marker eachMarker : newMarkers) {
					if (src.equals(eachMarker.getImage().getURL())) {
						MarkerData data = new MarkerData(eachMarker, img, projectLocation(eachMarker));
						markers.add(data);
						updateMatchingTiles(data);			
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
	
	private void updateMatchingTiles(MarkerData data) {
		for (java.util.Map.Entry<Tile, ArrayList<MarkerData>> entry : markersByTile.entrySet()) {
			if (isMarkerInTile(data, entry.getKey())) {
				entry.getValue().add(data);
				map.getView().tileUpdated(entry.getKey(), this);
			}
		}
	}

	private boolean isMarkerInTile(MarkerData data, Tile tile) {
		Rectangle tileRect = new Rectangle(tile.getLeftX(), tile.getTopY(), map.getTileSize().getWidth(), map.getTileSize().getHeight());	
		ImageResource imgResource = data.marker.getImage();
		int imgWidth = imgResource.getWidth();
		int imgHeight = imgResource.getHeight();
		Point pt = getMarkerPoint(data, tile);
		Rectangle markerRect = new Rectangle(pt.getX(), pt.getY(), imgWidth, imgHeight);
		if (map.calc().intersect(tileRect, markerRect)) {
			return true;
		}
		return false;
	}

	public void removeMarker(Marker marker) {
		MarkerData data = null;
		for (MarkerData each : markers) {
			if (each.marker.equals(marker)) {
				data = each;
				markers.remove(each);
				break;
			}
		}
		if (data != null) {
			for (java.util.Map.Entry<Tile, ArrayList<MarkerData>> entry : markersByTile.entrySet()) {
				if (entry.getValue().remove(data)) {
					map.getView().tileUpdated(entry.getKey(), this);
				}
			}
		}
	}
		
	public HandlerRegistration addMarkerClickHandler(MarkerClickEvent.Handler handler) {
		return handlers.addHandler(MarkerClickEvent.TYPE, handler);
	}
	
	public HandlerRegistration addMarkerDoubleClickHandler(MarkerDoubleClickEvent.Handler handler) {
		return handlers.addHandler(MarkerDoubleClickEvent.TYPE, handler);
	}
	
	@Override
	public void drawOn(View canvasView) {
		for (java.util.Map.Entry<Tile, ArrayList<MarkerData>> entry : markersByTile.entrySet()) {
			Tile tile = entry.getKey();
			for (MarkerData aech : entry.getValue()) {
				drawMarker(aech, tile);
			}
		}
	}

	private void drawMarker(MarkerData data, Tile inTile) {
		ImageResource imgResource = data.marker.getImage();
		int imgWidth = imgResource.getWidth();
		int imgHeight = imgResource.getHeight();
		Point pt = getMarkerPoint(data, inTile);
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

	private LonLat projectLocation(Marker marker) {
		LonLat normalizedLoc = getProjection().from(marker.getLoc());
		LonLat projectedLoc = map.getProjection().to(normalizedLoc);
		return projectedLoc;
	}
	
	
	public void onViewClicked(ViewClickEvent event) {
		Point p = event.point;
		for (java.util.Map.Entry<Tile, ArrayList<MarkerData>> entry : markersByTile.entrySet()) {
			Tile tile = entry.getKey();
			for (MarkerData data : entry.getValue()) {
				Point pt = getMarkerPoint(data, tile);
				if (p.getX() >= pt.getX() && p.getX() <= pt.getX() + data.marker.getSize().getWidth() &&
					p.getY() >= pt.getY() && p.getY() <= pt.getY() + data.marker.getSize().getHeight()) {
					handlers.fireEvent(new MarkerClickEvent(data.marker, this));
					break;
				}
				
			}
		}
	}

	private Point getMarkerPoint(MarkerData data, Tile inTile) {
		Point point = map.calc().getPoint(data.projectedLoc);
		Size fullMapSize = map.calc().getVirtualPixelSize();
		int actualX = (inTile.getLeftX() / fullMapSize.getWidth()) * fullMapSize.getWidth() + point.getX();
		point = new Point(actualX, point.getY());
		return data.marker.getPinPosition().translateAroundPoint(point, data.marker.getSize());			
	}

	@Override
	public void onTilesActivated(Collection<Tile> newTiles) {
		for (Tile each : newTiles) {
			ArrayList<MarkerData> markersForThis = new ArrayList<MarkerLayer.MarkerData>();
			for (MarkerData data : markers) {
				if (isMarkerInTile(data, each)) {
					markersForThis.add(data);
				}
			}
			markersByTile.put(each, markersForThis);
		}
	}
	
	@Override
	public void onTilesDeactivated(Collection<Tile> removedTiles) {
		markersByTile.keySet().removeAll(removedTiles);
	}
	
	@Override
	public void onAllTilesDeactivated() {
		markersByTile.clear();
	}
	
	@Override
	public void updateTile(Tile tile) {
		ArrayList<MarkerData> markersForTile = markersByTile.get(tile);
		if (markersForTile != null) {
			for (MarkerData data : markersForTile) {
				drawMarker(data, tile);
			}
		}
	}

}

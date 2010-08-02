package teropa.globetrotter.client.osm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teropa.globetrotter.client.Grid;
import teropa.globetrotter.client.ImagePool;
import teropa.globetrotter.client.Layer;
import teropa.globetrotter.client.common.Bounds;
import teropa.globetrotter.client.common.Calc;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Size;
import teropa.globetrotter.client.event.MapViewChangedEvent;
import teropa.globetrotter.client.proj.GoogleMercator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class OpenStreetMapLayer extends Layer {

	// based on the zoom levels at http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
	public static final double[] SUPPORTED_RESOLUTIONS = {
		156543.03392804097,
		78271.51696402048,
		39135.75848201024,
		19567.87924100512,
		9783.93962050256,
		4891.96981025128,
		2445.98490512564,
		1222.99245256282,
		611.49622628141,
		305.748113140705,
		152.8740565703525,
		76.43702828517625,
		38.21851414258813,
		19.109257071294063,
		9.554628535647032,
		4.777314267823516,
		2.388657133911758,
		1.194328566955879,
		0.5971642834779395};
	
	private final AbsolutePanel container = new AbsolutePanel();
	
	private final HashMap<Grid.Tile,Image> imageTiles = new HashMap<Grid.Tile, Image>();
	private int buffer = 2;
	private double ignoreEventsBuffer = buffer / 4.0;
	private Point lastDrawnAtPoint = null;

	private final String baseUrl;
	
	public OpenStreetMapLayer(String baseUrl, String name, boolean base) {
		super(name, base, new GoogleMercator());
		this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
	}
	
	protected void onVisibilityChanged() {
		if (visible && initialized && context.isDrawn()) {
			addNewTiles();
		}
	}
	
	public void onMapViewChanged(MapViewChangedEvent evt) {
		if (evt.effectiveExtentChanged) {
			repositionTiles();
		}
		if (evt.zoomed) {
			removeTiles(true);
			lastDrawnAtPoint = null;
		}
		if ((evt.zoomed || evt.panned) && visible) {
			if (evt.panEnded || shouldDraw()) {
				addNewTiles();
			}			
		}
		if (evt.panEnded) {
			removeTiles(false);
		}
	}
	
	private boolean shouldDraw() {
		Point newCenter = context.getViewCenterPoint();
		if (lastDrawnAtPoint == null || distanceExceedsBuffer(newCenter, lastDrawnAtPoint)) {
			return true;
		}
		return false;
	}

	private boolean distanceExceedsBuffer(Point lhs, Point rhs) {
		int xDist = Math.abs(lhs.getX() - rhs.getX());
		if (xDist > ignoreEventsBuffer * context.getTileSize().getWidth()) return true;
		int yDist = Math.abs(lhs.getY() - rhs.getY());
		if (yDist > ignoreEventsBuffer * context.getTileSize().getHeight()) return true;
		return false;
	}

	private void removeTiles(boolean removeAll) {
		if (imageTiles.isEmpty()) return;
		
		Bounds bufferedExtent = widenToBuffer(context.getVisibleExtent());
		java.util.Iterator<Map.Entry<Grid.Tile, Image>> it = imageTiles.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Grid.Tile, Image> entry = it.next();
			if (removeAll || !Calc.intersect(bufferedExtent, entry.getKey().getExtent(), context.getProjection())) {
				container.remove(entry.getValue());
				ImagePool.release(entry.getValue());
				it.remove();
			}
		}
	}

	private void addNewTiles() {
		lastDrawnAtPoint = context.getViewCenterPoint();
		Grid grid = context.getGrid();
		Bounds extent = widenToBuffer(context.getVisibleExtent());
		List<Grid.Tile> tiles = grid.getTiles(extent);
		int length = tiles.size();
		GWT.log("view: "+context.getViewSize());
		GWT.log("port at "+context.getViewportLocation());
		for (int i=0 ; i<length ; i++) {
			Grid.Tile eachTile = tiles.get(i);
			if (!imageTiles.containsKey(eachTile)) { 
				Image image = ImagePool.get();
				image.setUrl(getUrl(context.getResolutionIndex(), eachTile.getCol(), grid.getNumRows() - eachTile.getRow() - 1));
				imageTiles.put(eachTile, image);
				container.add(image);
				Point topLeft = eachTile.getTopLeft();
				fastSetElementPosition(image.getElement(), topLeft.getX(), topLeft.getY());
			}
		}
	}

	private Bounds widenToBuffer(Bounds extent) {
		if (buffer > 0) {
			Size viewportSize = context.getViewportSize();
			Size widenedSize = new Size(viewportSize.getWidth() + 2 * buffer * context.getTileSize().getWidth(), viewportSize.getHeight() + 2 * buffer * context.getTileSize().getHeight());
			Bounds widenedExtent = Calc.getExtent(context.getCenter(), context.getResolution(), widenedSize, context.getProjection());
			return Calc.narrow(widenedExtent, context.getEffectiveExtent(), context.getProjection());
		} else {
			return extent;
		}
	}

	private void repositionTiles() {
		for (Map.Entry<Grid.Tile, Image> each : imageTiles.entrySet()) {
			Point topLeft = each.getKey().getTopLeft();
			fastSetElementPosition(each.getValue().getElement(), topLeft.getX(), topLeft.getY());
		}
	}

	private void fastSetElementPosition(Element elem, int left, int top) {
		Style style = elem.getStyle();
		style.setProperty("position", "absolute");
		style.setPropertyPx("left", left);
		style.setPropertyPx("top", top);
	}

	private String getUrl(int zoom, int x, int y) {
		return baseUrl + zoom + "/" + x + "/" + y + ".png";
	}
	
	@Override
	public Widget asWidget() {
		return container;
	}

}

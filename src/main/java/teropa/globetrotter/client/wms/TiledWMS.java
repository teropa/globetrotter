package teropa.globetrotter.client.wms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teropa.globetrotter.client.Grid;
import teropa.globetrotter.client.ImagePool;
import teropa.globetrotter.client.common.Bounds;
import teropa.globetrotter.client.common.Calc;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Size;
import teropa.globetrotter.client.event.MapViewChangedEvent;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class TiledWMS extends WMSBase {

	private final AbsolutePanel container = new AbsolutePanel();
	private final HashMap<Grid.Tile,Image> imageTiles = new HashMap<Grid.Tile, Image>();
	private int buffer = 2;
	private double ignoreEventsBuffer = buffer / 4.0;
	private Point lastDrawnAtPoint = null;
	
	public TiledWMS(String name, String url) {
		super(name, url);
	}

	protected void onVisibilityChanged() {
		if (visible && initialized && context.isDrawn()) {
			addNewTiles();
		}
	}
	
	@Override
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
			if (removeAll || !Calc.intersect(bufferedExtent, entry.getKey().getExtent())) {
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
		for (int i=0 ; i<length ; i++) {
			Grid.Tile eachTile = tiles.get(i);
			if (!imageTiles.containsKey(eachTile)) { 
				Image image = ImagePool.get();
				image.setUrl(constructUrl(eachTile.getExtent(), eachTile.getSize()));
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
			Bounds widenedExtent = Calc.getExtent(context.getCenter(), context.getResolution(), widenedSize);
			return Calc.narrow(widenedExtent, context.getEffectiveExtent());
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

	@Override
	public Widget asWidget() {
		return container;
	}
}

package teropa.globetrotter.client.wms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teropa.globetrotter.client.Grid;
import teropa.globetrotter.client.ImagePool;
import teropa.globetrotter.client.common.Bounds;
import teropa.globetrotter.client.common.Calc;
import teropa.globetrotter.client.common.LonLat;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Size;
import teropa.globetrotter.client.event.MapViewChangedEvent;
import teropa.globetrotter.client.proj.Projection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class TiledWMS extends WMSBase {

	private final AbsolutePanel container = new AbsolutePanel();
	private final HashMap<Grid.Tile, Image> imageTiles = new HashMap<Grid.Tile, Image>();

	public TiledWMS(String name, String url, boolean base) {
		super(name, url, base);
	}

	public TiledWMS(String name, String url, boolean base, Projection projection) {
		super(name, url, base, projection);
	}

	protected void onVisibilityChanged() {
		if (visible && initialized && context.isDrawn()) {
			addNewTiles();
		}
	}

	public void onMapViewChanged(MapViewChangedEvent evt) {
		removeTiles(true);
		addNewTiles();
	}

	private void removeTiles(boolean removeAll) {
		if (imageTiles.isEmpty())
			return;

		java.util.Iterator<Map.Entry<Grid.Tile, Image>> it = imageTiles
				.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Grid.Tile, Image> entry = it.next();
			if (removeAll
					|| !Calc.intersect(context.getVisibleRectangle(), entry
							.getKey().getRect())) {
				container.remove(entry.getValue());
				ImagePool.release(entry.getValue());
				it.remove();
			}
		}
	}

	private void addNewTiles() {
		Grid grid = context.getGrid();
		List<Grid.Tile> tiles = grid.getTiles(context.getVisibleRectangle());
		int length = tiles.size();
		for (int i = 0; i < length; i++) {
			Grid.Tile eachTile = tiles.get(i);
			if (!imageTiles.containsKey(eachTile)) {
				Image image = ImagePool.get();

				Size tileSize = new Size(eachTile.getRect().width,
						eachTile.getRect().height);
				Point centerPoint = new Point(eachTile.getRect().x
						+ eachTile.getRect().width / 2, eachTile.getRect().y
						+ eachTile.getRect().height / 2);
				LonLat center = Calc.getLonLat(centerPoint,
						context.getMaxExtent(), context.getViewSize(),
						context.getProjector().getProjection());
				Bounds extent = Calc.getExtent(center, context.getResolution(),
						tileSize, context.getProjector().getProjection());
				image.setUrl(constructUrl(extent, tileSize));
				imageTiles.put(eachTile, image);
				container.add(image);
				Point topLeft = new Point(eachTile.getRect().x,
						eachTile.getRect().y);
				fastSetElementPosition(image.getElement(), topLeft.getX(),
						topLeft.getY());
				GWT.log("put to " + topLeft.getX() + "," + topLeft.getY());
			}
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

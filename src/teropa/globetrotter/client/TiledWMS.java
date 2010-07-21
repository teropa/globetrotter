package teropa.globetrotter.client;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;

public class TiledWMS extends WMSBase {

	private final AbsolutePanel container = new AbsolutePanel();
	private final HashMap<Grid.Tile, Image> currentContents = new HashMap<Grid.Tile, Image>();
	
	private int buffer = 2;
	
	public TiledWMS(String name, String url) {
		super(name, url);
		initWidget(container);
	}
	
	public void draw() {
		Grid grid = map.getCurrentGrid();
		Bounds extent = widenToBuffer(map.getExtent());
		Set<Grid.Tile> newTiles = grid.getTiles(extent);
		removeOldTiles(newTiles);
		addNewTiles(newTiles);
	}

	private void removeOldTiles(Set<Grid.Tile> newTiles) {
		Iterator<Map.Entry<Grid.Tile, Image>> oldTileIterator = currentContents.entrySet().iterator();
		while (oldTileIterator.hasNext()) {
			Map.Entry<Grid.Tile, Image> old = oldTileIterator.next();
			if (!newTiles.contains(old.getKey())) {
				container.remove(old.getValue());
				oldTileIterator.remove();
			}
		}
	}

	private void addNewTiles(Set<Grid.Tile> newTiles) {
		for (Grid.Tile eachTile : newTiles) {
			if (!currentContents.containsKey(eachTile)) {
				Image image = new Image(constructUrl(eachTile.getExtent(), eachTile.getSize()));
				currentContents.put(eachTile, image);
				container.add(image, eachTile.getTopLeft().getX(), eachTile.getTopLeft().getY());
			}
		}
	}

	private Bounds widenToBuffer(Bounds extent) {
		if (buffer > 0) {
			Size viewportSize = map.getViewportSize();
			Size widenedSize = new Size(viewportSize.getWidth() + buffer * map.getTileSize().getWidth(), viewportSize.getHeight() + 2 * map.getTileSize().getHeight());
			Bounds widenedExtent = Calc.getExtent(map.getCenter(), map.getResolution(), widenedSize);
			return Calc.narrow(widenedExtent, map.getMaxExtent());
		} else {
			return extent;
		}
	}

	
}

package teropa.globetrotter.client;


import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;

public class TiledWMS extends WMSBase {

	private final AbsolutePanel container = new AbsolutePanel();
	
	
	public TiledWMS(String name, String url) {
		super(name, url);
		initWidget(container);
	}
	
	public void draw() {
		while (container.getWidgetCount() > 0) {
			container.remove(0);
		}
		Grid grid = map.getCurrentGrid();
		for (Grid.Tile eachTile : grid.getTiles(map.getExtent())) {
			String url = constructUrl(eachTile.getExtent(), eachTile.getSize());
			container.add(new Image(url), eachTile.getTopLeft().getX(), eachTile.getTopLeft().getY());
		}
	
	}

	
}

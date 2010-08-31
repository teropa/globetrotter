package teropa.globetrotter.client;

import java.util.Collection;

import teropa.globetrotter.client.grid.Tile;
import teropa.globetrotter.client.proj.Projection;
import teropa.globetrotter.client.proj.WGS84;

public abstract class Layer {

	private static final String ID_PREFIX = "globetrotter_layer_" + System.currentTimeMillis() + "_";
	private static int idCounter = 0;
	
	protected final String id;
	protected final String name;
	protected final boolean base;
	protected final Projection projection;
	protected Map map;

	protected boolean initialized = false;
	protected boolean visible = true;
	
	public Layer(String name, boolean base) {
		this(name, base, new WGS84());
	}
	
	public Layer(String name, boolean base, Projection projection) {
		this.name = name;
		this.base = base;
		this.projection = projection;
		this.id = ID_PREFIX + idCounter++;
	}
	
	public void init(Map map) {
		this.map = map;
		this.initialized = true;
	}

	public String getName() {
		return name;
	}

	public boolean isBase() {
		return base;
	}

	public Projection getProjection() {
		return projection;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		if (initialized) map.getView().draw(true);
	}
	
	public abstract void drawOn(View canvasView);

	public abstract void onTilesActivated(Collection<Tile> newTiles);
	public abstract void onTilesDeactivated(Collection<Tile> removedTiles);
	public abstract void onAllTilesDeactivated();
	public abstract void updateTile(Tile tile);

	

}

package teropa.globetrotter.client;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import teropa.globetrotter.client.proj.Projection;

public abstract class Layer {

	private static final String ID_PREFIX = "globetrotter_layer_" + System.currentTimeMillis() + "_";
	private static int idCounter = 0;
	
	protected final String id;
	protected final String name;
	protected final boolean base;
	protected final Projection projection;
	protected ViewContext context;

	protected boolean initialized = false;
	
	public Layer(String name, boolean base) {
		this(name, base, Projection.WGS_84);
	}
	
	public Layer(String name, boolean base, Projection projection) {
		this.name = name;
		this.base = base;
		this.projection = projection;
		this.id = ID_PREFIX + idCounter++;
	}
	
	public void init(ViewContext ctx) {
		this.context = ctx;
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

	public abstract void drawOn(CanvasView canvasView);

	public abstract void addTiles(Collection<Grid.Tile> newTiles);
	public abstract void removeTiles(Collection<Grid.Tile> removedTiles);

	

}

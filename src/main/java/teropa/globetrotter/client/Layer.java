package teropa.globetrotter.client;

import teropa.globetrotter.client.event.MapViewChangedEvent;
import teropa.globetrotter.client.proj.Projection;

import com.google.gwt.user.client.ui.Widget;

public abstract class Layer implements MapViewChangedEvent.Handler {

	private static final String ID_PREFIX = "globetrotter_layer_" + System.currentTimeMillis() + "_";
	private static int idCounter = 0;
	
	protected final String id;
	protected final String name;
	protected final boolean base;
	protected final Projection projection;
	protected ViewContext context;
	protected int zIndex;
	protected boolean visible = true;

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
	
	public void init(ViewContext ctx, int zIndex) {
		this.context = ctx;
		this.zIndex = zIndex;
		ctx.addMapViewChangedHandler(this);
		this.initialized = true;
	}

	public String getName() {
		return name;
	}

	public boolean isBase() {
		return base;
	}

	public boolean isVisible() {
		return visible;
	}
	
	public Projection getProjection() {
		return projection;
	}
	
	public final void setIsVisible(boolean visible) {
		this.visible = visible;
		onVisibilityChanged();
	}

	protected void onVisibilityChanged() {
	}

	public abstract void drawOn(CanvasView canvasView);

	

}

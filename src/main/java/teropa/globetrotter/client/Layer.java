package teropa.globetrotter.client;

import teropa.globetrotter.client.event.MapViewChangedEvent;

import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Widget;

public abstract class Layer {

	private static final String ID_PREFIX = "globetrotter_layer_" + Random.nextInt(10000) + "_";
	private static int idCounter = 0;
	
	protected final String id;
	protected String name;
	protected ViewContext context;
	protected boolean visible = true;

	protected boolean initialized = false;
	
	public Layer(String name) {
		this.name = name;
		this.id = ID_PREFIX + idCounter++;
	}
	
	public void init(ViewContext ctx) {
		this.context = ctx;
		this.initialized = true;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public final void setIsVisible(boolean visible) {
		this.visible = visible;
		this.asWidget().setVisible(visible);
		onVisibilityChanged();
	}

	protected void onVisibilityChanged() {
	}
	
	public abstract Widget asWidget();
	
	public abstract void onMapViewChanged(MapViewChangedEvent evt);
}

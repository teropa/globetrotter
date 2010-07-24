package teropa.globetrotter.client;

import teropa.globetrotter.client.event.ViewPanEndedEvent;
import teropa.globetrotter.client.event.ViewPannedEvent;
import teropa.globetrotter.client.event.ViewZoomedEvent;

import com.google.gwt.user.client.ui.Widget;

public abstract class Layer {

	protected String name;
	protected ViewContext context;
	protected boolean visible = true;

	protected boolean initialized = false;
	
	public Layer(String name) {
		this.name = name;
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
	
	public abstract void onMapPanned(ViewPannedEvent evt);
	public abstract void onMapPanEnded(ViewPanEndedEvent evt);
	public abstract void onMapZoomed(ViewZoomedEvent evt);
}

package teropa.globetrotter.client.event;

import teropa.globetrotter.client.common.Point;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;


/**
 * Used no notify layers when the current view to the map has been 
 * changed in any way.
 * 
 */
public class MapViewChangedEvent extends GwtEvent<MapViewChangedEvent.Handler> {

	public static Type<Handler> TYPE = new Type<Handler>();
	
	public static interface Handler extends EventHandler {
		public void onMapViewChanged(MapViewChangedEvent event);
	}
	
	public final boolean panned;
	public final boolean panEnded;
	public final boolean zoomed;
	public final boolean effectiveExtentChanged;
	
	public MapViewChangedEvent(boolean panned, boolean panEnded, boolean zoomed, boolean effectiveExtentChanged) {
		this.panned = panned;
		this.panEnded = panEnded;
		this.zoomed = zoomed;
		this.effectiveExtentChanged = effectiveExtentChanged;
	}
	
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}
	
	protected void dispatch(Handler handler) {
		handler.onMapViewChanged(this);
	}
	
}

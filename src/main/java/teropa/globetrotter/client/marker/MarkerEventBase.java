package teropa.globetrotter.client.marker;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public abstract class MarkerEventBase<H extends EventHandler> extends GwtEvent<H> {
	
	public final Marker marker;
	public final MarkerLayer markerLayer;
	
	public MarkerEventBase(Marker marker, MarkerLayer layer) {
		this.marker = marker;
		this.markerLayer = layer;
	}
	
}

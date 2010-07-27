package teropa.globetrotter.client.marker;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class MarkerClickEvent extends GwtEvent<MarkerClickEvent.Handler> {
	
	public static Type<Handler> TYPE = new Type<MarkerClickEvent.Handler>();
	
	public static interface Handler extends EventHandler {
		public void onMarkerClick(MarkerClickEvent evt);
	}

	public final Marker marker;
	public final MarkerLayer markerLayer;
	
	public MarkerClickEvent(Marker marker, MarkerLayer layer) {
		this.marker = marker;
		this.markerLayer = layer;
	}
	
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}
	
	protected void dispatch(Handler handler) {
		handler.onMarkerClick(this);
	}

}

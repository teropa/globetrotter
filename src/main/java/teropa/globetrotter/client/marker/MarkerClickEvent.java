package teropa.globetrotter.client.marker;

import com.google.gwt.event.shared.EventHandler;

public class MarkerClickEvent extends MarkerEventBase<MarkerClickEvent.Handler> {
	
	public static Type<Handler> TYPE = new Type<MarkerClickEvent.Handler>();
	
	public static interface Handler extends EventHandler {
		public void onMarkerClick(MarkerClickEvent evt);
	}

	public MarkerClickEvent(Marker marker, MarkerLayer layer) {
		super(marker, layer);
	}
	
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}
	
	protected void dispatch(Handler handler) {
		handler.onMarkerClick(this);
	}

}

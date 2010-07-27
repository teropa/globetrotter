package teropa.globetrotter.client.marker;

import com.google.gwt.event.shared.EventHandler;

public class MarkerDoubleClickEvent extends MarkerEventBase<MarkerDoubleClickEvent.Handler> {
	
	public static Type<Handler> TYPE = new Type<MarkerDoubleClickEvent.Handler>();
	
	public static interface Handler extends EventHandler {
		public void onMarkerDoubleClick(MarkerDoubleClickEvent evt);
	}

	public MarkerDoubleClickEvent(Marker marker, MarkerLayer layer) {
		super(marker, layer);
	}
	
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}
	
	protected void dispatch(Handler handler) {
		handler.onMarkerDoubleClick(this);
	}

}

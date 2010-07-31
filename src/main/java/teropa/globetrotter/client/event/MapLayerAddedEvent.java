package teropa.globetrotter.client.event;

import teropa.globetrotter.client.Layer;
import teropa.globetrotter.client.Map;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;


public class MapLayerAddedEvent extends GwtEvent<MapLayerAddedEvent.Handler> {

	public static Type<Handler> TYPE = new Type<Handler>();
	
	public static interface Handler extends EventHandler {
		public void onMapLayerAdded(MapLayerAddedEvent event);
	}
	
	public final Map map;
	public final Layer layer;
	
	public MapLayerAddedEvent(Map map, Layer layer) {
		this.map = map;
		this.layer = layer;
	}
	
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}
	
	protected void dispatch(Handler handler) {
		handler.onMapLayerAdded(this);
	}
	
}

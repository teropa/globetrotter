package teropa.globetrotter.client.event;

import teropa.globetrotter.client.Map;
import teropa.globetrotter.client.common.Point;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;


public class MapZoomedEvent extends GwtEvent<MapZoomedEvent.Handler> {

	public static Type<Handler> TYPE = new Type<Handler>();
	
	public static interface Handler extends EventHandler {
		public void onMapZoomed(MapZoomedEvent event);
	}
	
	public final Map map;
	public final double resolution;
	
	public MapZoomedEvent(Map map, double resolution) {
		this.map = map;
		this.resolution = resolution;
	}
	
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}
	
	protected void dispatch(Handler handler) {
		handler.onMapZoomed(this);
	}
	
}

package teropa.globetrotter.client;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;


public class ViewZoomedEvent extends GwtEvent<ViewZoomedEvent.Handler> {

	public static Type<Handler> TYPE = new Type<Handler>();
	
	public static interface Handler extends EventHandler {
		public void onViewZoomed(ViewZoomedEvent event);
	}
	
	public final Point point;
	
	public ViewZoomedEvent(Point point) {
		this.point = point;
	}
	
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}
	
	protected void dispatch(Handler handler) {
		handler.onViewZoomed(this);
	}
	
}

package teropa.globetrotter.client.event.internal;

import teropa.globetrotter.client.common.Point;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class ViewPanEvent extends GwtEvent<ViewPanEvent.Handler> {

	public static Type<Handler> TYPE = new Type<Handler>();
	
	public static interface Handler extends EventHandler {
		public void onViewPanned(ViewPanEvent event);
	}
	
	public final Point newCenterPoint;
	
	public ViewPanEvent(Point newCenterPoint) {
		this.newCenterPoint = newCenterPoint;
	}
	
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}
	
	protected void dispatch(Handler handler) {
		handler.onViewPanned(this);
	}
	
}

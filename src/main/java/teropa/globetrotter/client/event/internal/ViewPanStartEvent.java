package teropa.globetrotter.client.event.internal;

import teropa.globetrotter.client.common.Point;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class ViewPanStartEvent extends GwtEvent<ViewPanStartEvent.Handler> {

	public static Type<Handler> TYPE = new Type<Handler>();
	
	public static interface Handler extends EventHandler {
		public void onViewPanStarted(ViewPanStartEvent event);
	}
	
	public final Point centerPoint;
	
	public ViewPanStartEvent(Point newCenterPoint) {
		this.centerPoint = newCenterPoint;
	}
	
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}
	
	protected void dispatch(Handler handler) {
		handler.onViewPanStarted(this);
	}
	
}

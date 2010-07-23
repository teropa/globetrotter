package teropa.globetrotter.client.event;

import teropa.globetrotter.client.common.Point;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;


public class ViewPanEndedEvent extends GwtEvent<ViewPanEndedEvent.Handler> {

	public static Type<Handler> TYPE = new Type<Handler>();
	
	public static interface Handler extends EventHandler {
		public void onViewPanEnded(ViewPanEndedEvent event);
	}
	
	public final Point newCenterPoint;
	
	public ViewPanEndedEvent(Point newCenterPoint) {
		this.newCenterPoint = newCenterPoint;
	}
	
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}
	
	protected void dispatch(Handler handler) {
		handler.onViewPanEnded(this);
	}
	
}

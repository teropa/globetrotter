package teropa.globetrotter.client;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;


public class ViewPannedEvent extends GwtEvent<ViewPannedEvent.Handler> {

	public static Type<Handler> TYPE = new Type<Handler>();
	
	public static interface Handler extends EventHandler {
		public void onViewPanned(ViewPannedEvent event);
	}
	
	public final Point newCenterPoint;
	
	public ViewPannedEvent(Point newCenterPoint) {
		this.newCenterPoint = newCenterPoint;
	}
	
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}
	
	protected void dispatch(Handler handler) {
		handler.onViewPanned(this);
	}
	
}

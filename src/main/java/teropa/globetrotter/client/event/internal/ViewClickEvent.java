package teropa.globetrotter.client.event.internal;

import teropa.globetrotter.client.common.Point;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class ViewClickEvent extends GwtEvent<ViewClickEvent.Handler> {

	public static Type<Handler> TYPE = new Type<Handler>();
	
	public static interface Handler extends EventHandler {
		public void onViewClicked(ViewClickEvent event);
	}
	
	public final Point point;
	
	public ViewClickEvent(Point point) {
		this.point = point;
	}
	
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}
	
	protected void dispatch(Handler handler) {
		handler.onViewClicked(this);
	}
	
}

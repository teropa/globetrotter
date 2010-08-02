package teropa.globetrotter.client.controls;

import teropa.globetrotter.client.Map;
import teropa.globetrotter.client.common.LonLat;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class LocationSwitch extends Composite implements Control, ClickHandler {

	private static final int TIMEOUT = 20 * 1000;
	
	private final Button theSwitch = new Button("L");
	private Map map;
	
	public LocationSwitch() {
		initWidget(theSwitch);
	}
	
	public void init(Map map) {
		this.map = map;
		theSwitch.addClickHandler(this);
	}
	
	public void onClick(ClickEvent event) {
		getLocation(TIMEOUT);
	}
	
	public Widget asWidget() {
		return this;
	}
	
	private native boolean isLocationSupported() /*-{
		return typeof(navigator.geolocation) != 'undefined';
	}-*/;
	
	private native void getLocation(int timeout) /*-{
		var self = this;
		var onReceived = function(location) {
			self.@teropa.globetrotter.client.controls.LocationSwitch::onPositionReceived(Lteropa/globetrotter/client/controls/LocationSwitch$Position;)(location);
		};
		var onFail = function(positionError) {
			self.@teropa.globetrotter.client.controls.LocationSwitch::onPositionError(Lteropa/globetrotter/client/controls/LocationSwitch$PositionError;)(positionError);
		};
		if (navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(onReceived, onFail, {
				timeout: timeout
			});
		}
	}-*/;
	
	private void onPositionReceived(Position pos) {
		map.zoomTo(map.getResolutions().length - 1, new LonLat(pos.longitude(), pos.latitude()));
	}
	
	private void onPositionError(PositionError error) {
		GWT.log(error.toString());
	}
	
	public static class Position extends JavaScriptObject {
		protected Position() { }
		
		public final native double latitude() /*-{ return this.coords.latitude; }-*/;
		public final native double longitude() /*-{ return this.coords.longitude; }-*/;
		public final native double altitude() /*-{ return this.coords.altitude; }-*/;
		public final native double accuracy() /*-{ return this.coords.accuracy; }-*/;
		public final native double altitudeAccuracy() /*-{ return this.coords.altitudeAccuracy; }-*/;
		public final native double heading() /*-{ return this.coords.heading; }-*/;
		public final native double speed() /*-{ return this.coords.speed; }-*/;
	}

	public static class PositionError extends JavaScriptObject {
		protected PositionError() { }
		
		public final native int code() /*-{ return this.code; }-*/;
		public final native String message() /*-{ return this.message; }-*/;
		
		public final boolean isUnknown() {
			return code() == 0;
		}
		
		public final boolean isPermissionDenied() {
			return code() == 1;
		}
		
		public final boolean isPositionUnavailable() {
			return code() == 2;
		}
		
		public final boolean isTimeout() {
			return code() == 3;
		}
		
	}
}

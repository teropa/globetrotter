package teropa.globetrotter.client;

import teropa.globetrotter.client.common.Direction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;

public class KeyboardControls implements NativePreviewHandler {

	private static final int PAN_AMOUNT = 50;
	
	private final Map map;
	private final HandlerRegistration registration;
	
	public KeyboardControls(Map map) {
		this.map = map;
		this.registration = Event.addNativePreviewHandler(this);
	}
	
	public void destroy() {
		registration.removeHandler();
	}
	
	public void onPreviewNativeEvent(NativePreviewEvent event) {
		if (event.getTypeInt() == Event.ONKEYDOWN && !isModifierKeyPressed(event)) {
			switch (event.getNativeEvent().getKeyCode()) {
			case KeyCodes.KEY_UP: map.move(Direction.UP, PAN_AMOUNT); break;
			case KeyCodes.KEY_RIGHT: map.move(Direction.RIGHT, PAN_AMOUNT); break;
			case KeyCodes.KEY_DOWN: map.move(Direction.DOWN, PAN_AMOUNT); break;
			case KeyCodes.KEY_LEFT: map.move(Direction.LEFT, PAN_AMOUNT); break;
			case 61: case 43: case 187: map.zoomIn(); break;
			case 109: case 45: case 189: map.zoomOut(); break;
			}
		}
	}
	
	private boolean isModifierKeyPressed(NativePreviewEvent event) {
		NativeEvent nat = event.getNativeEvent();
		return nat.getCtrlKey() || nat.getMetaKey() || nat.getAltKey();
	}
}

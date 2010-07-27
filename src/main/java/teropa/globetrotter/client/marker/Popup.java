package teropa.globetrotter.client.marker;

import com.google.gwt.user.client.ui.Widget;

public class Popup {

	private final Widget content;
	private final Position position;
	
	public Popup(Widget content) {
		this(content, Position.TOP_CENTER);
	}
	
	public Popup(Widget content, Position position) {
		this.content = content;
		this.position = position;
	}
	
	public Widget getContent() {
		return content;
	}
	
	public Position getPosition() {
		return position;
	}

}

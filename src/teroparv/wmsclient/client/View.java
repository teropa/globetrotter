package teroparv.wmsclient.client;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class View extends Composite {
	
	private final AbsolutePanel container = new AbsolutePanel();
	private Size size;
	
	public View() {
		initWidget(container);
	}
	
	public void setSize(Size size) {
		this.size = size;
		setWidth(size.getWidth() + "px");
		setHeight(size.getHeight() + "px");
	}
	
	public Size getSize() {
		return size;
	}
	
	public void add(Widget object, Point coord) {
		container.add(object, coord.getX(), coord.getY());
	}

	public void clear() {
		while (container.getWidgetCount() > 0) {
			container.remove(0);
		}
	}

}

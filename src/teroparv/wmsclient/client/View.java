package teroparv.wmsclient.client;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class View extends Composite {
	
	private final AbsolutePanel container = new AbsolutePanel();
	
	public View() {
		initWidget(container);
	}
	
	public void add(Widget object, Point coord) {
		container.add(object, coord.getX(), coord.getY());
	}

}

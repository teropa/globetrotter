package teroparv.wmsclient.client;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;

public class View extends Composite {
	
	private final AbsolutePanel container = new AbsolutePanel();
	private Size size;
	
	public View() {
		initWidget(container);
	}
	
	public void setSize(Size size) {
		this.size = size;
		final String widthPx = size.getWidth() + "px";
		final String heightPx = size.getHeight() + "px";
		setSize(widthPx, heightPx);
		for (int i=0 ; i<container.getWidgetCount() ; i++) {
			container.getWidget(i).setSize(widthPx, heightPx);
		}
	}
	
	public Size getSize() {
		return size;
	}
	
	public void addLayer(Layer layer) {
		container.add(layer);
	}

}

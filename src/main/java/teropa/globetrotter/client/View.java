package teropa.globetrotter.client;

import com.google.gwt.user.client.ui.Widget;

import teropa.globetrotter.client.common.Size;

public class View extends AbsoluteFocusPanel {
	
	private Size size;
	
	public View() {
	}

	public void setSize(Size size) {
		this.size = size;
		final String widthPx = size.getWidth() + "px";
		final String heightPx = size.getHeight() + "px";
		setSize(widthPx, heightPx);
		for (int i=0 ; i<getWidgetCount() ; i++) {
			getWidget(i).setSize(widthPx, heightPx);
		}
	}

	public Size getSize() {
		return size;
	}

	public void addLayer(Layer layer, int zIndex) {
		Widget layerWidget = layer.asWidget();
		add(layerWidget, 0, 0);
		layerWidget.getElement().getStyle().setProperty("zIndex", ""+zIndex);
	}

	public void removeLayer(Layer theLayer) {
		remove(theLayer.asWidget());
	}

}

package teropa.globetrotter.client;

import com.google.gwt.user.client.ui.Widget;

public interface Layer {

	public void setMap(Map map);
	public void setLayers(String layers);
	public void draw(Bounds extent, Size imageSize, Point topLeft);

	public Widget asWidget();
}
package teropa.globetrotter.client;

import com.google.gwt.user.client.ui.Widget;

public interface Layer {

	public void setMap(Map map);
	public void setLayers(String layers);
	
	public void onMapPanned(ViewPannedEvent evt);
	public void onMapPanEnded(ViewPanEndedEvent evt);
	public void onMapZoomed(ViewZoomedEvent event);
	
	public Widget asWidget();

}
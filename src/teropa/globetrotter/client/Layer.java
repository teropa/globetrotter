package teropa.globetrotter.client;

import com.google.gwt.user.client.ui.Widget;

public interface Layer {

	public void setLayers(String layers);
	public void setIsVisible(boolean visible);
	public boolean isVisible();
	
	public void onMapPanned(ViewPannedEvent evt);
	public void onMapPanEnded(ViewPanEndedEvent evt);
	public void onMapZoomed(ViewZoomedEvent event);
	
	
	public Widget asWidget();


}
package teropa.globetrotter.client;

import teropa.globetrotter.client.event.ViewPanEndedEvent;
import teropa.globetrotter.client.event.ViewPannedEvent;
import teropa.globetrotter.client.event.ViewZoomedEvent;

import com.google.gwt.user.client.ui.Widget;

public interface Layer {

	public void setIsVisible(boolean visible);
	public boolean isVisible();
	
	public void onMapPanned(ViewPannedEvent evt);
	public void onMapPanEnded(ViewPanEndedEvent evt);
	public void onMapZoomed(ViewZoomedEvent event);
	
	
	public Widget asWidget();


}
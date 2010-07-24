package teropa.globetrotter.client;


import teropa.globetrotter.client.controls.Zoomer;
import teropa.globetrotter.client.marker.Marker;
import teropa.globetrotter.client.marker.MarkerLayer;
import teropa.globetrotter.client.wms.TiledWMS;
import teropa.globetrotter.client.wms.WMSBase;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

public class Demo implements EntryPoint {

	public void onModuleLoad() {
		final Map map = new Map("100%", "100%");
		
		WMSBase base = new TiledWMS("Metacarta", "http://labs.metacarta.com/wms/vmap0");
		base.setLayers("basic");
		base.setIsVisible(true);
		map.addLayer(base);
		
		RootPanel.get("container").add(map);
		
		WMSBase canada = new TiledWMS("Canada", "http://www2.dmsolutions.ca/cgi-bin/mswms_gmap");
		canada.setLayers("bathymetry,land_fn,park,drain_fn,drainage,prov_bound,fedlimit,rail,road,popplace");
		canada.setTransparent(true);
		canada.setIsVisible(false);
		map.addLayer(canada);

		MarkerLayer markers = new MarkerLayer("Capitals");
		for (DemoCities.City city : DemoCities.CITIES) {
			markers.addMarker(new Marker(city.getLonLat()));
		}
		map.addLayer(markers);
		
		map.addControl(new Zoomer(map));
		
		addControls(map, canada);
	}

	private void addControls(final Map map, final Layer canada) {
		RootPanel.get().add(new Button("+", new ClickHandler() {
			public void onClick(ClickEvent event) {
				map.zoomIn();
			}
		}));
		RootPanel.get().add(new Button("-", new ClickHandler() {
			public void onClick(ClickEvent event) {
				map.zoomOut();
			}
		}));
		RootPanel.get().add(new Button("Blame Canada", new ClickHandler() {
			public void onClick(ClickEvent event) {
				canada.setIsVisible(!canada.isVisible());
			}
		}));
	}
	
}

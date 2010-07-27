package teropa.globetrotter.client;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import teropa.globetrotter.client.controls.Zoomer;
import teropa.globetrotter.client.marker.Marker;
import teropa.globetrotter.client.marker.MarkerClickEvent;
import teropa.globetrotter.client.marker.MarkerLayer;
import teropa.globetrotter.client.wms.SingleTileWMS;
import teropa.globetrotter.client.wms.TiledWMS;
import teropa.globetrotter.client.wms.WMSBase;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

public class Demo implements EntryPoint {

	private int radarIdx = 0;
	
	public void onModuleLoad() {
		final Map map = new Map("100%", "100%");
		
		WMSBase base = new TiledWMS("Metacarta", "http://labs.metacarta.com/wms/vmap0");
		base.setLayers("basic");
		base.setIsVisible(true);
		map.addLayer(base);
		
		RootPanel.get("container").add(map);
//		
//		WMSBase canada = new TiledWMS("Canada", "http://www2.dmsolutions.ca/cgi-bin/mswms_gmap");
//		canada.setLayers("bathymetry,land_fn,park,drain_fn,drainage,prov_bound,fedlimit,rail,road,popplace");
//		canada.setTransparent(true);
//		canada.setIsVisible(false);
//		map.addLayer(canada);

		WMSBase radar = new SingleTileWMS("Radar", "http://geoservices.knmi.nl/cgi-bin/RADNL_OPER_R___25PCPRR_L3.cgi?");
		radar.setLayers("RADNL_OPER_R___25PCPRR_L3_COLOR");
		radar.setTransparent(true);
		radar.setTime(getRadarTimes().get(0));
		map.addLayer(radar);
		
		final MarkerLayer markers = new MarkerLayer("Capitals");
		for (DemoCities.City city : DemoCities.CITIES) {
			markers.addMarker(new Marker(city.getLonLat()));
		}
		markers.addMarkerClickHandler(new MarkerClickEvent.Handler() {
			public void onMarkerClick(MarkerClickEvent evt) {
//				markers.addPopup(new Popup(new Label("Hello"), Position.TOP_CENTER));
			}
		});
		map.addLayer(markers);
		
		map.addControl(new Zoomer(map));
		
		addControls(map, radar);
	}

	private void addControls(final Map map, final WMSBase radar) {
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
//		final List<String> imgs = getRadarTimes();
//		RootPanel.get().add(new Button("inc radar", new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				radarIdx++;
//				if (radarIdx > imgs.size() - 1) {
//					radarIdx = 0;
//				}
//				radar.setTime(imgs.get(radarIdx));
//			}
//		}));
		final List<String> imgs = getRadarTimes();
		final Timer t = new Timer() {
			public void run() {
				radarIdx++;
				if (radarIdx > imgs.size() - 1) {
					radarIdx = 0;
				}
				radar.setTime(imgs.get(radarIdx));				
			}
		};
		RootPanel.get().add(new Button("toggle radar", new ClickHandler() {
			public void onClick(ClickEvent event) {
				t.cancel();
				t.scheduleRepeating(100);
			}
		}));
	}
	
	private List<String> getRadarTimes() {
		List<String> result = new ArrayList<String>();
		result.add("2010-07-27 09:00:00");
		result.add("2010-07-27 09:05:00");
		result.add("2010-07-27 09:10:00");
		result.add("2010-07-27 09:15:00");
		result.add("2010-07-27 09:20:00");
		result.add("2010-07-27 09:25:00");
		result.add("2010-07-27 09:30:00");
		result.add("2010-07-27 09:35:00");
		result.add("2010-07-27 09:40:00");
		result.add("2010-07-27 09:45:00");
		result.add("2010-07-27 09:50:00");
		result.add("2010-07-27 09:55:00");		
		result.add("2010-07-27 10:00:00");
		result.add("2010-07-27 10:05:00");
		result.add("2010-07-27 10:10:00");
		result.add("2010-07-27 10:15:00");
		result.add("2010-07-27 10:20:00");
		result.add("2010-07-27 10:25:00");
		result.add("2010-07-27 10:30:00");
		result.add("2010-07-27 10:35:00");
		result.add("2010-07-27 10:40:00");
		result.add("2010-07-27 10:45:00");
		result.add("2010-07-27 10:50:00");
		result.add("2010-07-27 10:55:00");
		result.add("2010-07-27 11:00:00");
		result.add("2010-07-27 11:05:00");
		result.add("2010-07-27 11:10:00");
		result.add("2010-07-27 11:15:00");
		result.add("2010-07-27 11:20:00");
		result.add("2010-07-27 11:25:00");
		result.add("2010-07-27 11:30:00");
		result.add("2010-07-27 11:35:00");
		result.add("2010-07-27 11:40:00");
		result.add("2010-07-27 11:45:00");
		result.add("2010-07-27 11:50:00");
		result.add("2010-07-27 11:55:00");
		result.add("2010-07-27 12:00:00");
		return result;
	}
}

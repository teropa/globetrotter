package teropa.globetrotter.client;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import teropa.globetrotter.client.common.Position;
import teropa.globetrotter.client.controls.LocationSwitch;
import teropa.globetrotter.client.controls.Panner;
import teropa.globetrotter.client.controls.ScaleIndicator;
import teropa.globetrotter.client.controls.Zoomer;
import teropa.globetrotter.client.marker.Marker;
import teropa.globetrotter.client.marker.MarkerClickEvent;
import teropa.globetrotter.client.marker.MarkerLayer;
import teropa.globetrotter.client.osm.OpenStreetMapLayer;
import teropa.globetrotter.client.proj.GoogleMercator;
import teropa.globetrotter.client.wms.SingleTileWMS;
import teropa.globetrotter.client.wms.TiledWMS;
import teropa.globetrotter.client.wms.WMSBase;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class Demo implements EntryPoint {

	private int radarIdx = 0;
	
	public void onModuleLoad() {
		final Map map = new Map("100%", "100%");
		
//		map.setResolutions(new double[] {78271.51696402048, 39135.75848201024, 19567.87924100512, 9783.93962050256, 4891.96981025128, 2445.98490512564});
//		map.setMaxExtent(GoogleMercator.MAX_EXTENT);
//
//		GoogleMercator baseProj = new GoogleMercator();
//		WMSBase base = new TiledWMS("Metacarta", "http://labs.metacarta.com/wms/vmap0", true, baseProj);
//		base.setLayers("basic");
//		base.setIsVisible(true);
//		map.addLayer(base);
		
		OpenStreetMapLayer base = new OpenStreetMapLayer("http://tile.openstreetmap.org/", "Mapnik", true);
		map.addLayer(base);
		map.setResolutions(OpenStreetMapLayer.SUPPORTED_RESOLUTIONS);
		map.setMaxExtent(GoogleMercator.MAX_EXTENT);
		
		RootPanel.get("container").add(map);
//		
//		WMSBase canada = new TiledWMS("Canada", "http://www2.dmsolutions.ca/cgi-bin/mswms_gmap");
//		canada.setLayers("bathymetry,land_fn,park,drain_fn,drainage,prov_bound,fedlimit,rail,road,popplace");
//		canada.setTransparent(true);
//		canada.setIsVisible(false);
//		map.addLayer(canada);

//		WMSBase radar = new SingleTileWMS("Radar", "http://geoservices.knmi.nl/cgi-bin/RADNL_OPER_R___25PCPRR_L3.cgi?", false);
//		radar.setLayers("RADNL_OPER_R___25PCPRR_L3_COLOR");
//		radar.setTransparent(true);
//		radar.setTime(getRadarTimes().get(0));
//		radar.setIsVisible(false);
//		map.addLayer(radar);
//		
//		final MarkerLayer markers = new MarkerLayer("Capitals");
//		final HashMap<Marker, DemoCities.City> citiesByMarker = new HashMap<Marker, DemoCities.City>();
//		for (DemoCities.City city : DemoCities.CITIES) {
//			Marker marker = new Marker(city.getLonLat());
//			markers.addMarker(marker);
//			citiesByMarker.put(marker, city);
//		}
//		markers.addMarkerClickHandler(new MarkerClickEvent.Handler() {
//			public void onMarkerClick(MarkerClickEvent evt) {
//				if (evt.marker.hasPopup()) {
//					evt.marker.removePopup(); 
//				} else {
//					Label label = new Label(citiesByMarker.get(evt.marker).getName());
//					label.setStyleName("CityPopup");
//					evt.marker.setPopup(label);
//				}
//			}
//		});
//		map.addLayer(markers);
		
		map.addControl(new Panner(), Position.TOP_LEFT);
		map.addControl(new Zoomer(), Position.MIDDLE_LEFT);
		map.addControl(new ScaleIndicator(), Position.BOTTOM_LEFT);
		map.addControl(new LocationSwitch(), Position.TOP_RIGHT);
//		addControls(map, radar);
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
				radar.setIsVisible(true);
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
		result.add("2010-07-27 12:05:00");
		result.add("2010-07-27 12:10:00");
		result.add("2010-07-27 12:15:00");
		result.add("2010-07-27 12:20:00");
		result.add("2010-07-27 12:25:00");
		result.add("2010-07-27 12:30:00");
		result.add("2010-07-27 12:35:00");
		result.add("2010-07-27 12:40:00");
		result.add("2010-07-27 12:45:00");
		result.add("2010-07-27 12:50:00");
		result.add("2010-07-27 12:55:00");
		result.add("2010-07-27 13:00:00");		
		result.add("2010-07-27 13:05:00");
		result.add("2010-07-27 13:10:00");
		result.add("2010-07-27 13:15:00");
		result.add("2010-07-27 13:20:00");
		result.add("2010-07-27 13:25:00");
		result.add("2010-07-27 13:30:00");
		result.add("2010-07-27 13:35:00");
		result.add("2010-07-27 13:40:00");
		result.add("2010-07-27 13:45:00");
		result.add("2010-07-27 13:50:00");
		result.add("2010-07-27 13:55:00");
		result.add("2010-07-27 14:00:00");
		result.add("2010-07-27 14:05:00");
		result.add("2010-07-27 14:10:00");
		result.add("2010-07-27 14:15:00");
		result.add("2010-07-27 14:20:00");
		result.add("2010-07-27 14:25:00");
		result.add("2010-07-27 14:30:00");
		result.add("2010-07-27 14:35:00");
		result.add("2010-07-27 14:40:00");
		result.add("2010-07-27 14:45:00");
		result.add("2010-07-27 14:50:00");
		result.add("2010-07-27 14:55:00");
		result.add("2010-07-27 15:00:00");		
		return result;
	}
}

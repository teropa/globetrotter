package teroparv.wmsclient.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

public class Demo implements EntryPoint {

	public void onModuleLoad() {
		Map map = new Map("100%", "100%");
		
		Layer base = new Layer("Metacarta", "http://labs.metacarta.com/wms/vmap0");
		base.setLayers("basic");
		map.addLayer(base);
		
		RootPanel.get("container").add(map);
		
		map.draw();
		
		addZoomControls(map);
	}

	private void addZoomControls(final Map map) {
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
	}
	
}

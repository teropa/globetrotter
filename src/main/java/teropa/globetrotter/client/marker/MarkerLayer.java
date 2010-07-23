package teropa.globetrotter.client.marker;

import java.util.ArrayList;
import java.util.List;

import teropa.globetrotter.client.AbsoluteFocusPanel;
import teropa.globetrotter.client.Layer;
import teropa.globetrotter.client.Map;
import teropa.globetrotter.client.common.Calc;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.event.ViewPanEndedEvent;
import teropa.globetrotter.client.event.ViewPannedEvent;
import teropa.globetrotter.client.event.ViewZoomedEvent;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class MarkerLayer extends Composite implements Layer, ClickHandler, DoubleClickHandler {
	
	private final AbsoluteFocusPanel container = new AbsoluteFocusPanel();
	private final Map map;
	private final List<Marker> markers = new ArrayList<Marker>();
	private boolean positioned = false;
	
	public MarkerLayer(Map map) {
		this.map = map;
		initWidget(container);
		container.addClickHandler(this);
		container.addDoubleClickHandler(this);
	}

	public Widget asWidget() {
		return this;
	}
	
	public boolean isVisible() {
		return super.isVisible();
	}
	
	public void setIsVisible(boolean visible) {
		super.setVisible(visible);
	}
	
	public void addMarker(Marker marker) {
		markers.add(marker);
	}

	public void onMapPanned(ViewPannedEvent evt) {
		if (!positioned) {
			positionMarkers();
			positioned = true;
		}
	}

	public void onMapZoomed(ViewZoomedEvent event) {
		positioned = false;
	}

	private void positionMarkers() {
		StringBuilder markup = new StringBuilder();
		int size = markers.size();
		for (int i=0 ; i<size ; i++) {
			Marker each = markers.get(i);
			Point loc = Calc.getPoint(each.getLoc(), map.getMaxExtent(), map.getViewSize());
			each.appendMarkup(markup, String.valueOf(i), loc);
		}
		DOM.setInnerHTML(container.getElement(), markup.toString());
	}

	public void onMapPanEnded(ViewPanEndedEvent evt) { }
	
	public void onClick(ClickEvent event) {
		Integer idx = getMarkerIdx(event);
		if (idx != null) {
			event.stopPropagation();
		}
	}

	public void onDoubleClick(DoubleClickEvent event) {
		Integer idx = getMarkerIdx(event);
		if (idx != null) {
			event.stopPropagation();
		}		
	}
	
	private Integer getMarkerIdx(DomEvent<?> event) {
		Element target = Element.as(event.getNativeEvent().getEventTarget());
		String itemId = null;
		while (isBlank(itemId) && target != container.getElement()) {
			itemId = target.getId();
			if (isBlank(itemId)) {
				target = target.getParentElement();
			}
		} 
		if (isBlank(itemId)) {
			return null;
		} else {
			return Integer.valueOf(itemId);
		}
	}

	private boolean isBlank(String str) {
		return str == null || "".equals(str);
	}
}

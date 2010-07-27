package teropa.globetrotter.client.marker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import teropa.globetrotter.client.AbsoluteFocusPanel;
import teropa.globetrotter.client.Layer;
import teropa.globetrotter.client.common.Calc;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.event.MapViewChangedEvent;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Widget;

public class MarkerLayer extends Layer implements ClickHandler, DoubleClickHandler, MouseDownHandler {
	
	private final AbsoluteFocusPanel container = new AbsoluteFocusPanel();
	private final List<Marker> markers = new ArrayList<Marker>();
	private final HandlerManager handlers = new HandlerManager(this);
	
	private boolean positioned = false;
	
	private boolean shouldReplace = false;
	private final Command replaceCommand = new Command() {	
		public void execute() {
			if (shouldReplace) {
				replaceMarkers();
				shouldReplace = false;
			}
		}
	};
	
	public MarkerLayer(String name) {
		super(name);
		container.addClickHandler(this);
		container.addDoubleClickHandler(this);
		container.addMouseDownHandler(this);
	}
	
	public void addMarker(Marker marker) {
		markers.add(marker);
		marker.setLayer(this);
		shouldReplace = true;
		DeferredCommand.addCommand(replaceCommand);
	}
	
	public void addMarkers(Collection<? extends Marker> newMarkers) {
		for (Marker each : markers) {
			markers.add(each);
		}
		shouldReplace = true;
		DeferredCommand.addCommand(replaceCommand);
	}
	
	public void removeMarker(Marker marker) {
		if (positioned) {
			marker.remove();
		}
		markers.remove(marker);
		marker.setLayer(null);
	}
		
	public HandlerRegistration addMarkerClickHandler(MarkerClickEvent.Handler handler) {
		return handlers.addHandler(MarkerClickEvent.TYPE, handler);
	}
	
	public HandlerRegistration addMarkerDoubleClickHandler(MarkerDoubleClickEvent.Handler handler) {
		return handlers.addHandler(MarkerDoubleClickEvent.TYPE, handler);
	}
	
	@Override
	public void onMapViewChanged(MapViewChangedEvent evt) {
		if (evt.effectiveExtentChanged && positioned) {
			repositionMarkers();
		}
		if (evt.zoomed) {
			positioned = false;
		}
		if ((evt.zoomed || evt.panned) && !positioned) {
			replaceMarkers();
			positioned = true;
		}
	}

	private void replaceMarkers() {
		StringBuilder markup = new StringBuilder();
		int size = markers.size();
		for (int i=0 ; i<size ; i++) {
			Marker each = markers.get(i);
			if (each.hasPopup()) {
				onMarkerPopupRemove(each);
			}
			Point loc = Calc.getPoint(each.getLoc(), context.getEffectiveExtent(), context.getViewSize());
			each.appendMarkup(markup, String.valueOf(i), loc);
		}
		DOM.setInnerHTML(container.getElement(), markup.toString());
		for (int i=0 ; i<size ; i++) {
			Marker each = markers.get(i);
			if (each.hasPopup()) {
				onMarkerPopupAdded(each);
			}
		}
	}

	public void onMarkerPopupAdded(Marker marker) {
		Point point = Calc.getPoint(marker.getLoc(), context.getEffectiveExtent(), context.getViewSize());
		Point pinPoint = marker.getPinPosition().translateAroundPoint(point, marker.getSize());
		Point popupPoint = marker.getPopupPosition().translateAroundSize(pinPoint, marker.getSize());
		container.add(marker.getPopup(), popupPoint.getX(), popupPoint.getY());		
	}

	public void onMarkerPopupRemove(Marker marker) {
		container.remove(marker.getPopup());		
	}

	private void repositionMarkers() {
		int size = markers.size();
		for (int i=0 ; i<size ; i++) {
			Marker each = markers.get(i);
			Point loc = Calc.getPoint(each.getLoc(), context.getEffectiveExtent(), context.getViewSize());
			each.repositionTo(loc);
		}
	}
	
	public void onClick(ClickEvent event) {
		Integer idx = getMarkerIdx(event);
		if (idx != null) {
			event.stopPropagation();
			handlers.fireEvent(new MarkerClickEvent(markers.get(idx), this));
		}
	}

	public void onDoubleClick(DoubleClickEvent event) {
		Integer idx = getMarkerIdx(event);
		if (idx != null) {
			event.stopPropagation();
			handlers.fireEvent(new MarkerDoubleClickEvent(markers.get(idx), this));
		}		
	}
	
	public void onMouseDown(MouseDownEvent event) {
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

	@Override
	public Widget asWidget() {
		return container;
	}

	
}

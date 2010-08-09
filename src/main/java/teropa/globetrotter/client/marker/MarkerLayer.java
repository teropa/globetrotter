package teropa.globetrotter.client.marker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import teropa.globetrotter.client.AbsoluteFocusPanel;
import teropa.globetrotter.client.Layer;
import teropa.globetrotter.client.common.Calc;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.event.MapViewChangedEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Widget;

public class MarkerLayer extends Layer implements ClickHandler, DoubleClickHandler, MouseDownHandler, MouseOverHandler, EventListener {
	
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
		super(name, false);
		container.addClickHandler(this);
		container.addDoubleClickHandler(this);
		container.addMouseDownHandler(this);
		container.addMouseOverHandler(this);
	}
	
	public void addMarker(Marker marker) {
		markers.add(marker);
		marker.setLayer(this);
		shouldReplace = true;
		DeferredCommand.addCommand(replaceCommand);
	}
	
	public void addMarkers(Collection<? extends Marker> newMarkers) {
		for (Marker each : newMarkers) {
			markers.add(each);
			each.setLayer(this);
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
			Point loc = Calc.getPoint(each.getLoc(), context.getMaxExtent(), context.getViewSize(), context.getProjector().getProjection());
			each.appendMarkup(markup, getMarkerIdPrefix() + i, loc, zIndex + 1);
		}
//		final MarkerLayer self = this;
//		DeferredCommand.addCommand(new Command() {
//			public void execute() {
//				int size = markers.size();
//				for (int i=0 ; i<size ; i++) {
//					Element markerElement = Document.get().getElementById(getMarkerIdPrefix() + i);
//					Event.sinkEvents(markerElement, Event.ONMOUSEOVER | Event.ONMOUSEOUT);
//					Event.setEventListener(markerElement, self);
//				}
//			}
//		});
		DOM.setInnerHTML(container.getElement(), markup.toString());
		for (int i=0 ; i<size ; i++) {
			Marker each = markers.get(i);
			if (each.hasPopup()) {
				onMarkerPopupAdded(each);
			}
		}
	}

	public void onMarkerPopupAdded(Marker marker) {
		Point point = Calc.getPoint(marker.getLoc(), context.getMaxExtent(), context.getViewSize(), context.getProjector().getProjection());
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
			Point loc = Calc.getPoint(each.getLoc(), context.getMaxExtent(), context.getViewSize(), context.getProjector().getProjection());
			each.repositionTo(loc);
		}
	}
	
	public void onClick(ClickEvent event) {
		Integer idx = getMarkerIdx(event.getNativeEvent());
		if (idx != null) {
			event.stopPropagation();
			handlers.fireEvent(new MarkerClickEvent(markers.get(idx), this));
		}
	}

	public void onDoubleClick(DoubleClickEvent event) {
		Integer idx = getMarkerIdx(event.getNativeEvent());
		if (idx != null) {
			event.stopPropagation();
			handlers.fireEvent(new MarkerDoubleClickEvent(markers.get(idx), this));
		}		
	}
	
	public void onMouseDown(MouseDownEvent event) {
		Integer idx = getMarkerIdx(event.getNativeEvent());
		if (idx != null) {
			event.stopPropagation();
		}
	}

	public void onMouseOver(MouseOverEvent event) {
	}
	
	public void onBrowserEvent(Event event) {
		if (event.getTypeInt() == Event.ONMOUSEOVER) {
			Integer idx = getMarkerIdx(event);
			if (idx != null) {
				onMarkerMouseOver(markers.get(idx));
			}
		} else if (event.getTypeInt() == Event.ONMOUSEOUT) {
			Integer idx = getMarkerIdx(event);
			if (idx != null) {
				onMarkerMouseOut(markers.get(idx));
			}
		}
	}
	
	public void onMarkerMouseOver(Marker marker) {
		marker.setZIndex(zIndex + 2);
	}
	
	public void onMarkerMouseOut(Marker marker) {
		marker.setZIndex(zIndex + 1);
	}
	
	private Integer getMarkerIdx(NativeEvent event) {
		Element target = Element.as(event.getEventTarget());
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
			return Integer.valueOf(itemId.substring(getMarkerIdPrefix().length()));
		}
	}

	private boolean isBlank(String str) {
		return str == null || "".equals(str);
	}

	@Override
	public Widget asWidget() {
		return container;
	}

	private String getMarkerIdPrefix() {
		return id + "_marker_";
	}
}

package teropa.globetrotter.client.controls;

import teropa.globetrotter.client.AbsoluteFocusPanel;
import teropa.globetrotter.client.Map;
import teropa.globetrotter.client.event.MapZoomedEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;

public class Zoomer extends Composite implements ClickHandler, MouseDownHandler, MouseUpHandler, MouseMoveHandler, MouseOverHandler, MouseOutHandler, NativePreviewHandler, MapZoomedEvent.Handler {
	
	private static final int WIDTH = 30;
	private static final int BUTTON_HEIGHT = 38;
	private static final int NOTCH_HEIGHT = 21;

	private final ZoomerImages images = GWT.create(ZoomerImages.class);
	
	private final AbsoluteFocusPanel container = new AbsoluteFocusPanel();
	private final Image knob = images.zoomerKnob().createImage();
	private final Image zoomIn = images.zoomerIn().createImage();
	private final Image zoomOut = images.zoomerOut().createImage();
	
	private Map map;

	private boolean dragging = false;
	private int yOffset;
	private HandlerRegistration preventDefaultMouseEventsRegistration = null;
	
	public Zoomer() {
		initWidget(container);
		setStyleName("Zoomer");
		setWidth(WIDTH+"px");
	}
	
	public void init(final Map map) {
		this.map = map;
		container.addMouseDownHandler(this);
		container.addMouseUpHandler(this);
		container.addMouseMoveHandler(this);
		container.addMouseOverHandler(this);
		container.addMouseOutHandler(this);
		knob.addMouseDownHandler(this);
		zoomIn.addClickHandler(this);
		zoomOut.addClickHandler(this);
		map.addMapZoomHandler(this);
		
		zoomIn.setWidth("100%");
		zoomIn.setHeight(BUTTON_HEIGHT+"px");
		container.add(zoomIn, 0, 0);
		
		double[] res = map.getResolutions();
		int trackHeight = res.length * NOTCH_HEIGHT;
		for (int i=0 ; i < res.length ; i++) {
			int y = BUTTON_HEIGHT + i * NOTCH_HEIGHT;
			container.add(images.zoomerTrack().createImage(), 0, y);
		}
		knob.setWidth("100%");
		knob.setHeight(NOTCH_HEIGHT + "px");
		container.add(knob);
		positionKnob();
		zoomOut.setWidth("100%");
		zoomOut.setHeight(BUTTON_HEIGHT+"px");
		container.add(zoomOut, 0, BUTTON_HEIGHT + trackHeight);
		
		setHeight((2 * BUTTON_HEIGHT + trackHeight)+"px");
	}

	private void positionKnob() {
		int yInTrack = (map.getResolutions().length - map.getResolutionIndex() - 1) * NOTCH_HEIGHT;
		container.setWidgetPosition(knob, 0, BUTTON_HEIGHT + yInTrack);
	}
	
	public void onClick(ClickEvent event) {
		if (event.getSource() == zoomIn) {
			map.zoomIn();
		} else if (event.getSource() == zoomOut) {
			map.zoomOut();
		}
	}
	
	public void onMouseDown(MouseDownEvent event) {
		if (event.getSource() == knob) {
			yOffset = event.getY();
			dragging = true;
			DOM.setCapture(container.getElement());
			event.stopPropagation();
		} else if (event.getSource() == container) {
			int minY = container.getAbsoluteTop() + BUTTON_HEIGHT;
			int maxY = container.getAbsoluteTop() + BUTTON_HEIGHT + (map.getResolutions().length + 1)* NOTCH_HEIGHT;
			int evtY = event.getNativeEvent().getClientY();
			if (evtY >= minY && evtY <= maxY) {
				int relY = evtY - minY;
				int idx = map.getResolutions().length - 1 - relY / NOTCH_HEIGHT;
				if (idx < map.getResolutions().length && idx >= 0) {
					map.setResolutionIndex(idx);
				}
			}
		}
	}

	public void onMouseUp(MouseUpEvent event) {
		if (dragging) {
			int movedToY = event.getY() - yOffset;
			int minY = BUTTON_HEIGHT;
			int maxY = container.getOffsetHeight() - BUTTON_HEIGHT - NOTCH_HEIGHT;
			if (movedToY < minY) {
				movedToY = minY;
			} else if (movedToY > maxY) {
				movedToY = maxY;
			}
			double pos = ((double)movedToY - BUTTON_HEIGHT) / NOTCH_HEIGHT;
			
			int idx = (int)Math.round(map.getResolutions().length - 1 - pos);
			if (idx < map.getResolutions().length && idx >= 0) {
				map.setResolutionIndex(idx);
			}
			
			DOM.releaseCapture(container.getElement());
			dragging = false;
		}
	}
	
	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (dragging) {
			int movedToY = event.getY() - yOffset;
			int minY = BUTTON_HEIGHT;
			int maxY = container.getOffsetHeight() - BUTTON_HEIGHT - NOTCH_HEIGHT;
			if (movedToY >= minY && movedToY <= maxY) {
				container.setWidgetPosition(knob, 0, movedToY);
			} else if (movedToY < minY) {
				container.setWidgetPosition(knob, 0, minY);
			} else if (movedToY > maxY) {
				container.setWidgetPosition(knob, 0, maxY);
			}
		}
	}
	
	public void onPreviewNativeEvent(NativePreviewEvent event) {
		switch (event.getTypeInt()) {
		case Event.ONMOUSEDOWN:
		case Event.ONMOUSEMOVE:
			event.getNativeEvent().preventDefault();
		}			
	}
	
	public void onMouseOver(MouseOverEvent event) {
		preventDefaultMouseEventsRegistration = Event.addNativePreviewHandler(this);
	}
	
	public void onMouseOut(MouseOutEvent event) {
		if (preventDefaultMouseEventsRegistration != null) {
			preventDefaultMouseEventsRegistration.removeHandler();
			preventDefaultMouseEventsRegistration = null;
		}
	}

	public void onMapZoomed(MapZoomedEvent event) {
		positionKnob();
	}
	
}

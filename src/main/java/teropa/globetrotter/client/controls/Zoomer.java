package teropa.globetrotter.client.controls;

import teropa.globetrotter.client.AbsoluteFocusPanel;
import teropa.globetrotter.client.Map;
import teropa.globetrotter.client.event.MapZoomedEvent;
import teropa.globetrotter.client.util.MouseHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class Zoomer extends Composite implements Control, MouseHandler, NativePreviewHandler, MapZoomedEvent.Handler {
	
	private static final int LEFT_MARGIN = 17;
	private static final int WIDTH = 25 + LEFT_MARGIN;
	private static final int BUTTON_HEIGHT = 24;
	private static final int NOTCH_HEIGHT = 17;

	private final ZoomerImages images = GWT.create(ZoomerImages.class);
	
	private final AbsoluteFocusPanel container = new AbsoluteFocusPanel();
	private final Image knob = new Image(images.zoomerKnob());
	private final Image zoomIn = new Image(images.zoomerIn());
	private final Image zoomOut = new Image(images.zoomerOut());
	
	private Map map;
	private int numLevels;
	private int trackHeight;
	
	private boolean dragging = false;
	private int yOffset;
	private HandlerRegistration preventDefaultMouseEventsRegistration = null;
	
	public Zoomer() {
		initWidget(container);
		setStyleName("Zoomer");
		setWidth(WIDTH + "px");
	}
	
	public void init(final Map map) {
		this.map = map;
		this.numLevels = map.getResolutions().length;
		this.trackHeight = numLevels * NOTCH_HEIGHT;
		
		registerHandlers();
		initZoomIn();
		initTrack();
		initKnob();
		initZoomOut();
		
		setHeight((2 * BUTTON_HEIGHT + trackHeight) + "px");
	}

	private void initZoomIn() {
		zoomIn.setHeight(BUTTON_HEIGHT + "px");
		container.add(zoomIn, LEFT_MARGIN, 0);
	}

	private void registerHandlers() {
		container.addMouseDownHandler(this);
		container.addMouseUpHandler(this);
		container.addMouseMoveHandler(this);
		container.addMouseOverHandler(this);
		container.addMouseOutHandler(this);
		container.addMouseWheelHandler(this);
		knob.addMouseDownHandler(this);
		zoomIn.addClickHandler(this);
		zoomIn.addMouseOverHandler(this);
		zoomIn.addMouseOutHandler(this);
		zoomOut.addClickHandler(this);
		zoomOut.addMouseOverHandler(this);
		zoomOut.addMouseOutHandler(this);
		map.addMapZoomedHandler(this);
	}

	private void initTrack() {
		for (int i=0 ; i < numLevels ; i++) {
			int y = BUTTON_HEIGHT + i * NOTCH_HEIGHT;
			container.add(new Image(images.zoomerTrack()), LEFT_MARGIN, y);
		}
	}

	private void initKnob() {
		knob.setHeight(NOTCH_HEIGHT + "px");
		container.add(knob);
		positionKnob();
	}

	private void initZoomOut() {
		zoomOut.setHeight(BUTTON_HEIGHT+"px");
		container.add(zoomOut, LEFT_MARGIN, BUTTON_HEIGHT + trackHeight);
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
			startDrag(event);
		} else if (event.getSource() == container) {
			zoomToSpecificLevel(event);
		}
	}
	
	public void onMouseMove(MouseMoveEvent event) {
		if (dragging) {
			moveKnob(event);
		}
	}

	public void onMouseUp(MouseUpEvent event) {
		if (dragging) {
			endDrag(event);
		}
	}

	public void onMouseOver(MouseOverEvent event) {
		if (event.getSource() == zoomIn) {
			zoomIn.setResource(images.zoomerInOver());
		} else if (event.getSource() == zoomOut) {
			zoomOut.setResource(images.zoomerOutOver());
		} else {
			preventDefaultMouseEventsRegistration = Event.addNativePreviewHandler(this);
		}

	}
	
	public void onMouseOut(MouseOutEvent event) {
		if (event.getSource() == zoomIn) {
			zoomIn.setResource(images.zoomerIn());
		} else if (event.getSource() == zoomOut) {
			zoomOut.setResource(images.zoomerOut());
		} else {
			if (preventDefaultMouseEventsRegistration != null) {
				preventDefaultMouseEventsRegistration.removeHandler();
				preventDefaultMouseEventsRegistration = null;
			}
		}
	}

	public void onMouseWheel(MouseWheelEvent event) {
		if (event.getDeltaY() > 0) {
			map.zoomOut();
		} else {
			map.zoomIn();
		}	
	}
	
	public void onPreviewNativeEvent(NativePreviewEvent event) {
		switch (event.getTypeInt()) {
		case Event.ONMOUSEDOWN:
		case Event.ONMOUSEMOVE:
			event.getNativeEvent().preventDefault();
		}			
	}
	
	public void onMapZoomed(MapZoomedEvent event) {
		positionKnob();
	}

	private void positionKnob() {
		int yInTrack = (numLevels - map.getResolutionIndex() - 1) * NOTCH_HEIGHT;
		container.setWidgetPosition(knob, LEFT_MARGIN, BUTTON_HEIGHT + yInTrack);
	}

	private void startDrag(MouseDownEvent event) {
		yOffset = event.getY();
		dragging = true;
		DOM.setCapture(container.getElement());
		event.stopPropagation();
	}

	private void moveKnob(MouseMoveEvent event) {
		int movedToY = event.getY() - yOffset;
		int minY = BUTTON_HEIGHT;
		int maxY = container.getOffsetHeight() - BUTTON_HEIGHT - NOTCH_HEIGHT;
		if (movedToY < minY) movedToY = minY;
		if (movedToY > maxY) movedToY = maxY;
		container.setWidgetPosition(knob, LEFT_MARGIN, movedToY);
	}

	private void endDrag(MouseUpEvent event) {
		int movedToY = event.getY() - yOffset;
		int minY = BUTTON_HEIGHT;
		int maxY = container.getOffsetHeight() - BUTTON_HEIGHT - NOTCH_HEIGHT;
		if (movedToY < minY) movedToY = minY;
		if (movedToY > maxY) movedToY = maxY;
		
		int zoomLevel = getZoomLevelByY(movedToY);		
		if (zoomLevel >= 0 && zoomLevel < numLevels) {
			map.zoomTo(zoomLevel);
		}
		
		DOM.releaseCapture(container.getElement());
		dragging = false;
	}

	private void zoomToSpecificLevel(MouseDownEvent event) {
		int minY = container.getAbsoluteTop() + BUTTON_HEIGHT;
		int maxY = container.getAbsoluteTop() + BUTTON_HEIGHT + (numLevels + 1) * NOTCH_HEIGHT;
		int evtY = event.getNativeEvent().getClientY();
		if (evtY >= minY && evtY <= maxY) {
			int relativeY = evtY - container.getAbsoluteTop();
			int zoomLevel = getZoomLevelByY(relativeY);
			if (zoomLevel < numLevels && zoomLevel >= 0) {
				map.zoomTo(zoomLevel);
			}
		}
	}
	
	private int getZoomLevelByY(int y) {
		double yInTrack = (double)(y - BUTTON_HEIGHT);
		return (int)Math.round(numLevels - 1 - yInTrack / NOTCH_HEIGHT);
	}
			
	public Widget asWidget() {
		return this;
	}
}

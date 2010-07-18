package teroparv.wmsclient.client;

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
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

public class Viewport extends Composite implements MouseOverHandler, MouseOutHandler, MouseDownHandler, MouseMoveHandler, MouseUpHandler, NativePreviewHandler {

	private final AbsolutePanel container = new AbsolutePanel();
	private final FocusPanel focusPanel = new FocusPanel();
	private final Widget view;
	
	private boolean dragging = false;	
	private int xOffset;
	private int yOffset;
	
	private HandlerRegistration preventDefaultMouseEventsRegistration = null;
	
	public Viewport(Widget view) {
		this.view = view;
		initWidget(container);
		container.add(focusPanel);
		focusPanel.setWidget(view);
		focusPanel.addMouseOverHandler(this);
		focusPanel.addMouseOutHandler(this);
		focusPanel.addMouseDownHandler(this);
		focusPanel.addMouseMoveHandler(this);
		focusPanel.addMouseUpHandler(this);
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
	
	public void onMouseDown(MouseDownEvent event) {
		dragging = true;
		xOffset = event.getX();
		yOffset = event.getY();
		DOM.setCapture(focusPanel.getElement());
	}
	
	public void onMouseMove(MouseMoveEvent event) {
		if (dragging) {
			int newX = event.getX() + container.getWidgetLeft(focusPanel) - xOffset;
			int newY = event.getY() + container.getWidgetTop(focusPanel) - yOffset;
			repositionView(newX, newY);
		}
	}
	
	public void onMouseUp(MouseUpEvent event) {
		if (dragging) {
			dragging = false;
			DOM.releaseCapture(focusPanel.getElement());
			fireEvent(new ViewPannedEvent(getViewCenterPoint()));
		}
	}
	
	private void repositionView(int newX, int newY) {
		final Size size = getSize();
		final Size viewSize = getViewSize();
		if (newX > 0) {
			newX = 0;
		} else if (newX < 0 - viewSize.getWidth() + size.getWidth()) {
			newX = 0 - viewSize.getWidth() + size.getWidth();
		}
		if (newY > 0) {
			newY = 0;
		} else if (newY < 0 - viewSize.getHeight() + size.getHeight()) {
			newY = 0 - viewSize.getHeight() + size.getHeight();
		}
		
		container.setWidgetPosition(focusPanel, newX, newY);
	}

	public Point getViewTopLeftPoint() {
		return new Point(
				-container.getWidgetLeft(focusPanel), 
				-container.getWidgetTop(focusPanel));
	}
	
	public Point getViewCenterPoint() {
		final Size size = getSize();
		final Point topLeft = getViewTopLeftPoint();
		return new Point(topLeft.getX() + size.getWidth() / 2, topLeft.getY() + size.getHeight() / 2);
	}

	public Size getViewSize() {
		return new Size(view.getOffsetWidth(), view.getOffsetHeight());
	}

	public Size getSize() {
		return new Size(container.getOffsetWidth(), container.getOffsetHeight());
	}


	public void addViewPannedEventHandler(ViewPannedEvent.Handler handler) {
		addHandler(handler, ViewPannedEvent.TYPE);
	}

	public void positionView(Point newCenterPoint) {
		final Size size = getSize();
		repositionView(
				-(newCenterPoint.getX() - size.getWidth() / 2),
				-(newCenterPoint.getY() - size.getHeight() / 2));
	}
}

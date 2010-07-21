package teropa.globetrotter.client;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
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

public class Viewport extends Composite implements MouseOverHandler, MouseOutHandler, MouseDownHandler, MouseMoveHandler, MouseUpHandler, DoubleClickHandler, NativePreviewHandler {

	private final AbsolutePanel container = new AbsolutePanel();
	private final FocusPanelWithDoubleClicks focusPanel = new FocusPanelWithDoubleClicks();
	private final Widget view;
	
	private boolean dragging = false;
	private Size cachedSizeWhileDragging = null;
	private boolean hasMoved = false;
	private int movedToX;
	private int movedToY;
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
		focusPanel.addDoubleClickHandler(this);
		addStyleName("moveCursor");
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
		cachedSizeWhileDragging = getSize();
		xOffset = event.getX();
		yOffset = event.getY();
		DOM.setCapture(focusPanel.getElement());
	}
	
	public void onMouseMove(MouseMoveEvent event) {
		if (dragging) {
			hasMoved = true;
			movedToX = event.getX() + container.getWidgetLeft(focusPanel) - xOffset;
			movedToY = event.getY() + container.getWidgetTop(focusPanel) - yOffset;
			repositionView(movedToX, movedToY);
			fireEvent(new ViewPannedEvent(getViewCenterPoint()));
		}
	}
	
	public void onMouseUp(MouseUpEvent event) {
		if (dragging) {
			DOM.releaseCapture(focusPanel.getElement());
			if (hasMoved) {
				fireEvent(new ViewPanEndedEvent(getViewCenterPoint()));
				hasMoved = false;
			}
			dragging = false;
			cachedSizeWhileDragging = null;
		}
	}
	
	public void onDoubleClick(DoubleClickEvent event) {
		final int eventX = event.getNativeEvent().getClientX() - getAbsoluteLeft();
		final int eventY = event.getNativeEvent().getClientY() - getAbsoluteTop();
		final Point topLeft = getViewTopLeftPoint();
		fireEvent(new ViewZoomedEvent(new Point(topLeft.getX() + eventX, topLeft.getY() + eventY)));
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
				dragging ? -movedToX : -container.getWidgetLeft(focusPanel), 
				dragging ? -movedToY : -container.getWidgetTop(focusPanel));
	}
	
	public Point getViewCenterPoint() {
		final Size size = dragging ? cachedSizeWhileDragging : getSize();
		final int topLeftX = dragging ? -movedToX : -container.getWidgetLeft(focusPanel);
		final int topLeftY = dragging ? -movedToY : -container.getWidgetTop(focusPanel);
		return new Point(topLeftX + size.getWidth() / 2, topLeftY + size.getHeight() / 2);
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

	public void addViewPanEndedEventHandler(ViewPanEndedEvent.Handler handler) {
		addHandler(handler, ViewPanEndedEvent.TYPE);
	}

	public void addViewZoomedEventHandler(ViewZoomedEvent.Handler handler) {
		addHandler(handler, ViewZoomedEvent.TYPE);
	}

	public void positionView(Point newCenterPoint) {
		final Size size = getSize();
		repositionView(
				-(newCenterPoint.getX() - size.getWidth() / 2),
				-(newCenterPoint.getY() - size.getHeight() / 2));
	}

	private static class FocusPanelWithDoubleClicks extends FocusPanel implements HasDoubleClickHandlers {
		
		@Override
		public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
			return addDomHandler(handler, DoubleClickEvent.getType());
		}
	}

}

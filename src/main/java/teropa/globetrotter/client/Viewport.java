package teropa.globetrotter.client;

import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Position;
import teropa.globetrotter.client.common.Size;
import teropa.globetrotter.client.controls.Control;
import teropa.globetrotter.client.event.ViewPanEndedEvent;
import teropa.globetrotter.client.event.ViewPannedEvent;
import teropa.globetrotter.client.event.ViewZoomedEvent;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;

public class Viewport extends Composite implements MouseOverHandler, MouseOutHandler, MouseDownHandler, MouseMoveHandler, MouseUpHandler, DoubleClickHandler, NativePreviewHandler {

	private final AbsolutePanel container = new AbsolutePanel();
	private final Map map;
	private final View view;
	
	private boolean dragging = false;
	private boolean hasMoved = false;
	private int movedToX;
	private int movedToY;
	private int xOffset;
	private int yOffset;
	
	private HandlerRegistration preventDefaultMouseEventsRegistration = null;
	
	public Viewport(Map map, View view) {
		this.map = map;
		this.view = view;
		initWidget(container);
		setWidth("100%");
		setHeight("100%");
		container.add(view);
		view.addMouseOverHandler(this);
		view.addMouseOutHandler(this);
		view.addMouseDownHandler(this);
		view.addMouseMoveHandler(this);
		view.addMouseUpHandler(this);
		view.addDoubleClickHandler(this);
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
		DOM.setCapture(view.getElement());
	}
	
	public void onMouseMove(MouseMoveEvent event) {
		if (dragging) {
			if (!hasMoved) addStyleName("moveCursor");
			hasMoved = true;
			movedToX = event.getX() + container.getWidgetLeft(view) - xOffset;
			movedToY = event.getY() + container.getWidgetTop(view) - yOffset;
			repositionView(movedToX, movedToY);
			map.onViewPanned(new ViewPannedEvent(getViewCenterPoint()));
		}
	}
	
	public void onMouseUp(MouseUpEvent event) {
		if (dragging) {
			removeStyleName("moveCursor");
			DOM.releaseCapture(view.getElement());
			if (hasMoved) {
				map.onViewPanEnded(new ViewPanEndedEvent(getViewCenterPoint()));
				hasMoved = false;
			}
			dragging = false;
		}
	}
	
	public void onDoubleClick(DoubleClickEvent event) {
		final int eventX = event.getNativeEvent().getClientX() - getAbsoluteLeft();
		final int eventY = event.getNativeEvent().getClientY() - getAbsoluteTop();
		final Point topLeft = getViewTopLeftPoint();
		map.onViewZoomed(new ViewZoomedEvent(new Point(topLeft.getX() + eventX, topLeft.getY() + eventY), 1));
	}
	
	private void repositionView(int newX, int newY) {
		final Size viewSize = getViewSize();
		if (newX > 0) {
			newX = 0;
		} else if (newX < 0 - viewSize.getWidth() + getSize().getWidth()) {
			newX = 0 - viewSize.getWidth() + getSize().getWidth();
		}
		if (newY > 0) {
			newY = 0;
		} else if (newY < 0 - viewSize.getHeight() + getSize().getHeight()) {
			newY = 0 - viewSize.getHeight() + getSize().getHeight();
		}
		container.setWidgetPosition(view, newX, newY);
	}

	public Point getViewTopLeftPoint() {
		return new Point(
				dragging ? -movedToX : -container.getWidgetLeft(view), 
				dragging ? -movedToY : -container.getWidgetTop(view));
	}
	
	public Point getViewCenterPoint() {
		final int topLeftX = dragging ? -movedToX : -container.getWidgetLeft(view);
		final int topLeftY = dragging ? -movedToY : -container.getWidgetTop(view);
		return new Point(topLeftX + getSize().getWidth() / 2, topLeftY + getSize().getHeight() / 2);
	}

	public Size getViewSize() {
		return view.getSize();
	}

	public Size getSize() {
//		if (size == null) {
			return new Size(container.getOffsetWidth(), container.getOffsetHeight());
//		}
//		return size;
	}

	public void positionView(Point newCenterPoint) {
		repositionView(
				-(newCenterPoint.getX() - getSize().getWidth() / 2),
				-(newCenterPoint.getY() - getSize().getHeight() / 2));
	}

	public void addControl(final Control control, final Position at) {
		control.asWidget().getElement().getStyle().setProperty("zIndex", "10000");
		container.add(control.asWidget(), -100, -1000);
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				positionControl(control, at);
			}
		});
	}
	
	private void positionControl(Control control, Position at) {
		switch (at) {
		case TOP_LEFT:
			setControlPosition(control, "top", "10px");
			setControlPosition(control, "left", "10px");
			break;			
		case MIDDLE_LEFT:
			setControlPosition(control, "top", getMiddleY(control) + "px");
			setControlPosition(control, "left", "10px");
			break;
		case BOTTOM_LEFT:
			setControlPosition(control, "bottom", "10px");
			setControlPosition(control, "left", "10px");
			break;
		case TOP_CENTER:
			setControlPosition(control, "top", "10px");
			setControlPosition(control, "left", getCenterX(control) + "px");
		case MIDDLE_CENTER:
			setControlPosition(control, "top", getMiddleY(control) + "px");
			setControlPosition(control, "left", getCenterX(control) + "px");
			break;
		case BOTTOM_CENTER:
			setControlPosition(control, "bottom", "10px");			
			setControlPosition(control, "left", getCenterX(control) + "px");
			break;
		case TOP_RIGHT:
			setControlPosition(control, "top", "10px");
			setControlPosition(control, "right", "10px");
			break;			
		case MIDDLE_RIGHT:
			setControlPosition(control, "top", getMiddleY(control) + "px");
			setControlPosition(control, "right", "10px");
			break;
		case BOTTOM_RIGHT:
			setControlPosition(control, "bottom", "10px");
			setControlPosition(control, "right", "10px");
			break;
		}
	}

	private int getCenterX(Control control) {
		int portWidth = getOffsetWidth();
		int controlWidth = control.asWidget().getOffsetWidth();
		return portWidth / 2 - controlWidth / 2;
	}

	private int getMiddleY(Control control) {
		int portHeight = getOffsetHeight();
		int controlHeight = control.asWidget().getOffsetHeight();
		return portHeight / 2 - controlHeight / 2;
	}
	
	private void setControlPosition(Control control, String posDeclaration, String value) {
		control.asWidget().getElement().getStyle().setProperty(posDeclaration, value);
	}

}

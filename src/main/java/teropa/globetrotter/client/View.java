package teropa.globetrotter.client;

import teropa.globetrotter.client.common.Calc;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Size;
import teropa.globetrotter.client.event.internal.ViewClickEvent;
import teropa.globetrotter.client.event.internal.ViewPanEndEvent;
import teropa.globetrotter.client.event.internal.ViewPanEvent;
import teropa.globetrotter.client.event.internal.ViewPanHandler;
import teropa.globetrotter.client.event.internal.ViewPanStartEvent;
import teropa.globetrotter.client.util.MouseHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;

public class View extends Composite implements MouseHandler, DoubleClickHandler {

	private final MouseCanvas canvas = new MouseCanvas();
	private final Map map;
	
	private Size virtualSize;
	private Point topLeft = new Point(0, 0);
	
	private boolean dragging;
	private int xOffset;
	private int yOffset;
	
	private NativePreviewHandler preventDefaults = new NativePreviewHandler() {
		public void onPreviewNativeEvent(NativePreviewEvent event) {
			int type = event.getTypeInt();
			if (type == Event.ONMOUSEDOWN || type == Event.ONMOUSEMOVE) {
				event.getNativeEvent().preventDefault();
			}
		}
	};
	private HandlerRegistration preventDefaultsRegistration;
	
	public View(Map map) {
		this.map = map;
		canvas.addMouseOverHandler(this);
		canvas.addMouseOutHandler(this);
		canvas.addMouseDownHandler(this);
		canvas.addMouseUpHandler(this);
		canvas.addMouseMoveHandler(this);
		canvas.addClickHandler(this);
		canvas.addDoubleClickHandler(this);
		initWidget(canvas);
		setWidth("100%");
		setHeight("100%");
	}
	
	public void draw() {
		canvas.clear();
		for (Layer eachLayer : map.getLayers()) {
			eachLayer.drawOn(this);
		}
	}
	
	public Size getSize() {
		return this.virtualSize;
	}
	
	public void setSize(Size size) {
		this.virtualSize = size;
	}

	public Size getVisibleSize() {
		return new Size(getOffsetWidth(), getOffsetHeight());
	}

	public Point getTopLeft() {
		return topLeft;
	}

	public void position(Point newCenterPoint) {
		Point newTopLeft = toTopLeft(newCenterPoint);
		canvas.translate(topLeft.getX() - newTopLeft.getX(), topLeft.getY() - newTopLeft.getY());
		topLeft = newTopLeft;
	}

	public void addImage(ImageAndCoords image) {
		canvas.drawImage(image.getImage(), image.getX(), image.getY());
	}

	@Override
	protected void onLoad() {
		Size visibleSize = getVisibleSize();
		canvas.setCoordWidth(visibleSize.getWidth());
		canvas.setCoordHeight(visibleSize.getHeight());
	}
	
	public void onMouseOver(MouseOverEvent event) {
		preventDefaultsRegistration = Event.addNativePreviewHandler(preventDefaults);
	}
	
	public void onMouseOut(MouseOutEvent event) {
		if (preventDefaultsRegistration != null) {
			preventDefaultsRegistration.removeHandler();
			preventDefaultsRegistration = null;
		}
	}

	public void onMouseDown(MouseDownEvent event) {
		dragging = true;
		Event.setCapture(canvas.getElement());
		xOffset = event.getX();
		yOffset = event.getY();
		fireEvent(new ViewPanStartEvent(toCenter(topLeft)));
	}
	
	public void onMouseUp(MouseUpEvent event) {
		dragging = false;
		Event.releaseCapture(canvas.getElement());
		fireEvent(new ViewPanEndEvent(toCenter(topLeft)));
	}
	
	public void onMouseMove(MouseMoveEvent event) {
		if (dragging) {
			int wantedX = topLeft.getX() - (event.getX() - xOffset);
			int wantedY = topLeft.getY() - (event.getY() - yOffset);
			
			moveTo(wantedX, wantedY);
			
			xOffset = event.getX();
			yOffset = event.getY();
		}
	}

	public void moveTo(int newX, int newY) {
		int maxX = virtualSize.getWidth() - getVisibleSize().getWidth();
		int maxY = virtualSize.getHeight() - getVisibleSize().getHeight();
		newX = Math.min(maxX, Math.max(0, newX));
		newY = Math.min(maxY, Math.max(0, newY));
		int xDelta = topLeft.getX() - newX;
		int yDelta = topLeft.getY() - newY;

		canvas.translate(xDelta, yDelta);
		topLeft = new Point(newX, newY);

		draw();
		fireEvent(new ViewPanEvent(toCenter(topLeft)));
	}
	
	public void move(int xDelta, int yDelta) {
		int newX = topLeft.getX() + xDelta;
		int newY = topLeft.getY() + yDelta;
		moveTo(newX, newY);
	}
	
	public void onClick(ClickEvent event) {
		int x = topLeft.getX() + (event.getNativeEvent().getClientX() - getAbsoluteLeft());
		int y = topLeft.getY() + (event.getNativeEvent().getClientY() - getAbsoluteTop());
		fireEvent(new ViewClickEvent(new Point(x, y)));
	}

	public void onDoubleClick(DoubleClickEvent event) {
		int x = topLeft.getX() + (event.getNativeEvent().getClientX() - getAbsoluteLeft());
		int y = topLeft.getY() + (event.getNativeEvent().getClientY() - getAbsoluteTop());
		map.zoomIn(Calc.getLonLat(new Point(x, y), map.getMaxExtent(), map.getViewSize(), map.getProjection()));
	}
	
	public HandlerRegistration addViewPanHandler(ViewPanHandler handler) {
		final HandlerRegistration startRegistration = addHandler(handler, ViewPanStartEvent.TYPE);
		final HandlerRegistration panRegistration = addHandler(handler, ViewPanEvent.TYPE);
		final HandlerRegistration endRegistration = addHandler(handler, ViewPanEndEvent.TYPE);
		return new HandlerRegistration() {
			public void removeHandler() {
				startRegistration.removeHandler();
				panRegistration.removeHandler();
				endRegistration.removeHandler();
			}
		};
	}
	
	public HandlerRegistration addViewClickHandler(ViewClickEvent.Handler handler) {
		return addHandler(handler, ViewClickEvent.TYPE);
	}
	
	private Point toTopLeft(Point center) {
		Size visibleSize = getVisibleSize();
		return new Point(
				center.getX() - visibleSize.getWidth() / 2,
				center.getY() - visibleSize.getHeight() / 2);
	}

	private Point toCenter(Point topLeft) {
		Size visibleSize = getVisibleSize();
		return new Point(
				topLeft.getX() + visibleSize.getWidth() / 2,
				topLeft.getY() + visibleSize.getHeight() / 2);
	}

	public GWTCanvas getCanvas() {
		return canvas;
	}
	
}

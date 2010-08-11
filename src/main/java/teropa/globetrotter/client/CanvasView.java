package teropa.globetrotter.client;

import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Size;
import teropa.globetrotter.client.event.internal.ViewPanEndEvent;
import teropa.globetrotter.client.event.internal.ViewPanEvent;
import teropa.globetrotter.client.event.internal.ViewPanHandler;
import teropa.globetrotter.client.event.internal.ViewPanStartEvent;
import teropa.globetrotter.client.util.MouseHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;

public class CanvasView extends Composite implements MouseHandler {

	private final MouseCanvas canvas = new MouseCanvas();
	private final Map map;
	
	private Size virtualSize;
	private Point topLeft = new Point(0, 0);
	
	private boolean dragging;
	private int xOffset;
	private int yOffset;
	
	public CanvasView(Map map) {
		this.map = map;
		canvas.addMouseOverHandler(this);
		canvas.addMouseOutHandler(this);
		canvas.addMouseDownHandler(this);
		canvas.addMouseUpHandler(this);
		canvas.addMouseMoveHandler(this);
		canvas.addClickHandler(this);
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
		
	}
	
	public void onMouseOut(MouseOutEvent event) {
		
	}

	public void onMouseDown(MouseDownEvent event) {
		dragging = true;
		xOffset = event.getX();
		yOffset = event.getY();
		fireEvent(new ViewPanStartEvent(toCenter(topLeft)));
	}
	
	public void onMouseUp(MouseUpEvent event) {
		dragging = false;
		fireEvent(new ViewPanEndEvent(toCenter(topLeft)));
	}
	
	public void onMouseMove(MouseMoveEvent event) {
		if (dragging) {
			int minX = 0, minY = 0;
			int maxX = virtualSize.getWidth() - getVisibleSize().getWidth();
			int maxY = virtualSize.getHeight() - getVisibleSize().getHeight();
			int x = Math.min(maxX, Math.max(minX, topLeft.getX() - (event.getX() - xOffset)));
			int y = Math.min(maxY, Math.max(minY, topLeft.getY() - (event.getY() - yOffset)));
			int xDelta = topLeft.getX() - x;
			int yDelta = topLeft.getY() - y;

			canvas.translate(xDelta, yDelta);
			topLeft = new Point(x, y);

			xOffset = event.getX();
			yOffset = event.getY();
			
			fireEvent(new ViewPanEvent(toCenter(topLeft)));
			draw();
		}
	}
	
	public void onClick(ClickEvent event) {
		
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
	
}

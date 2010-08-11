package teropa.globetrotter.client;

import java.util.ArrayList;
import java.util.List;

import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Size;

import com.google.gwt.dom.client.ImageElement;
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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.widgetideas.graphics.client.ImageLoader;
import com.google.gwt.widgetideas.graphics.client.ImageLoader.CallBack;

public class CanvasView extends Composite implements MouseOverHandler, MouseOutHandler, MouseDownHandler, MouseUpHandler, MouseMoveHandler {

	private final MouseCanvas canvas = new MouseCanvas();
	private final Map map;
	
	private Size virtualSize;
	private Point topLeft = new Point(0, 0);
	private List<ImageAndCoords> currentImages = new ArrayList<CanvasView.ImageAndCoords>();
	
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
		initWidget(canvas);
	}
	
	public void draw() {
		canvas.clear();
		currentImages.clear();
		for (Layer eachLayer : map.getLayers()) {
			eachLayer.drawOn(this);
		}
	}

	public void redraw() {
		canvas.clear();
		for (ImageAndCoords each : currentImages) {
			canvas.drawImage(each.image, each.x, each.y);
		}
	}
	public Size getSize() {
		return this.virtualSize;
	}
	
	public void setSize(Size size) {
		this.virtualSize = size;
		canvas.setCoordWidth(getVisibleSize().getWidth());
		canvas.setCoordHeight(getVisibleSize().getHeight());
	}

	public Size getVisibleSize() {
		return new Size(getOffsetWidth(), getOffsetHeight());
	}

	public Point getVisibleAreaTopLeftPoint() {
		return topLeft;
	}

	public void position(Point newCenterPoint) {
		Size visibleSize = getVisibleSize();
		Point newTopLeft = new Point(
				newCenterPoint.getX() - visibleSize.getWidth() / 2,
				newCenterPoint.getY() - visibleSize.getHeight() / 2);
		canvas.translate(topLeft.getX() - newTopLeft.getX(), topLeft.getY() - newTopLeft.getY());
		topLeft = newTopLeft;
	}

	public void addImage(int x, int y, String url) {
		final double visibleX = x;
		final double visibleY = y;
		ImageLoader.loadImages(new String[] { url }, new CallBack() {
			public void onImagesLoaded(ImageElement[] imageElements) {
				canvas.drawImage(imageElements[0], visibleX, visibleY);
				currentImages.add(new ImageAndCoords(imageElements[0], visibleX, visibleY));
			}
		});
	}
	
	public void onMouseOver(MouseOverEvent event) {
		
	}
	
	public void onMouseOut(MouseOutEvent event) {
		
	}

	public void onMouseDown(MouseDownEvent event) {
		dragging = true;
		xOffset = event.getX();
		yOffset = event.getY();
	}
	
	public void onMouseUp(MouseUpEvent event) {
		dragging = false;
		draw();
	}
	
	public void onMouseMove(MouseMoveEvent event) {
		if (dragging) {
			int xDelta = event.getX() - xOffset;
			int yDelta = event.getY() - yOffset;
			topLeft = new Point(topLeft.getX() - xDelta, topLeft.getY() - yDelta);
//			GWT.log("Move "+xDelta+","+yDelta);
			canvas.translate(xDelta, yDelta);
			xOffset = event.getX();
			yOffset = event.getY();
			redraw();
		}
	}
	
	private static class ImageAndCoords {
		ImageElement image;
		double x;
		double y;
		
		public ImageAndCoords(ImageElement image, double x, double y) {
			this.image = image;
			this.x = x;
			this.y = y;
		}
		
	}
	
}

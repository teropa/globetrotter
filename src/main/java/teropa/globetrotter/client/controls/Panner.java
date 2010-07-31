package teropa.globetrotter.client.controls;

import teropa.globetrotter.client.Map;
import teropa.globetrotter.client.common.Calc;
import teropa.globetrotter.client.common.Direction;
import teropa.globetrotter.client.common.Point;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class Panner extends Composite implements Control, ClickHandler, MouseOverHandler, MouseOutHandler, MouseMoveHandler {

	private final PannerImages images = GWT.create(PannerImages.class);
	
	private final Image img = images.panner().createImage();
	private Map map;
	
	public Panner() {
		initWidget(img);
		img.addClickHandler(this);
		img.addMouseOverHandler(this);
		img.addMouseOutHandler(this);
		img.addMouseMoveHandler(this);
	}
	
	public void init(Map map) {
		this.map = map;
	}
	
	public void onClick(ClickEvent event) {
		int relX = event.getNativeEvent().getClientX() - getAbsoluteLeft();
		int relY = event.getNativeEvent().getClientY() - getAbsoluteTop();
		Direction dir = getDirection(relX, relY);
		if (dir != null) {
			map.move(dir, 50);
		}
	}

	public void onMouseOver(MouseOverEvent event) {
		highlightDirection(event.getX(), event.getY());
	}
	
	public void onMouseOut(MouseOutEvent event) {
		removeHighlight();
	}
	
	public void onMouseMove(MouseMoveEvent event) {
		highlightDirection(event.getX(), event.getY());
	}

	private void highlightDirection(int x, int y) {
		Direction dir = getDirection(x, y);
		if (dir == null) {
			removeHighlight();
		} else if (dir == Direction.UP) {
			images.pannerUp().applyTo(img);
		} else if (dir == Direction.RIGHT) {
			images.pannerRight().applyTo(img);
		} else if (dir == Direction.DOWN) {
			images.pannerDown().applyTo(img);
		} else if (dir == Direction.LEFT) {
			images.pannerLeft().applyTo(img);
		}
	}

	private Direction getDirection(int x, int y) {
		int r = getOffsetWidth() / 2;
		Point mousePoint = new Point(x, y);
		Point centerPoint = new Point(r, r);
		if (Calc.getDistance(mousePoint, centerPoint) <= r) {
			Point mouseFromCenter = new Point(mousePoint.getX() - centerPoint.getX(), centerPoint.getY() - mousePoint.getY());
			double angle = Calc.getAngle(mouseFromCenter);
			double sin = Math.sin(angle);
			double cos = Math.cos(angle);
			if (sin <= 0.5 && sin >= -0.5) {
				if (cos >= 0) {
					return Direction.RIGHT;
				} else {
					return Direction.LEFT;
				}
			} else {
				if (sin >= 0) {
					return Direction.UP;
				} else {
					return Direction.DOWN;
				}
			}
		}
		return null;
	}

	private void removeHighlight() {
		images.panner().applyTo(img);
	}

	public Widget asWidget() {
		return this;
	}
	
}


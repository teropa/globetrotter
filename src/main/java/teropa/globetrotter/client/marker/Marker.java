package teropa.globetrotter.client.marker;

import teropa.globetrotter.client.Images;
import teropa.globetrotter.client.common.LonLat;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Size;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class Marker {

	private static Images DEFAULT_IMAGES = GWT.create(Images.class);
	
	private final LonLat loc;
	private final AbstractImagePrototype imageProto;
	private final Size size;
	private final Position pinPosition;
	private final PopupProvider popupProvider;
	private Popup popup;
	
	private String domId;
	private Element element;
	
	public Marker(LonLat loc) {
		this(loc, DEFAULT_IMAGES.markerRed(), new Size(32, 32), Position.BOTTOM_LEFT, null);
	}
	
	public Marker(LonLat loc, AbstractImagePrototype image, Size size, Position pinPosition, PopupProvider popupProvider) {
		this.loc = loc;
		this.imageProto = image;
		this.size = size;
		this.pinPosition = pinPosition;
		this.popupProvider = popupProvider;
	}

	public LonLat getLoc() {
		return loc;
	}
		
	public void appendMarkup(StringBuilder builder, String domId, Point location) {
		this.domId = domId;
		this.element = null;
		Point loc = translate(location);
		builder.append("<div class=\"pointerCursor\" id=\"");
		builder.append(domId);
		builder.append("\" style=\"position: absolute; left: ");
		builder.append(loc.getX());
		builder.append("px; top: ");
		builder.append(loc.getY());
		builder.append("px;\">");
		builder.append(imageProto.getHTML());
		builder.append("</div>");
	}
	
	private Point translate(Point point) {
		switch (pinPosition) {
		case TOP_LEFT: return point;
		case TOP_CENTER: return new Point(toCenter(point), point.getY());
		case TOP_RIGHT: return new Point(toRight(point), point.getY());
		case MIDDLE_LEFT: return new Point(point.getX(), toMiddle(point));
		case MIDDLE_CENTER: return new Point(toCenter(point), toMiddle(point));
		case MIDDLE_RIGHT: return new Point(toRight(point), toMiddle(point));
		case BOTTOM_LEFT: return new Point(point.getX(), toBottom(point));
		case BOTTOM_CENTER: return new Point(toCenter(point), toBottom(point));
		case BOTTOM_RIGHT: return new Point(toRight(point), toBottom(point));
		default: return point;
		}
	}

	private int toBottom(Point point) {
		return point.getY() - size.getHeight();
	}

	private int toMiddle(Point point) {
		return point.getY() - size.getHeight() / 2;
	}

	private int toRight(Point point) {
		return point.getX() - size.getWidth();
	}

	private int toCenter(Point point) {
		return point.getX() - size.getWidth() / 2;
	}

	public void repositionTo(Point point) {
		maybeGetElement();
		Point pos = translate(point);
		Style style = this.element.getStyle();
		style.setPropertyPx("left", pos.getX());
		style.setPropertyPx("top", pos.getY());
	}

	public void remove() {
		maybeGetElement();
		this.element.getParentElement().removeChild(this.element);
	}
	
	public Popup getPopup() {
		if (this.popup == null && this.popupProvider != null) {
			this.popup = popupProvider.get(this);
		}
		return this.popup;
	}

	private void maybeGetElement() {
		if (this.element == null) {
			this.element = Document.get().getElementById(this.domId);
		}
	}
	
}

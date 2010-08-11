package teropa.globetrotter.client.marker;

import teropa.globetrotter.client.Images;
import teropa.globetrotter.client.common.LonLat;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Position;
import teropa.globetrotter.client.common.Size;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Widget;

public class Marker {

	private static Images DEFAULT_IMAGES = GWT.create(Images.class);
	
	private final LonLat loc;
	private final AbstractImagePrototype imageProto;
	private final Size size;
	private final Position pinPosition;
	private MarkerLayer layer;
	private Widget popup;
	private Position popupPosition;
	
	private String domId;
	private Element element;
	
	public Marker(LonLat loc) {
		this(loc, DEFAULT_IMAGES.markerRed(), new Size(32, 32), Position.BOTTOM_LEFT);
	}
	
	public Marker(LonLat loc, AbstractImagePrototype image, Size size, Position pinPosition) {
		this.loc = loc;
		this.imageProto = image;
		this.size = size;
		this.pinPosition = pinPosition;
	}

	public LonLat getLoc() {
		return loc;
	}

	public Position getPinPosition() {
		return pinPosition;
	}
	
	public Position getPopupPosition() {
		return popupPosition;
	}

	public Size getSize() {
		return size;
	}

	public void setLayer(MarkerLayer layer) {
		this.layer = layer;
	}
	
	public void setPopup(Widget popup) {
		setPopup(popup, Position.TOP_RIGHT);
	}
	
	public void setPopup(Widget popup, Position pos) {
		if (this.popup != null) {
			removePopup();
		}
		this.popup = popup;
		this.popupPosition = pos;
		if (layer != null) {
			layer.onMarkerPopupAdded(this);
		}
	}
	
	public Widget getPopup() {
		return popup;
	}
	
	public boolean hasPopup() {
		return popup != null;
	}
	
	public void removePopup() {
		if (layer != null) {
			layer.onMarkerPopupRemove(this);
		}
		this.popup = null;
	}
	
	public void appendMarkup(StringBuilder builder, String domId, Point location) {
		this.domId = domId;
		this.element = null;
		Point loc = pinPosition.translateAroundPoint(location, size);
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

	public void repositionTo(Point point) {
		maybeGetElement();
		if (this.element != null) {
			Point pos = pinPosition.translateAroundPoint(point, size);
			Style style = this.element.getStyle();
			style.setPropertyPx("left", pos.getX());
			style.setPropertyPx("top", pos.getY());
			if (hasPopup()) {
				Point popupPos = popupPosition.translateAroundSize(pos, size);
				Style popupStyle = getPopup().getElement().getStyle();
				popupStyle.setPropertyPx("left", popupPos.getX());
				popupStyle.setPropertyPx("top", popupPos.getY());
			}
		}
	}

	public void remove() {
		maybeGetElement();
		if (this.element != null) {
			this.element.getParentElement().removeChild(this.element);
		}
	}

	private void maybeGetElement() {
		if (this.element == null) {
			this.element = Document.get().getElementById(this.domId);
		}
	}

	public void setZIndex(int zIndex) {
		maybeGetElement();
		if (this.element != null) {
			this.element.getStyle().setProperty("zIndex", "" + zIndex);
		}
		if (this.popup != null) {
			this.popup.getElement().getStyle().setProperty("zIndex", "" + zIndex);
		}
	}
	
}

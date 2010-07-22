package teropa.globetrotter.client.marker;

import java.util.ArrayList;
import java.util.List;

import teropa.globetrotter.client.Layer;
import teropa.globetrotter.client.Map;
import teropa.globetrotter.client.common.Calc;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.event.ViewPanEndedEvent;
import teropa.globetrotter.client.event.ViewPannedEvent;
import teropa.globetrotter.client.event.ViewZoomedEvent;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class MarkerLayer extends Composite implements Layer {
	
	private final AbsolutePanel container = new AbsolutePanel();
	private final Map map;
	private final List<Marker> markers = new ArrayList<Marker>();
	private boolean positioned = false;
	
	public MarkerLayer(Map map) {
		this.map = map;
		initWidget(container);	
	}

	public Widget asWidget() {
		return this;
	}
	
	public boolean isVisible() {
		return super.isVisible();
	}
	
	public void setIsVisible(boolean visible) {
		super.setVisible(visible);
	}
	
	public void addMarker(Marker marker) {
		markers.add(marker);
		container.add(marker.getImage());
	}

	public void onMapPanned(ViewPannedEvent evt) {
		if (!positioned) {
			positionMarkers();
			positioned = true;
		}
	}

	public void onMapZoomed(ViewZoomedEvent event) {
		positioned = false;
	}

	private void positionMarkers() {
		for (Marker each : markers) {
			Point point = each.translate(Calc.getPoint(each.getLoc(), map.getMaxExtent(), map.getViewSize()));
			container.setWidgetPosition(each.getImage(), point.getX(), point.getY());
		}
	}

	public void onMapPanEnded(ViewPanEndedEvent evt) { }
	
}

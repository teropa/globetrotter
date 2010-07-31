package teropa.globetrotter.client.controls;

import teropa.globetrotter.client.Map;
import teropa.globetrotter.client.common.Calc;
import teropa.globetrotter.client.event.MapViewChangedEvent;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class ScaleIndicator extends Composite implements Control, MapViewChangedEvent.Handler {

	private static enum Unit { METRIC, IMPERIAL };
	private static final double PREFERRED_WIDTH_PX = 100.0;
	private static final double[] MAGNITUDES_KM = new double[] { 0.01, 0.1, 1.0, 10.0, 100.0, 1000.0, 10000.0 };
	private static final double[] MAGNITUDES_MI = new double[] { 0.001893939, 0.01893939, 1.0, 10.0, 100.0, 1000.0, 10000.0 };
	private static final double[] AMOUNTS = new double[] { 1.0, 2.0, 5.0 };
	private static final double ONE_FOOT_IN_METERS = 3.2808399;
	private static final double ONE_MILE_IN_FEET = 5280.0;
	
	private final FlowPanel container = new FlowPanel();
	private final Indicator metricIndicator = new Indicator(Unit.METRIC);
	private final Indicator imperialIndicator = new Indicator(Unit.IMPERIAL);
	
	private Map map;

	public ScaleIndicator() {
		initWidget(container);
	}
	
	public void init(Map map) {
		this.map = map;
		map.addMapViewChangedHandler(this);
		container.add(metricIndicator);
		container.add(imperialIndicator);
	}

	
	@Override
	public void onMapViewChanged(MapViewChangedEvent event) {
		if (event.panEnded || event.zoomed) {
			double viewWidthDeg = map.getVisibleExtent().getWidth();
			double viewWidthMeters = viewWidthDeg * Calc.getLonDegreeLengthMeters(map.getCenter());
			double viewWidthFeet = viewWidthMeters * ONE_FOOT_IN_METERS;
			int viewWidthPx = map.getViewportSize().getWidth();
			metricIndicator.adjust(viewWidthMeters / 1000, viewWidthPx);
			imperialIndicator.adjust(viewWidthFeet / ONE_MILE_IN_FEET, viewWidthPx);
		}
	}
	
	public Widget asWidget() {
		return this;
	}
	
	private class Indicator extends Widget {
		
		private final Element wrapperDiv = Document.get().createElement("div");
		private final Element markerDiv = Document.get().createElement("div");
		private final Element markerLineDiv = Document.get().createElement("div");
		private final Element labelDiv = Document.get().createElement("div");
		private final Unit unit;
		
		public Indicator(Unit unit) {
			this.unit = unit;
			wrapperDiv.appendChild(markerDiv);
			markerDiv.appendChild(markerLineDiv);
			wrapperDiv.appendChild(labelDiv);
			// TODO: Move to CSSResource once we can target 2.x
			Style markerStyle = markerDiv.getStyle();
			markerStyle.setProperty("cssFloat", "left");
			markerStyle.setPropertyPx("width", 100);
			markerStyle.setPropertyPx("height", 11);
			markerStyle.setPropertyPx("marginTop", 3);
			markerStyle.setProperty("borderWidth", "0 1px");
			markerStyle.setProperty("borderColor", "black");
			markerStyle.setProperty("borderStyle", "solid");
			
			Style markerLineStyle = markerLineDiv.getStyle();
			markerLineStyle.setProperty("width", "100%");
			markerLineStyle.setPropertyPx("height", 1);
			markerLineStyle.setPropertyPx("marginTop", 5);
			markerLineStyle.setProperty("borderTop", "1px solid black");
			
			Style labelStyle = labelDiv.getStyle();
			labelStyle.setProperty("cssFloat", "left");
			labelStyle.setPropertyPx("marginLeft", 5);
			labelStyle.setPropertyPx("marginTop", 1);
			labelStyle.setProperty("fontSize", "0.8em");
			
			setElement(wrapperDiv);		
		}
		
		public void adjust(double viewWidthUnits, int viewWidthPx) {
			double nearestDist = Integer.MAX_VALUE;
			double nearestUnits = Integer.MAX_VALUE;
			double nearestPx = Integer.MAX_VALUE;
			double[] magnitudes = getMagnitudes();
			for (int m = 0 ; m < magnitudes.length ; m++) {
				for (int a = 0 ; a < AMOUNTS.length ; a++) {
					double units = magnitudes[m] * AMOUNTS[a];
					double px = (units / viewWidthUnits) * viewWidthPx;
					double dist = Math.abs(PREFERRED_WIDTH_PX - px);
					if (dist < nearestDist) {
						nearestUnits = units;
						nearestDist = dist;
						nearestPx = px;
					}
				}
			}
			markerDiv.getStyle().setPropertyPx("width", (int)Math.round(nearestPx));
			labelDiv.setInnerText(formatUnits(nearestUnits));
		}
		
		private double[] getMagnitudes() {
			switch (unit) {
			case METRIC: return MAGNITUDES_KM;
			case IMPERIAL: return MAGNITUDES_MI;
			}
			return null;
		}

		private String formatUnits(double units) {
			switch(unit) {
			case METRIC:
				if (units >= 1) {
					return "" + (int)Math.round(units) + " km";
				} else {
					return (int)Math.round(units * 1000) + " m";
				}
			case IMPERIAL:
				if (units >= 1) {
					return "" + (int)Math.round(units) + " mi";
				} else {
					return (int)Math.round(units * ONE_MILE_IN_FEET) + " ft";
				}
			}
			return null;
		}

	}
	
}

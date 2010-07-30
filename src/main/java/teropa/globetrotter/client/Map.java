package teropa.globetrotter.client;

import static teropa.globetrotter.client.common.Calc.getLonLat;
import static teropa.globetrotter.client.common.Calc.getPixelSize;
import static teropa.globetrotter.client.common.Calc.getPoint;
import static teropa.globetrotter.client.common.Calc.narrow;

import java.util.ArrayList;
import java.util.List;

import teropa.globetrotter.client.common.Bounds;
import teropa.globetrotter.client.common.Calc;
import teropa.globetrotter.client.common.LonLat;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Size;
import teropa.globetrotter.client.controls.Zoomer;
import teropa.globetrotter.client.event.MapViewChangedEvent;
import teropa.globetrotter.client.event.ViewPanEndedEvent;
import teropa.globetrotter.client.event.ViewPannedEvent;
import teropa.globetrotter.client.event.ViewZoomedEvent;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Composite;

public class Map extends Composite implements ViewContext, ViewPannedEvent.Handler, ViewPanEndedEvent.Handler, ViewZoomedEvent.Handler {

	private final View view = new View();
	private final Viewport viewport = new Viewport(this, view);
	private final List<Layer> layers = new ArrayList<Layer>();
	
	private Bounds maxExtent = new Bounds(-180, -90, 180, 90);
	private Bounds effectiveExtent = maxExtent;
	private LonLat center = new LonLat(0, 0);
	private double[] resolutions = new double[] { 1.0, 0.5, 0.2, 0.1, 0.05, 0.02, 0.01, 0.005 };
	private int resolutionIndex = 4;
	private boolean drawn;
	
	private Size tileSize = new Size(256, 256);
	
	// A different grid for each resolution
	private Grid[] grids;
	
	public Map(String width, String height) {
		initWidget(viewport);
		setWidth(width);
		setHeight(height);
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				init();
			}
		});
		grids = new Grid[resolutions.length];
	}
	
	private void init() {
		setEffectiveExtent(false);
		adjustViewAndViewportSize();
	}
	
	public void addLayer(Layer layer) {
		int zIndex = layers.size() * 100;
		layer.init(this, zIndex);
		layers.add(layer);
		view.addLayer(layer, zIndex);
	}
	
	public Layer getLayerByName(String name) {
		for (Layer each : layers) {
			if (name.equals(each.getName())) {
				return each;
			}
		}
		return null;
	}
	
	public void removeLayer(String name) {
		Layer theLayer = getLayerByName(name);
		if (theLayer != null) {
			layers.remove(theLayer);
			view.removeLayer(theLayer);
		}
	}
	
	public void setCenter(LonLat center) {
		this.center = center;
	}

	public void setResolutions(double[] resolutions) {
		this.resolutions = resolutions;
		this.grids = new Grid[resolutions.length];
	}
	
	public double[] getResolutions() {
		return resolutions;
	}
	
	public void setResolutionIndex(int index) {
		resizeView(index - this.resolutionIndex);
	}
	
	public Size getTileSize() {
		return tileSize;
	}

	public Size getViewSize() {
		return view.getSize();
	}
	
	public Bounds getMaxExtent() {
		return maxExtent;
	}
	
	public void setMaxExtent(Bounds maxExtent) {
		this.maxExtent = maxExtent;
		this.effectiveExtent = maxExtent;
	}
	
	public Bounds getEffectiveExtent() {
		return effectiveExtent;
	}

	public Bounds getVisibleExtent() {
		return narrow(Calc.getExtent(center, resolutions[resolutionIndex], getViewportSize()), effectiveExtent);
	}

	public double getResolution() {
		return resolutions[resolutionIndex];
	}

	public Size getViewportSize() {
		return viewport.getSize();
	}

	public Point getViewportLocation() {
		return viewport.getViewTopLeftPoint();
	}
	
	public LonLat getCenter() {
		return center;
	}

	public Point getViewCenterPoint() {
		return Calc.getPoint(getCenter(), getEffectiveExtent(), getViewSize());
	}
	
	public Grid getGrid() {
		if (grids[resolutionIndex] == null) {
			grids[resolutionIndex] = new Grid(this, getTileSize(), getMaxExtent(), getEffectiveExtent(), getResolution());
		}
		return grids[resolutionIndex];
	}
	
	public void zoomIn() {
		resizeView(1);
	}
	
	public void zoomOut() {
		resizeView(-1);
	}

	private void notifyLayers(MapViewChangedEvent evt) {
		for (Layer eachLayer : layers) {
			eachLayer.onMapViewChanged(evt);
		}
	}

	public void onViewPanEnded(ViewPanEndedEvent event) {
		if (effectiveExtent.getArea() < maxExtent.getArea()) {
			setEffectiveExtent(true);
			notifyLayers(new MapViewChangedEvent(false, true, false, true));
		} else {
			notifyLayers(new MapViewChangedEvent(false, true, false, false));
		}
	}
	
	public void onViewPanned(ViewPannedEvent event) {
		setCenter(getLonLat(event.newCenterPoint, effectiveExtent, view.getSize()));
		notifyLayers(new MapViewChangedEvent(true, false, false, false));
	}
	
	public void onViewZoomed(ViewZoomedEvent event) {
		LonLat pointedAt = getLonLat(event.point, effectiveExtent, view.getSize());
		resizeView(event.levels, pointedAt);
	}

	private boolean newResolutionInBounds(int delta) {
		if (delta > 0) {
			return resolutionIndex + delta < resolutions.length;
		} else {
			return resolutionIndex + delta >= 0;
		}
	}
	
	private void resizeView(int delta) {
		if (newResolutionInBounds(delta)) {
			resolutionIndex += delta;
		}
		setEffectiveExtent(false);
		adjustViewAndViewportSize();
		notifyLayers(new MapViewChangedEvent(false, false, true, false));
	}
	
	private void resizeView(int delta, LonLat newCenter) {
		if (newResolutionInBounds(delta)) {
			resolutionIndex += delta;
		}
		setCenter(newCenter);
		setEffectiveExtent(false);
		adjustViewAndViewportSize();
		notifyLayers(new MapViewChangedEvent(true, true, true, false));
	}

	private void setEffectiveExtent(boolean position) {
		this.effectiveExtent = Calc.getEffectiveExtent(maxExtent, getResolution(), getCenter());
		if (grids[resolutionIndex] != null) {
			grids[resolutionIndex].setEffectiveExtent(effectiveExtent);
		}
		if (position) {
			adjustViewAndViewportSize();
		}
	}

	private void adjustViewAndViewportSize() {
		view.setSize(getPixelSize(effectiveExtent, resolutions[resolutionIndex]));
		viewport.positionView(getPoint(center, effectiveExtent, view.getSize()));
	}

	public String getSRS() {
		return "EPSG:4326";
	}
	
	public boolean isDrawn() {
		return drawn;
	}

	public void addControl(Zoomer zoomer) {
		viewport.addControl(zoomer);
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				drawn = true;
				notifyLayers(new MapViewChangedEvent(true, true, true, false));
			}
		});
	}


}


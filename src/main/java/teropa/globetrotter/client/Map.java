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
				resizeView();				
			}
		});
		grids = new Grid[resolutions.length];
	}
	
	public void addLayer(Layer layer) {
		layer.init(this);
		layers.add(layer);
		view.addLayer(layer);
	}
	
	public void setCenter(LonLat center) {
		this.center = center;
	}

	public void setResolutions(double[] resolutions) {
		this.resolutions = resolutions;
	}
	
	public double[] getResolutions() {
		return resolutions;
	}
	
	public void setResolutionIndex(int index) {
		this.resolutionIndex = index;
		resizeView();
		notifyLayers(new MapViewChangedEvent(false, false, true));
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
	}

	public Bounds getExtent() {
		return narrow(Calc.getExtent(center, resolutions[resolutionIndex], getViewportSize()), maxExtent);
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
		return Calc.getPoint(getCenter(), getMaxExtent(), getViewSize());
	}
	
	public Grid getGrid() {
		if (grids[resolutionIndex] == null) {
			grids[resolutionIndex] = new Grid(getViewSize(), getTileSize(), getMaxExtent(), getResolution());
		}
		return grids[resolutionIndex];
	}
	
	public void zoomIn() {
		if (newResolutionInBounds(1)) {
			resolutionIndex++;
			resizeView();
		}
		notifyLayers(new MapViewChangedEvent(false, false, true));
	}
	
	public void zoomOut() {
		if (newResolutionInBounds(-1)) {
			resolutionIndex--;
			resizeView();
		}
		notifyLayers(new MapViewChangedEvent(false, false, true));
	}

	private void notifyLayers(MapViewChangedEvent evt) {
		for (Layer eachLayer : layers) {
			eachLayer.onMapViewChanged(evt);
		}
	}

	public void onViewPanEnded(ViewPanEndedEvent event) {
		notifyLayers(new MapViewChangedEvent(false, true, false));
	}
	
	public void onViewPanned(ViewPannedEvent event) {
		setCenter(getLonLat(event.newCenterPoint, maxExtent, view.getSize()));
		notifyLayers(new MapViewChangedEvent(true, false, false));
	}
	
	public void onViewZoomed(ViewZoomedEvent event) {
		LonLat pointedAt = getLonLat(event.point, maxExtent, view.getSize());
		if (newResolutionInBounds(event.levels)) {
			resolutionIndex += event.levels;
			resizeView(pointedAt);
		}
		notifyLayers(new MapViewChangedEvent(true, true, true));
	}

	private boolean newResolutionInBounds(int delta) {
		if (delta > 0) {
			return resolutionIndex + delta < resolutions.length;
		} else {
			return resolutionIndex + delta >= 0;
		}
	}
	
	private void resizeView() {
		view.setSize(getPixelSize(maxExtent, resolutions[resolutionIndex]));
		viewport.positionView(getPoint(center, maxExtent, view.getSize()));
		setCenter(getLonLat(getPoint(center, maxExtent, view.getSize()), maxExtent, view.getSize()));
	
	}
	
	private void resizeView(LonLat newCenter) {
		view.setSize(getPixelSize(maxExtent, resolutions[resolutionIndex]));
		setCenter(newCenter);
		viewport.positionView(getPoint(center, maxExtent, view.getSize()));
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
				notifyLayers(new MapViewChangedEvent(true, true, true));
			}
		});
	}


}


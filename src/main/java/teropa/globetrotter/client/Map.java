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
import teropa.globetrotter.client.event.ViewPanEndedEvent;
import teropa.globetrotter.client.event.ViewPannedEvent;
import teropa.globetrotter.client.event.ViewZoomedEvent;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Composite;

public class Map extends Composite implements ViewPannedEvent.Handler, ViewPanEndedEvent.Handler, ViewZoomedEvent.Handler {

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
		onViewZoomed(new ViewZoomedEvent(viewport.getViewCenterPoint(), 1));
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

	public Grid getCurrentGrid() {
		if (grids[resolutionIndex] == null) {
			grids[resolutionIndex] = new Grid(getViewSize(), getTileSize(), getMaxExtent(), getResolution());
		}
		return grids[resolutionIndex];
	}
	
	public void zoomIn() {
		onViewZoomed(new ViewZoomedEvent(viewport.getViewCenterPoint(), 1));
	}
	
	public void zoomOut() {
		onViewZoomed(new ViewZoomedEvent(viewport.getViewCenterPoint(), -1));
	}

	private void draw() {
		ViewPannedEvent evt = new ViewPannedEvent(viewport.getViewCenterPoint());
		drawn = true;
		notifyLayersMapPanned(evt);
	}

	private void notifyLayersMapPanned(ViewPannedEvent evt) {
		for (Layer eachLayer : layers) {
			eachLayer.onMapPanned(evt);
		}
	}

	public void onViewPanEnded(ViewPanEndedEvent event) {
		for (Layer eachLayer : layers) {
			eachLayer.onMapPanEnded(event);
		}	
	}
	
	public void onViewPanned(ViewPannedEvent event) {
		setCenter(getLonLat(event.newCenterPoint, maxExtent, view.getSize()));
		notifyLayersMapPanned(event);
	}
	
	public void onViewZoomed(ViewZoomedEvent event) {
		LonLat pointedAt = getLonLat(event.point, maxExtent, view.getSize());
		if (newResolutionInBounds(event)) {
			resolutionIndex += event.levels;
			resizeView(pointedAt);
		}
		for (Layer eachLayer : layers) {
			eachLayer.onMapZoomed(event);
		}
		draw();
	}

	private boolean newResolutionInBounds(ViewZoomedEvent event) {
		if (event.levels > 0) {
			return resolutionIndex + event.levels < resolutions.length;
		} else {
			return resolutionIndex + event.levels >= 0;
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
				draw();
			}
		});
	}


}


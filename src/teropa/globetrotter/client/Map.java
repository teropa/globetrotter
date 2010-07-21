package teropa.globetrotter.client;

import static teropa.globetrotter.client.Calc.getLonLat;
import static teropa.globetrotter.client.Calc.getPixelSize;
import static teropa.globetrotter.client.Calc.getPoint;
import static teropa.globetrotter.client.Calc.narrow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Composite;

public class Map extends Composite implements ViewPannedEvent.Handler, ViewPanEndedEvent.Handler, ViewZoomedEvent.Handler {

	private final View view = new View();
	private final Viewport viewport = new Viewport(view);
	private final List<Layer> layers = new ArrayList<Layer>();
	
	private Bounds maxExtent = new Bounds(-180, -90, 180, 90);
	private LonLat center = new LonLat(0, 0);
	private double[] resolutions = new double[] { 1.0, 0.5, 0.2, 0.1, 0.05, 0.02, 0.01, 0.005 };
	private int resolutionIndex = 4;
	
	private Size tileSize = new Size(256, 256);
	
	// A different grid for each resolution
	private final HashMap<Integer, Grid> grids = new HashMap<Integer, Grid>();
	
	public Map(String width, String height) {
		initWidget(viewport);
		setWidth(width);
		setHeight(height);
		viewport.addViewPannedEventHandler(this);
		viewport.addViewPanEndedEventHandler(this);
		viewport.addViewZoomedEventHandler(this);
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				resizeView();				
			}
		});
	}
	
	public void addLayer(Layer layer) {
		layers.add(layer);
		layer.setMap(this);
		view.addLayer(layer);
	}
	
	public void setCenter(LonLat center) {
		this.center = center;
	}

	public void setResolutionIndex(int index) {
		this.resolutionIndex = index;
		resizeView();
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
		if (grids.get(resolutionIndex) == null) {
			grids.put(resolutionIndex, new Grid(getViewSize(), getTileSize(), getMaxExtent(), getResolution()));
		}
		return grids.get(resolutionIndex);
	}
	
	public void zoomIn() {
		if (resolutionIndex < resolutions.length - 1) {
			resolutionIndex++;
			resizeView();
			draw();	
		}
	}
	
	public void zoomOut() {
		if (resolutionIndex > 0) {
			resolutionIndex--;
			resizeView();
			draw();	
		}
	}

	public void draw() {
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				for (Layer eachLayer : layers) {
					eachLayer.onMapPanned();
				}				
			}
		});
	}

	public void onViewPanEnded(ViewPanEndedEvent event) {
		for (Layer eachLayer : layers) {
			eachLayer.onMapPanEnded();
		}	
	}
	
	public void onViewPanned(ViewPannedEvent event) {
		setCenter(getLonLat(event.newCenterPoint, maxExtent, view.getSize()));
		draw();
	}
	
	public void onViewZoomed(ViewZoomedEvent event) {
		LonLat pointedAt = getLonLat(event.point, maxExtent, view.getSize());
		if (resolutionIndex < resolutions.length - 1) {
			resolutionIndex++;
			resizeView(pointedAt);
		}
		draw();
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

	

}

// bbox, size, resolution

// extent -> näkyvillä oleva osa kartasta
// size -> viewportin ikkuna, bboxin mahduttava tähän
// view size > extentin ja maxextentin suhteen mukaan
// resolution = map units per pixel

package teropa.globetrotter.client;


import java.util.ArrayList;
import java.util.List;

import teropa.globetrotter.client.common.Bounds;
import teropa.globetrotter.client.common.Calc;
import teropa.globetrotter.client.common.Direction;
import teropa.globetrotter.client.common.LonLat;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Position;
import teropa.globetrotter.client.common.Rectangle;
import teropa.globetrotter.client.common.Size;
import teropa.globetrotter.client.controls.Control;
import teropa.globetrotter.client.event.MapZoomedEvent;
import teropa.globetrotter.client.event.internal.ViewPanEvent;
import teropa.globetrotter.client.proj.Projection;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;

public class Map extends Composite implements ViewContext, ViewPanEvent.Handler {

	private final AbsolutePanel container = new AbsolutePanel();
	private final View view = new View(this);
	private final Calc calc = new Calc(this);
	private final List<Layer> layers = new ArrayList<Layer>();
	private Layer baseLayer;
	
	private Bounds maxExtent = new Bounds(-180, -90, 180, 90);
	private LonLat center = new LonLat(0, 0);
	private double[] resolutions = new double[] { 1.0, 0.5, 0.2, 0.1, 0.05, 0.02, 0.01, 0.005 };
	private int resolutionIndex = 4;
	
	private Size tileSize = new Size(256, 256);
	
	private Grid grid;
	
	public Map(String width, String height) {
		initWidget(container);
		setWidth(width);
		setHeight(height);
		view.addViewPanHandler(this);
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				init();
			}
		});
	}
	
	private void init() {
		container.add(view);
		Size fullSize = calc.getVirtualPixelSize();
		Point centerPoint = calc.getPoint(center);
		view.position(centerPoint);
		getGrid().init(fullSize);
		view.draw();
	}
	
	public void addLayer(Layer layer) {
		layer.init(this);
		layers.add(layer);
		if (layer.isBase()) {
			baseLayer = layer;
		}
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
		}
	}
	
	public void setCenter(LonLat center) {
		this.center = center;
	}
	
	public Projection getProjection() {
		if (baseLayer != null) {
			return baseLayer.getProjection();
		} else {
			return Projection.WGS_84;
		}
	}

	public void setResolutions(double[] resolutions) {
		this.resolutions = resolutions;
	}
	
	public double[] getResolutions() {
		return resolutions;
	}
	
	public void zoomTo(int resolutionIndex) {
		resizeView(resolutionIndex - this.resolutionIndex);
	}
	
	public void zoomTo(int resolutionIndex, LonLat point) {
		resizeView(resolutionIndex - this.resolutionIndex, point);
	}

	public int getResolutionIndex() {
		return this.resolutionIndex;
	}

	public Size getTileSize() {
		return tileSize;
	}

	public View getView() {
		return view;
	}
	
	public Bounds getMaxExtent() {
		return maxExtent;
	}
	
	public void setMaxExtent(Bounds maxExtent) {
		this.maxExtent = maxExtent;
	}
	
	public double getResolution() {
		return resolutions[resolutionIndex];
	}

	public Point getViewportLocation() {
		return view.getTopLeft();
	}
	
	public LonLat getCenter() {
		return center;
	}

	public Point getViewCenterPoint() {
		return calc.getPoint(getCenter());
	}
	
	public Grid getGrid() {
		if (grid == null) {
			grid = new Grid(getTileSize(), this);
		}
		return grid;
	}
	
	public Rectangle getVisibleRectangle() {
		Size portSize = view.getVisibleSize();
		Point topLeft = getViewportLocation();
		return new Rectangle(topLeft.getX(), topLeft.getY(), portSize.getWidth(), portSize.getHeight());
	}
	
	public void zoomIn() {
		resizeView(1);
	}

	public void zoomIn(LonLat newCenter) {
		resizeView(1, newCenter);
	}

	public void zoomOut() {
		resizeView(-1);
	}

	public void onViewPanned(ViewPanEvent event) {
		setCenter(calc.getLonLat(event.newCenterPoint));
	}

	public void move(Direction dir, int amountPx) {
		switch(dir) {
		case RIGHT: view.move(amountPx, 0); break;
		case DOWN: view.move(0, amountPx); break;
		case LEFT: view.move(-amountPx, 0); break;
		case UP: view.move(0, -amountPx); break;
		}
	}

	private boolean newResolutionInBounds(int delta) {
		if (delta > 0) {
			return resolutionIndex + delta < resolutions.length;
		} else {
			return resolutionIndex + delta >= 0;
		}
	}
	
	private void resizeView(int delta) {
		resizeView(delta, center);
	}
	
	private void resizeView(int delta, LonLat newCenter) {
		int prevResolutionIndex = resolutionIndex;
		LonLat prevCenter = center;
		if (newResolutionInBounds(delta)) {
			resolutionIndex += delta;
		}
		setCenter(newCenter);
		adjustView(resolutions[prevResolutionIndex], resolutions[resolutionIndex], prevCenter, newCenter);
		fireEvent(new MapZoomedEvent());
	}

	private void adjustView(double fromRes, double toRes, LonLat fromCenter, LonLat toCenter) {
		final double resScale = fromRes / toRes;
		
		double width = getView().getVisibleSize().getWidth();
		double height = getView().getVisibleSize().getHeight();

		Point fromCenterPoint = calc().getPoint(fromCenter);
		Point toCenterPoint = calc().getPoint(toCenter);
		double xMove = fromCenterPoint.getX() - toCenterPoint.getX();
		double yMove = fromCenterPoint.getY() - toCenterPoint.getY();
		
		view.getCanvas().saveContext();		
		if (resScale >= 1) {
			view.getCanvas().translate(
					-view.getTopLeft().getX() - width / resScale + xMove,
					-view.getTopLeft().getY() - height / resScale + yMove);		
			view.getCanvas().scale(resScale, resScale);
		} else {
			view.getCanvas().scale(resScale, resScale);
			view.getCanvas().translate(
					view.getTopLeft().getX() + width - width * resScale,
					view.getTopLeft().getY() + height - height * resScale);
		}
		view.draw();
		
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				view.getCanvas().restoreContext();
				Size fullSize = calc.getVirtualPixelSize();
				Point centerPoint = calc.getPoint(center);
				view.position(centerPoint);
				getGrid().init(fullSize);
				view.draw();								
			}
		});
	}

	public void addControl(Control control, Position at) {
		control.init(this);
		switch (at) {
		case TOP_LEFT: container.add(control.asWidget(), 10, 10); break;
		case MIDDLE_LEFT: container.add(control.asWidget(), 10, 100); break;
		}
	}

	public List<Layer> getLayers() {
		return layers;
	}

	public Calc calc() {
		return calc;
	}
	
	public void addMapZoomedHandler(MapZoomedEvent.Handler handler) {
		addHandler(handler, MapZoomedEvent.TYPE);
	}

}


package teropa.globetrotter.client;

import static teropa.globetrotter.client.common.Calc.getLonLat;
import static teropa.globetrotter.client.common.Calc.getPixelSize;
import static teropa.globetrotter.client.common.Calc.getPoint;
import static teropa.globetrotter.client.common.Calc.narrow;

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
import teropa.globetrotter.client.event.MapLayerAddedEvent;
import teropa.globetrotter.client.event.MapViewChangedEvent;
import teropa.globetrotter.client.event.MapZoomedEvent;
import teropa.globetrotter.client.event.ViewPanEndedEvent;
import teropa.globetrotter.client.event.ViewPannedEvent;
import teropa.globetrotter.client.event.ViewZoomedEvent;
import teropa.globetrotter.client.proj.Projection;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Composite;

public class Map extends Composite implements ViewContext, ViewPannedEvent.Handler, ViewPanEndedEvent.Handler, ViewZoomedEvent.Handler {

	private final Projector projector = new Projector(this);
	private final View view = new View();
	private final Viewport viewport = new Viewport(this, view);
	private final List<Layer> overlays = new ArrayList<Layer>();
	private Layer baseLayer;
	private final HandlerManager mapEvents = new HandlerManager(this);
	
	private Bounds maxExtent = new Bounds(-180, -90, 180, 90);
	private LonLat center = new LonLat(0, 0);
	private double[] resolutions = new double[] { 1.0, 0.5, 0.2, 0.1, 0.05, 0.02, 0.01, 0.005 };
	private int resolutionIndex = 4;
	private boolean drawn;
	
	private Size tileSize = new Size(256, 256);
	
	private Grid grid;
	
	public Map(String width, String height) {
		initWidget(viewport);
		setWidth(width);
		setHeight(height);
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				init();
			}
		});
	}
	
	private void init() {
		adjustViewAndViewportSize();
	}
	
	public Projector getProjector() {
		return projector;
	}
	
	public void addLayer(Layer layer) {
		int zIndex = overlays.size() * 100;
		layer.init(this, zIndex);
		overlays.add(layer);
		view.addLayer(layer, zIndex);
		if (layer.isBase()) {
			baseLayer = layer;
		}
		mapEvents.fireEvent(new MapLayerAddedEvent(this, layer));
	}
	
	public Layer getLayerByName(String name) {
		for (Layer each : overlays) {
			if (name.equals(each.getName())) {
				return each;
			}
		}
		return null;
	}
	
	public void removeLayer(String name) {
		Layer theLayer = getLayerByName(name);
		if (theLayer != null) {
			overlays.remove(theLayer);
			view.removeLayer(theLayer);
		}
	}
	
	public void setCenter(LonLat center) {
		this.center = center;
	}
	
	Projection getProjection() {
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

	public Size getViewSize() {
		return view.getSize();
	}
	
	public Bounds getMaxExtent() {
		return maxExtent;
	}
	
	public void setMaxExtent(Bounds maxExtent) {
		this.maxExtent = maxExtent;
	}
	

	public Bounds getVisibleExtent() {
		Bounds extent = Calc.getExtent(center, resolutions[resolutionIndex], getViewportSize(), getProjection());
		return narrow(extent, maxExtent, getProjection());
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
		return Calc.getPoint(getCenter(), getMaxExtent(), getViewSize(), getProjection());
	}
	
	public Grid getGrid() {
		if (grid == null) {
			grid = new Grid(getTileSize(), getFullSize());
		}
		return grid;
	}
	
	private Size getFullSize() {
		return Calc.getPixelSize(getMaxExtent(), getResolution());
	}
	
	public Rectangle getVisibleRectangle() {
		Size portSize = getViewportSize();
		Point topLeft = getViewportLocation();
		return new Rectangle(topLeft.getX(), topLeft.getY(), portSize.getWidth(), portSize.getHeight());
	}
	
	public void zoomIn() {
		resizeView(1);
	}
	
	public void zoomOut() {
		resizeView(-1);
	}

	public void onViewPanEnded(ViewPanEndedEvent event) {
		mapEvents.fireEvent(new MapViewChangedEvent(false, true, false, false));
	}
	
	public void onViewPanned(ViewPannedEvent event) {
		setCenter(getLonLat(event.newCenterPoint, maxExtent, view.getSize(), getProjection()));
		mapEvents.fireEvent(new MapViewChangedEvent(true, false, false, false));
	}

	public void move(Direction dir, int amountPx) {
		Point centerPoint = Calc.getPoint(getCenter(), getMaxExtent(), getViewSize(), getProjection());
		Point newCenterPoint = Calc.addToPoint(centerPoint, amountPx, dir);
		LonLat newCenter = Calc.getLonLat(newCenterPoint, getMaxExtent(), getViewSize(), getProjection());
		Bounds newExtent = Calc.getExtent(newCenter, resolutions[resolutionIndex], getViewportSize(), getProjection());
		LonLat ensuredCenter = Calc.keepInBounds(newExtent, maxExtent, getProjection()).getCenter();
		setCenter(ensuredCenter);
		viewport.positionView(newCenterPoint);
		mapEvents.fireEvent(new MapViewChangedEvent(true, true, false, false));
	}

	public void onViewZoomed(ViewZoomedEvent event) {
		LonLat pointedAt = getLonLat(event.point, maxExtent, view.getSize(), getProjection());
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
		adjustViewAndViewportSize();
		grid = null;
		mapEvents.fireEvent(new MapViewChangedEvent(false, false, true, false));
		mapEvents.fireEvent(new MapZoomedEvent(this, resolutions[resolutionIndex]));
	}
	
	private void resizeView(int delta, LonLat newCenter) {
		if (newResolutionInBounds(delta)) {
			resolutionIndex += delta;
		}
		setCenter(newCenter);
		adjustViewAndViewportSize();
		grid = null;
		mapEvents.fireEvent(new MapViewChangedEvent(true, true, true, false));
		mapEvents.fireEvent(new MapZoomedEvent(this, resolutions[resolutionIndex]));
	}

	private void adjustViewAndViewportSize() {
		view.setSize(getPixelSize(maxExtent, resolutions[resolutionIndex]));
		viewport.positionView(getPoint(center, maxExtent, view.getSize(), getProjection()));
	}

	public boolean isDrawn() {
		return drawn;
	}

	public void addControl(Control control, Position at) {
		control.init(this);
		viewport.addControl(control, at);
	}
	
	public HandlerRegistration addMapViewChangedHandler(MapViewChangedEvent.Handler handler) {
		return mapEvents.addHandler(MapViewChangedEvent.TYPE, handler);
	}

	public HandlerRegistration addMapZoomedHandler(MapZoomedEvent.Handler handler) {
		return mapEvents.addHandler(MapZoomedEvent.TYPE, handler);
	}
	
	public HandlerRegistration addMapLayerAddedHandler(MapLayerAddedEvent.Handler handler) {
		return mapEvents.addHandler(MapLayerAddedEvent.TYPE, handler);
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				drawn = true;
				mapEvents.fireEvent(new MapViewChangedEvent(true, true, true, false));
			}
		});
	}


}


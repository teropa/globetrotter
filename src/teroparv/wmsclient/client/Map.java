package teroparv.wmsclient.client;

import static teroparv.wmsclient.client.Calc.getExtent;
import static teroparv.wmsclient.client.Calc.getLonLat;
import static teroparv.wmsclient.client.Calc.getPixelSize;
import static teroparv.wmsclient.client.Calc.getPoint;
import static teroparv.wmsclient.client.Calc.narrow;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Composite;

public class Map extends Composite implements ViewPannedEvent.Handler {

	private final View view = new View();
	private final Viewport viewport = new Viewport(view);
	private final List<Layer> layers = new ArrayList<Layer>();
	
	private Bounds maxExtent = new Bounds(-180, -90, 180, 90);
	private LonLat center = new LonLat(0, 0);
	private double[] resolutions = new double[] { 1.0, 0.5, 0.2, 0.1 };
	private double resolution = resolutions[3];
	
	public Map(String width, String height) {
		initWidget(viewport);
		setWidth(width);
		setHeight(height);
		viewport.addViewPannedEventHandler(this);
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

	public void setResolution(int index) {
		this.resolution = resolutions[index];
		resizeView();
	}
	
	public void draw() {
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				final Size portSize = viewport.getSize();
				final Bounds extent = narrow(getExtent(center, resolution, portSize), maxExtent);
				final Size imageSize = getPixelSize(extent, resolution);
				final Point topLeft = viewport.getViewTopLeftPoint();
				for (Layer eachLayer : layers) {
					eachLayer.draw(extent, imageSize, topLeft);
				}				
			}
		});
	}

	public void clear() {
		for (Layer eachLayer : layers) {
			eachLayer.clear();
		}
	}
	
	@Override
	public void onViewPanned(ViewPannedEvent event) {
		setCenter(getLonLat(event.newCenterPoint, maxExtent, view.getSize()));
		clear();
		draw();
	}
	
	private void resizeView() {
		view.setSize(getPixelSize(maxExtent, resolution));
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

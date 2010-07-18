package teroparv.wmsclient.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

public class Map extends Composite {

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
		resizeView();
	}
	
	public void addLayer(Layer layer) {
		layers.add(layer);
		layer.setMap(this);
	}
	
	public void setCenter(LonLat center) {
		this.center = center;
	}

	public void setResolution(int index) {
		this.resolution = resolutions[index];
		resizeView();
	}
	
	public void draw() {
		final Size portSize = viewport.getSize();
		final Bounds extent = Calc.narrow(Calc.getExtent(center, resolution, portSize), maxExtent);
		final Size imageSize = Calc.getPixelSize(extent, resolution);
		for (Layer eachLayer : layers) {
			final String url = eachLayer.constructUrl(extent, imageSize);
			GWT.log(url, null);
			Image image = new Image(url);
			image.setWidth(imageSize.getWidth()+"px");
			image.setHeight(imageSize.getHeight()+"px");
			view.add(image, new Point(0, 0));
		}
	}

	private void resizeView() {
		view.setWidth(Calc.getPixelWidth(maxExtent, resolution) + "px");
		view.setHeight(Calc.getPixelHeight(maxExtent, resolution) + "px");
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

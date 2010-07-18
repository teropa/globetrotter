package teroparv.wmsclient.client;

import static teroparv.wmsclient.client.Calc.getExtent;
import static teroparv.wmsclient.client.Calc.getPixelSize;
import static teroparv.wmsclient.client.Calc.narrow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;

public class Layer extends Composite {

	private final AbsolutePanel container = new AbsolutePanel();
	
	private String name;
	private String url;
	private boolean baseLayer = false;
	private boolean visible = true;
	
	private String layers;
	private String format = "image/png";
	private boolean transparent = false;
	private boolean singleTile = true;
	private double ratio;
	
	private Map map;
	
	public Layer(String name, String url) {
		this.name = name;
		this.url = url;
		initWidget(container);
	}
	
	public void setMap(Map map) {
		this.map = map;
	}

	public void setLayers(String layers) {
		this.layers = layers;
	}
	
	public void draw(Bounds extent, Size imageSize, Point topLeft) {
		final String url = constructUrl(extent, imageSize);
		GWT.log(url, null);
		Image image = new Image(url);
		image.setWidth(imageSize.getWidth()+"px");
		image.setHeight(imageSize.getHeight()+"px");
		container.add(image, topLeft.getX(), topLeft.getY());
	}


	public void clear() {
		while (container.getWidgetCount() > 0) {
			container.remove(0);
		}
	}
	
	public String constructUrl(Bounds forBounds, Size imageSize) {
		StringBuilder res = new StringBuilder();
		res.append(this.url);
		res.append("?VERSION=1.1.0&REQUEST=GetMap&LAYERS=");
		res.append(layers);
		res.append("&STYLES=&SRS=");
		res.append(URL.encodeComponent(map.getSRS()));
		res.append("&BBOX=");
		res.append(forBounds.getLowerLeftX());
		res.append(",");
		res.append(forBounds.getLowerLeftY());
		res.append(",");
		res.append(forBounds.getUpperRightX());
		res.append(",");
		res.append(forBounds.getUpperRightY());
		res.append("&WIDTH=");
		res.append(imageSize.getWidth());
		res.append("&HEIGHT=");
		res.append(imageSize.getHeight());
		res.append("&FORMAT=");
		res.append(URL.encodeComponent(format));
		res.append("&TRANSPARENT=");
		res.append(transparent);
		return res.toString();
	}

}

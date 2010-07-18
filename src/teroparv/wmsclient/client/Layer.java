package teroparv.wmsclient.client;

import com.google.gwt.http.client.URL;

public class Layer {

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
	}
	
	public void setMap(Map map) {
		this.map = map;
	}

	public void setLayers(String layers) {
		this.layers = layers;
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

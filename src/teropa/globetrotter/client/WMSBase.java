package teropa.globetrotter.client;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public abstract class WMSBase extends Composite implements Layer {

	protected String name;
	protected String url;
	
	protected String layers;
	protected String format = "image/png";
	protected boolean transparent = false;
	
	protected Map map;
			
	public WMSBase(String name, String url) {
		this.name = name;
		this.url = url;
	}
	
	public void setMap(Map map) {
		this.map = map;
	}

	public void setLayers(String layers) {
		this.layers = layers;
	}

	public Widget asWidget() {
		return this;
	}

	protected String constructUrl(Bounds forBounds, Size imageSize) {
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

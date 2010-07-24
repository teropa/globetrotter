package teropa.globetrotter.client.wms;

import teropa.globetrotter.client.Layer;
import teropa.globetrotter.client.ViewContext;
import teropa.globetrotter.client.common.Bounds;
import teropa.globetrotter.client.common.Size;

import com.google.gwt.http.client.URL;

public abstract class WMSBase extends Layer {

	protected String url;
	
	protected String layers;
	protected String format = "image/png";
	protected boolean transparent = false;
	
	protected String urlBase;
	
	protected String time;

	protected WMSBase(String name, String url) {
		super(name);
		this.url = url;
	}
	
	@Override
	public void init(ViewContext ctx) {
		super.init(ctx);
		this.urlBase = constructUrlBase();
	}

	public void setLayers(String layers) {
		this.layers = layers;
		this.urlBase = constructUrlBase();
	}

	public void setFormat(String format) {
		this.format = format;
		this.urlBase = constructUrlBase();
	}
	
	public void setTime(String time) {
		this.time = time;
		this.urlBase = constructUrlBase();
		onVisibilityChanged();
	}

	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
		this.urlBase = constructUrlBase();
	}

	private String constructUrlBase() {
		if (!initialized) return null;
		
		StringBuilder res = new StringBuilder();
		res.append(this.url);
		
		if (!this.url.contains("?")) {
			res.append("?");
		}
		if (!this.url.contains("VERSION=")) {
			res.append("&VERSION=1.1.0");
		}
		res.append("&REQUEST=GetMap&LAYERS=");
		res.append(layers);
		res.append("&STYLES=&SRS=");
		res.append(URL.encodeComponent(context.getSRS()));
		res.append("&FORMAT=");
		res.append(URL.encodeComponent(format));
		res.append("&TRANSPARENT=");
		res.append(transparent);
		if (time != null) {
			res.append("&TIME=");
			res.append(time);
		}
		return res.toString();
	}

	protected String constructUrl(Bounds forBounds, Size imageSize) {
		StringBuilder res = new StringBuilder();
		res.append(this.urlBase);
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
		return res.toString();
	}

}

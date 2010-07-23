package teropa.globetrotter.client.wms;

import java.util.Date;

import teropa.globetrotter.client.Layer;
import teropa.globetrotter.client.Map;
import teropa.globetrotter.client.common.Bounds;
import teropa.globetrotter.client.common.Size;

import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public abstract class WMSBase extends Composite implements Layer {

	protected String name;
	protected String url;
	protected boolean visible = true;
	
	protected String layers;
	protected String format = "image/png";
	protected boolean transparent = false;
	
	protected Map map;
	protected String urlBase;
	
	
	protected String time;

	protected WMSBase(Map map, String name, String url) {
		this.map = map;
		this.name = name;
		this.url = url;
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
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setIsVisible(boolean visible) {
		super.setVisible(visible);
		this.visible = visible;
		onVisibilityChanged();
	}
	
	public void setTime(String time) {
		this.time = time;
		this.urlBase = constructUrlBase();
		onVisibilityChanged();
	}
	
	protected abstract void onVisibilityChanged();

	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
		this.urlBase = constructUrlBase();
	}

	public Widget asWidget() {
		return this;
	}

	private String constructUrlBase() {
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
		res.append(URL.encodeComponent(map.getSRS()));
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

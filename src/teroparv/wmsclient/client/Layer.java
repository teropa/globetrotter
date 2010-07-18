package teroparv.wmsclient.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;

public class Layer extends Composite implements LoadHandler {

	private static final int IMAGE_BUFFER_SIZE = 3;
	
	private static final class BufferedImage extends Image {
		public Point desiredPosition;
		public boolean added;
	}
	
	private final AbsolutePanel container = new AbsolutePanel();
	
	private String name;
	private String url;
	
	private String layers;
	private String format = "image/png";
	private boolean transparent = false;
	
	private Map map;
	
	private BufferedImage[] imageBuffer = new BufferedImage[IMAGE_BUFFER_SIZE];
	private int imageBufferIdx = 0;
	
	public Layer(String name, String url) {
		this.name = name;
		this.url = url;
		initWidget(container);
		initImageBuffer();
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
		addImage(imageSize, topLeft, url);
	}

	private void addImage(Size imageSize, final Point topLeft, final String url) {
		final BufferedImage image = imageBuffer[imageBufferIdx];
		image.added = true;
		image.desiredPosition = topLeft;
		image.setUrl(url);
		image.setWidth(imageSize.getWidth() + "px");
		image.setHeight(imageSize.getHeight() + "px");
		container.add(image, -imageSize.getWidth(), -imageSize.getHeight());
		incImageBufferIdx();
	}

	public void onLoad(LoadEvent event) {
		for (int i=0 ; i<IMAGE_BUFFER_SIZE ; i++) {
			final BufferedImage image = imageBuffer[i];
			if (image.added) {
				container.setWidgetPosition(image, image.desiredPosition.getX(), image.desiredPosition.getY());
			}
		}		
	}
	
	private void incImageBufferIdx() {
		if (imageBufferIdx >= IMAGE_BUFFER_SIZE - 1) {
			imageBufferIdx = 0;
		} else {
			imageBufferIdx++;
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

	private void initImageBuffer() {
		for (int i = 0 ; i < IMAGE_BUFFER_SIZE ; i++) {
			imageBuffer[i] = new BufferedImage();
			imageBuffer[i].addLoadHandler(this);
		}
	}

}

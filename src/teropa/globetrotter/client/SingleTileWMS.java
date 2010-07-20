package teropa.globetrotter.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;

public class SingleTileWMS extends WMSBase implements LoadHandler {

	private static final int IMAGE_BUFFER_SIZE = 3;
	
	private static final class BufferedImage extends Image {
		public Point desiredPosition;
	}
	
	private final AbsolutePanel container = new AbsolutePanel();
	
	private BufferedImage[] imageBuffer = new BufferedImage[IMAGE_BUFFER_SIZE];
	private int imageBufferIdx = 0;
	private int requestedIdx = 0;
	
	public SingleTileWMS(String name, String url) {
		super(name, url);
		initWidget(container);
		initImageBuffer();
	}
	
	public void draw(Bounds extent, Size imageSize, Point topLeft) {
		final String url = constructUrl(extent, imageSize);
		GWT.log(url, null);
		addImage(imageSize, topLeft, url);
	}
	
	private void addImage(Size imageSize, final Point topLeft, final String url) {
		final BufferedImage image = imageBuffer[imageBufferIdx];
		image.desiredPosition = topLeft;
		image.setUrl(url);
		image.setWidth(imageSize.getWidth() + "px");
		image.setHeight(imageSize.getHeight() + "px");
		container.add(image, -imageSize.getWidth(), -imageSize.getHeight());
		requestedIdx = imageBufferIdx;
		incImageBufferIdx();
	}

	public void onLoad(LoadEvent event) {
		BufferedImage requested = imageBuffer[requestedIdx];
		if (event.getSource() == requested) {
			container.setWidgetPosition(requested, requested.desiredPosition.getX(), requested.desiredPosition.getY());
			for (int i=0 ; i<IMAGE_BUFFER_SIZE ; i++) {
				if (i != requestedIdx) {
					container.remove(imageBuffer[i]);
				}
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

	private void initImageBuffer() {
		for (int i = 0 ; i < IMAGE_BUFFER_SIZE ; i++) {
			imageBuffer[i] = new BufferedImage();
			imageBuffer[i].addLoadHandler(this);
		}
	}

}

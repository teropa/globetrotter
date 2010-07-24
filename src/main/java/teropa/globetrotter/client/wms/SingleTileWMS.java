package teropa.globetrotter.client.wms;

import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Size;
import teropa.globetrotter.client.event.ViewPanEndedEvent;
import teropa.globetrotter.client.event.ViewPannedEvent;
import teropa.globetrotter.client.event.ViewZoomedEvent;

import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

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
		initImageBuffer();
	}
	
	protected void onVisibilityChanged() {
		if (visible && initialized && context.isDrawn()) {
			draw();
		}
	}
	
	public void onMapPanned(ViewPannedEvent evt) {
	}
	
	public void onMapPanEnded(ViewPanEndedEvent evt) {		
		draw();
	}

	public void onMapZoomed(ViewZoomedEvent event) {
		draw();
	}

	private void draw() {
		final String url = constructUrl(context.getExtent(), context.getViewportSize());
		addImage(context.getViewportSize(), context.getViewportLocation(), url);
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
			for (int i=0 ; i<IMAGE_BUFFER_SIZE ; i++) {
				if (i != requestedIdx) {
					container.remove(imageBuffer[i]);
				}
			}
			container.setWidgetPosition(requested, requested.desiredPosition.getX(), requested.desiredPosition.getY());
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

	@Override
	public Widget asWidget() {
		return container;
	}
}

package teropa.globetrotter.client.wms;

import teropa.globetrotter.client.HiddenUntilLoadedImage;
import teropa.globetrotter.client.common.Calc;
import teropa.globetrotter.client.common.LonLat;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Size;
import teropa.globetrotter.client.event.MapViewChangedEvent;

import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

public class SingleTileWMS extends WMSBase implements LoadHandler {

	private static final int IMAGE_BUFFER_SIZE = 100;
	
	private static final class BufferedImage extends HiddenUntilLoadedImage {
		public LonLat desiredPosition;
		public boolean attached;
	}
	
	private final AbsolutePanel container = new AbsolutePanel();
	
	private BufferedImage[] imageBuffer = new BufferedImage[IMAGE_BUFFER_SIZE];
	private int imageBufferIdx = 0;
	private int requestedIdx = 0;
	
	public SingleTileWMS(String name, String url, boolean base) {
		super(name, url, base);
		initImageBuffer();
	}
	
	protected void onVisibilityChanged() {
		if (visible && initialized && context.isDrawn()) {
			draw();
		}
	}
	
	public void onMapViewChanged(MapViewChangedEvent evt) {
		if (!visible) return;
		
		if (evt.effectiveExtentChanged) {
			repositionImages();
		}
		if (evt.panEnded || evt.zoomed) {
			draw();
		}
	}
	
	private void draw() {
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				final String url = constructUrl(context.getVisibleExtent(), context.getViewportSize());
				addImage(context.getViewportSize(), Calc.getLonLat(context.getViewportLocation(), context.getEffectiveExtent(), context.getViewSize(), getProjection()), url);			
			}
		});
	}
	
	private void addImage(Size imageSize, final LonLat topLeft, final String url) {
		final BufferedImage image = imageBuffer[imageBufferIdx];
		image.attached = false;
		image.desiredPosition = topLeft;
		image.setUrl(url);
		image.setWidth(imageSize.getWidth() + "px");
		image.setHeight(imageSize.getHeight() + "px");
		container.add(image, -imageSize.getWidth(), -imageSize.getHeight());
		requestedIdx = imageBufferIdx;
		incImageBufferIdx();
	}

	private void repositionImages() {
		for (int i=0 ; i<IMAGE_BUFFER_SIZE ; i++) {
			BufferedImage img = imageBuffer[i];
			if (img.attached) {
				Point pos = Calc.getPoint(img.desiredPosition, context.getEffectiveExtent(), context.getViewSize(), context.getProjection());
				container.setWidgetPosition(img, pos.getX(), pos.getY());
			}
		}
	}

	public void onLoad(LoadEvent event) {
		BufferedImage requested = imageBuffer[requestedIdx];
		if (event.getSource() == requested) {
			for (int i=0 ; i<IMAGE_BUFFER_SIZE ; i++) {
				if (i != requestedIdx) {
					container.remove(imageBuffer[i]);
					imageBuffer[i].attached = false;
				}
			}
			Point pos = Calc.getPoint(requested.desiredPosition, context.getEffectiveExtent(), context.getViewSize(), context.getProjection());
			container.setWidgetPosition(requested, pos.getX(), pos.getY());
			requested.attached = true;
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

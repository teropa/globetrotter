package teropa.globetrotter.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Image;

public class ImagePool {

	private static final List<Image> pool = new ArrayList<Image>();
	
	public static Image get() {
		if (pool.isEmpty()) {
			return new Image();
		} else {
			return pool.remove(0);
		}
	}
	
	public static void release(Image image) {
		image.setUrl(null);
		pool.add(image);
	}
	
}

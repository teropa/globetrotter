package teropa.globetrotter.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Image;

public class ImagePool {

	private static final List<Image> pool = new ArrayList<Image>();
	
	public static Image get() {
		int size = pool.size();
		if (size == 0) {
			return new Image();
		} else {
			return pool.remove(size - 1);
		}
	}
	
	public static void release(Image image) {
		image.setUrl(null);
		pool.add(image);
	}
	
}

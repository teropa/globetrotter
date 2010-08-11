package teropa.globetrotter.client.controls;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface PannerImages extends ClientBundle {

	@Source("panner.png")
	public ImageResource panner();
	@Source("panner_up.png")
	public ImageResource pannerUp();
	@Source("panner_right.png")
	public ImageResource pannerRight();
	@Source("panner_down.png")
	public ImageResource pannerDown();
	@Source("panner_left.png")
	public ImageResource pannerLeft();
}

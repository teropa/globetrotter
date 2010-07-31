package teropa.globetrotter.client.controls;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;

public interface PannerImages extends ImageBundle {

	@Resource("panner.png")
	public AbstractImagePrototype panner();
	@Resource("panner_up.png")
	public AbstractImagePrototype pannerUp();
	@Resource("panner_right.png")
	public AbstractImagePrototype pannerRight();
	@Resource("panner_down.png")
	public AbstractImagePrototype pannerDown();
	@Resource("panner_left.png")
	public AbstractImagePrototype pannerLeft();
}

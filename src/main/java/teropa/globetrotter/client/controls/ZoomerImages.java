package teropa.globetrotter.client.controls;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;

public interface ZoomerImages extends ImageBundle {

	@Resource("zoomer_in.png")
	public AbstractImagePrototype zoomerIn();
	@Resource("zoomer_out.png")
	public AbstractImagePrototype zoomerOut();
	@Resource("zoomer_track.png")
	public AbstractImagePrototype zoomerTrack();
	@Resource("zoomer_knob.png")
	public AbstractImagePrototype zoomerKnob();
	
}

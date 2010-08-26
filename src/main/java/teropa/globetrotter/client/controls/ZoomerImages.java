package teropa.globetrotter.client.controls;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface ZoomerImages extends ClientBundle {

	@Source("zoomer_in.png")
	public ImageResource zoomerIn();
	@Source("zoomer_in_over.png")
	public ImageResource zoomerInOver();
	@Source("zoomer_out.png")
	public ImageResource zoomerOut();
	@Source("zoomer_out_over.png")
	public ImageResource zoomerOutOver();
	@Source("zoomer_track.png")
	public ImageResource zoomerTrack();
	@Source("zoomer_knob.png")
	public ImageResource zoomerKnob();
	
}

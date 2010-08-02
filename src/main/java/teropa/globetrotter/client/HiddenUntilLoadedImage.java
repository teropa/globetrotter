package teropa.globetrotter.client;

import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;

public class HiddenUntilLoadedImage extends Image implements LoadHandler {
	
	public HiddenUntilLoadedImage() {
		super();
		addLoadHandler(this);
		setVisible(false);
	}

	public HiddenUntilLoadedImage(String url) {
		super(url);
		addLoadHandler(this); // TODO: It's possible here that the image becomes "loaded" on the super constructor call (?)
		setVisible(false);
	}

	@Override
	public void setUrl(String url) {
		setVisible(false);
		super.setUrl(url);
	}

	public void onLoad(LoadEvent event) {
		setVisible(true);
	}
	
}

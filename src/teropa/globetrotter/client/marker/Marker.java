package teropa.globetrotter.client.marker;

import teropa.globetrotter.client.common.LonLat;

import com.google.gwt.user.client.ui.Image;

public class Marker {

	private final LonLat loc;
	private final Image image;
	
	public Marker(LonLat loc, Image image) {
		this.loc = loc;
		this.image = image;
	}

	public LonLat getLoc() {
		return loc;
	}
	
	public Image getImage() {
		return image;
	}
	
}

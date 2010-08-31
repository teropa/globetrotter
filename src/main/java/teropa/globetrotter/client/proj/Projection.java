package teropa.globetrotter.client.proj;

import teropa.globetrotter.client.common.Bounds;
import teropa.globetrotter.client.common.LonLat;

public abstract class Projection {

	public abstract LonLat to(LonLat lonLat);
	public abstract LonLat from(LonLat lonLat);
	public abstract String getSRS();
	public abstract int leftToRight();
	public abstract int topToBottom();
	public abstract Bounds getMaxExtent();;
	
}

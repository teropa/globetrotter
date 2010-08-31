package teropa.globetrotter.client.proj;

import teropa.globetrotter.client.common.Bounds;
import teropa.globetrotter.client.common.LonLat;

public final class WGS84 extends Projection {
	private static final Bounds MAX_EXTENT = new Bounds(-180, -90, 180, 90);
	
	@Override
	public LonLat to(LonLat lonLat) {
		return lonLat;
	}
	@Override
	public LonLat from(LonLat lonLat) {
		return lonLat;
	}
	@Override
	public String getSRS() {
		return "EPSG:4326";
	}
	@Override
	public int leftToRight() {
		return 1;
	}
	public int topToBottom() {
		return -1;
	};
	@Override
	public Bounds getMaxExtent() {
		return MAX_EXTENT;
	}
}
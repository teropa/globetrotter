package teropa.globetrotter.client;

import teropa.globetrotter.client.common.Bounds;
import teropa.globetrotter.client.common.LonLat;
import teropa.globetrotter.client.proj.Projection;

public class Projector {

	private final Map map;
	
	public Projector(Map map) {
		this.map = map;
	}
	
	public Projection getProjection() {
		return map.getProjection();
	}
	
	public LonLat getCenter() {
		return null;
	}
	
	public Bounds getVisibleExtent() {
		return null;
	}
	
	public Bounds getMaxExtent() {
		return null;
	}
	
}

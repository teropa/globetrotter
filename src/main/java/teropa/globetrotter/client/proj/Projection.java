package teropa.globetrotter.client.proj;

import teropa.globetrotter.client.common.LonLat;

public abstract class Projection {

	public abstract LonLat to(LonLat lonLat);
	public abstract LonLat from(LonLat lonLat);
	public abstract String getSRS();
	public abstract int leftToRight();
	public abstract int topToBottom();
	
	public static final Projection WGS_84 = new Projection() {
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
	};
	
}

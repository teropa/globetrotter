package teropa.globetrotter.client.proj;

import teropa.globetrotter.client.common.Bounds;

/**
 * 'EPSG:3785': "
 * +title= Google Mercator
 * +proj=merc
 * +a=6378137
 * +b=6378137
 * +lat_ts=0.0
 * +lon_0=0.0
 * +x_0=0.0
 * +y_0=0
 * +k=1.0
 * +units=m
 * +nadgrids=@null
 * +no_defs"
 *
 */
public class GoogleMercator extends Mercator {

	public static final Bounds MAX_EXTENT = new Bounds(2.0037508342789244E7, -2.0037508342789244E7, -2.0037508342789244E7, 2.0037508342789244E7);
	
	public GoogleMercator() {
		super(0.0, true, 0, 0, 0, 6378137, 0, 0, 1);
	}
	
	public String getSRS() {
		return "EPSG:3785";
	}
	
}

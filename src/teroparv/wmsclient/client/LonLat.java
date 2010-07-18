package teroparv.wmsclient.client;

public class LonLat {

	private final double lon;
	private final double lat;
	
	public LonLat(double lon, double lat) {
		this.lon = lon;
		this.lat = lat;
	}
	
	public double getLon() {
		return lon;
	}
	
	public double getLat() {
		return lat;
	}
	
}

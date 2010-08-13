package teropa.globetrotter.client.proj;

import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.sin;
import static java.lang.Math.tan;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;
import static teropa.globetrotter.client.proj.Constants.EPSILON;
import static teropa.globetrotter.client.proj.Constants.FORT_PI;
import static teropa.globetrotter.client.proj.Constants.HALF_PI;
import static teropa.globetrotter.client.proj.Constants.lonRadtoDeg;
import static teropa.globetrotter.client.proj.Constants.msfnz;
import static teropa.globetrotter.client.proj.Constants.phi2z;
import static teropa.globetrotter.client.proj.Constants.tsfnz;
import teropa.globetrotter.client.common.LonLat;

public abstract class Mercator extends Projection {

	private final boolean sphere;
	private final double x0;
	private final double y0;
	private final double a;
	private final double long0;
	private final double e;

	private final double k0;

	public Mercator(double latTs, boolean sphere, double es, double x0, double y0, double a, double long0, double e, double k0) {
		this.sphere = sphere;
		this.x0 = x0;
		this.y0 = y0;
		this.a = a;
		this.long0 = long0;
		this.e = e;
		if (latTs > 0) { // ????
			if (sphere) {
				this.k0 = cos(latTs);
			} else {
				this.k0 = msfnz(es, sin(latTs), cos(latTs));
			}
		} else {
			this.k0 = k0;
		}
	}
	
	@Override
	public LonLat to(LonLat lonLat) {
		double lon = toRadians(lonLat.getLon());
		double lat = toRadians(lonLat.getLat());
		
		if (abs(abs(lat) - HALF_PI) <= EPSILON) {
			throw new ProjectionException("Transformation cannot be computed at the poles");
		} 
		
		if (sphere) {
			double x = x0 + a * k0 * lonRadtoDeg(lon - long0);
			double y = y0 + a * k0 * log(tan(FORT_PI + 0.5 * lat));
			return new LonLat(x, y);
		} else {
			double sinphi = sin(lat);
			double ts = tsfnz(e, lat, sinphi);
			double x = x0 + a * k0 * lonRadtoDeg(lon - long0);
			double y = y0 + a * k0 * log(ts);
			return new LonLat(x, y);
		}
	}
	
	@Override
	public LonLat from(LonLat lonLat) {
		double x = lonLat.getLon() - x0;
		double y = lonLat.getLat() - y0;
		double lon, lat;
		
		if (sphere) {
			lat = HALF_PI - 2.0 * atan(exp(-y / a * k0));
		} else {
			double ts = exp(-y / (a * k0));
			lat = phi2z(e, ts);
			if (lat == -9999) {
				throw new ProjectionException("No convergence");
			}
		}
		lon = lonRadtoDeg(long0 + x / (a * k0));
		return new LonLat(toDegrees(lon), toDegrees(lat));
	}
	
	@Override
	public int leftToRight() {
		return 1;
	}
	
	@Override
	public int topToBottom() {
		return -1;
	}
	
}

package teropa.globetrotter.client.proj;

public class Constants {

	public static final double PI = 3.141592653589793238;
	public static final double HALF_PI = 1.570796326794896619;
	public static final double TWO_PI = 6.283185307179586477;
	public static final double FORT_PI = 0.78539816339744833;

	public static final double EPSILON = 1.0e-10;

	public static int sign(double num) {
		return num < 0 ? -1 : 1;
	}
	
	public static double lonRadtoDeg(double lon) {
		return Math.abs(lon) < Math.PI ? lon : (lon - (sign(lon) * TWO_PI) );
	}
	
	// Function to compute the constant small t for use in the forward
	//   computations in the Lambert Conformal Conic and the Polar
	//   Stereographic projections.
	public static double tsfnz(double eccent, double phi, double sinphi) {
		double con = eccent * sinphi;
		double com = 0.5 * eccent;
		con = Math.pow(((1.0 - con) / (1.0 + con)), com);
		return (Math.tan(0.5 * (HALF_PI - phi)) / con);
	}

	
	// Function to compute the latitude angle, phi2, for the inverse of the
	//   Lambert Conformal Conic and Polar Stereographic projections.
	public static double phi2z(double eccent, double ts) {
		double eccnth = 0.5 * eccent;
		double con, dphi;
		double phi = HALF_PI - 2 * Math.atan(ts);
		for (int i = 0; i <= 15; i++) {
			con = eccent * Math.sin(phi);
			dphi = HALF_PI - 2 * Math.atan(ts * (Math.pow(((1.0 - con) / (1.0 + con)), eccnth))) - phi;
			phi += dphi;
			if (Math.abs(dphi) <= EPSILON) return phi;
		}
		return -9999;
	}

	// Function to compute the constant small m which is the radius of
	//   a parallel of latitude, phi, divided by the semimajor axis.
	public static double msfnz(double eccent, double sinphi, double cosphi) {
		double con = eccent * sinphi;
		return cosphi / (Math.sqrt(1.0 - con * con));
	}
	
}

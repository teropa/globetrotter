package teropa.globetrotter.proj;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import teropa.globetrotter.client.common.Bounds;
import teropa.globetrotter.client.common.LonLat;
import teropa.globetrotter.client.proj.GoogleMercator;
import teropa.globetrotter.client.proj.ProjectionException;


public class GoogleMercatorTest {

	private static LonLat[][] testData = new LonLat[][] {
		{ new LonLat(0, 0), new LonLat(0, 0) },
		{ new LonLat(5.625, 14.0625), new LonLat(626172.135712, 1581387.986379) },
		{ new LonLat(6.328125, 31.640625), new LonLat(704443.652676, 3716228.970187) },
		{ new LonLat(2.109375, -20.390625), new LonLat(234814.550892, -2319363.689517) },
		{ new LonLat(-119.53125, -8.4375), new LonLat(-13306157.883883, -942671.539953) },
		{ new LonLat(-68.90625, 85.153419729306), new LonLat(-7670608.662474, 20170885.48005) }
	};
	
	private static GoogleMercator proj;
	
	@BeforeClass
	public static void setUpClass() {
		proj = new GoogleMercator();
	}
	
	@Test
	public void shouldConvertToAndFromCorrectly() {
		for (LonLat[] each : testData) {
			LonLat base = each[0];
			LonLat projected = each[1];
			
			assertSameLonLat(projected, proj.to(base));
			assertSameLonLat(base, proj.from(projected));			
		}
	}

	@Test(expected=ProjectionException.class)
	public void shouldNotComputeAtNorthPole() {
		LonLat lonLat = new LonLat(0, -90);
		proj.to(lonLat);
	}
	
	@Test(expected=ProjectionException.class)
	public void shouldNotComputeAtSouthPole() {
		LonLat lonLat = new LonLat(0, 90);
		proj.to(lonLat);	
	}
	
	@Test
	public void fullExtentShouldBeRoughlySquare() {
		LonLat bottomLeft = proj.to(new LonLat(-180, -85.0511));
		LonLat topRight = proj.to(new LonLat(180, 85.0511));
		
		Bounds extent = new Bounds(bottomLeft.getLon(), bottomLeft.getLat(), topRight.getLon(), topRight.getLat());
		int orderOfMagnitude = (int)Math.log10(extent.getWidth()); 
		double delta = Math.pow(10, orderOfMagnitude - 5);
		
		assertEquals(extent.getWidth(), extent.getHeight(), delta);
	}
	
	@Test
	public void zoomLevels() {
		double full = GoogleMercator.MAX_EXTENT.getWidth();
		for (int n=0 ; n<19 ; n++) {
			double numTiles = Math.pow(2, n);
			double size = numTiles * 256;
			double res = full / size;
			System.out.println(n+" px: "+size+", res: "+res);
		}
	}
	
	private void assertSameLonLat(LonLat base, LonLat from) {
		assertEquals(base.getLon(), from.getLon(), 0.000001);
		assertEquals(base.getLat(), from.getLat(), 0.000001);
	}
	
}

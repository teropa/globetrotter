package teropa.globetrotter.client;

import static org.junit.Assert.*;

import java.awt.geom.Area;

import org.junit.Test;

import teropa.globetrotter.client.common.Bounds;
import teropa.globetrotter.client.common.Calc;
import teropa.globetrotter.client.common.LonLat;
import teropa.globetrotter.client.common.Point;
import teropa.globetrotter.client.common.Size;
import teropa.globetrotter.client.proj.GoogleMercator;
import teropa.globetrotter.client.proj.Projection;

public class CalcTest {

	@Test
	public void pixelWidthShouldBeSameAsBoundsWidthWhenResolutionIsOne() throws Exception {
		Bounds bounds = new Bounds(-180, -90, 180, 90);
		assertEquals(360, Calc.getPixelWidth(bounds, 1.0));
	}

	@Test
	public void pixelHeightShouldBeSameAsBoundsWidthWhenResolutionIsOne() throws Exception {
		Bounds bounds = new Bounds(-180, -90, 180, 90);
		assertEquals(180, Calc.getPixelHeight(bounds, 1.0));
	}

	@Test
	public void pixelWidthShouldBeDoubleOfBoundsWidthWhenResolutionIsHalf() throws Exception {
		Bounds bounds = new Bounds(-180, -90, 180, 90);
		assertEquals(720, Calc.getPixelWidth(bounds, 0.5));
	}

	@Test
	public void pixelHeightShouldBeDoubleOfBoundsHeightWhenResolutionIsHalf() throws Exception {
		Bounds bounds = new Bounds(-180, -90, 180, 90);
		assertEquals(360, Calc.getPixelHeight(bounds, 0.5));
	}
	
	@Test
	public void lonLatShouldStayTheSameWhenConvertedToPointAndBackWithTheSameArea() throws Exception {
		LonLat lonLat = new LonLat(10, 10);
		Bounds extent = new Bounds(-180, -90, 180, 90);
		Size area = new Size(360, 180);
		
		Point point = Calc.getPoint(lonLat, extent, area, Projection.WGS_84);
		
		assertEquals(190, point.getX());
		assertEquals(80, point.getY());
		
		LonLat result = Calc.getLonLat(point, extent, area, Projection.WGS_84);
		
		assertEquals(lonLat.getLon(), result.getLon(), 0.01);
		assertEquals(lonLat.getLat(), result.getLat(), 0.01);
	}

	@Test
	public void shouldCalculateExtentWithLonLatProjection() throws Exception {
		LonLat center = new LonLat(0, 0);
		double resolution = 2.0;
		Size viewportSize = new Size(100, 100); 
		
		Bounds extent = Calc.getExtent(center, resolution, viewportSize, Projection.WGS_84);
		
		assertEquals(-100.0, extent.getLowerLeftX(), 0.000001);
		assertEquals(100.0, extent.getUpperRightX(), 0.000001);
		assertEquals(-100.0, extent.getLowerLeftY(), 0.000001);
		assertEquals(100.0, extent.getUpperRightY(), 0.000001);
	}

	@Test
	public void shouldCalculateExtentWithGoogleMercator() throws Exception {
		LonLat center = new LonLat(0, 0);
		double resolution = 2.0;
		Size viewportSize = new Size(100, 100); 
		
		Bounds extent = Calc.getExtent(center, resolution, viewportSize, new GoogleMercator());
		
		assertEquals(100.0, extent.getLowerLeftX(), 0.000001);
		assertEquals(-100.0, extent.getUpperRightX(), 0.000001);
		assertEquals(-100.0, extent.getLowerLeftY(), 0.000001);
		assertEquals(100.0, extent.getUpperRightY(), 0.000001);
	}

	@Test
	public void shouldNarrowBoundsCorrectlyWithLonLat() throws Exception {
		Bounds extent = new Bounds(-10, -10, 10, 10);
		Bounds max = new Bounds(-5, -5, 20, 20);
		
		Bounds result = Calc.narrow(extent, max, Projection.WGS_84);
		
		assertEquals(new Bounds(-5, -5, 10, 10), result);
	}
	
	@Test
	public void shouldNarrowBoundsCorrectlyWwithGoogleMercator() throws Exception {
		Bounds extent = new Bounds(10, -10, -10, 10);
		Bounds max = new Bounds(20, -5, -5, 20);
		
		Bounds result = Calc.narrow(extent, max, new GoogleMercator());
		
		assertEquals(new Bounds(10, -5, -5, 10), result);
	}

	@Test
	public void shouldCalculateLonLatCorrectlyWithLonLat() throws Exception {
		LonLat result = Calc.getLonLat(new Point(1, 1), new Bounds(-10, -10, 10, 10), new Size(10, 10), Projection.WGS_84);
		assertEquals(-8, result.getLon(), 0.000001);
		assertEquals(8, result.getLat(), 0.000001);
	}

	@Test
	public void shouldCalculateLonLatCorrectlyWithGoogleMercator() throws Exception {
		LonLat result = Calc.getLonLat(new Point(1, 1), new Bounds(10, -10, -10, 10), new Size(10, 10), new GoogleMercator());
		assertEquals(8, result.getLon(), 0.000001);
		assertEquals(8, result.getLat(), 0.000001);
	}

	@Test
	public void shouldCalculatePointCorrectlyWithLonLat() throws Exception {
		Point point = Calc.getPoint(new LonLat(-8, 8), new Bounds(-10, -10, 10, 10), new Size(10, 10), Projection.WGS_84);
		assertEquals(1, point.getX());
		assertEquals(1, point.getY());
	}
	
	@Test
	public void shouldCalculatePointCorrectlyWithGoogleMercator() throws Exception {
		Point point = Calc.getPoint(new LonLat(8, 8), new Bounds(10, -10, -10, 10), new Size(10, 10), new GoogleMercator());
		assertEquals(1, point.getX());
		assertEquals(1, point.getY());
	}
	
	@Test
	public void shouldCalculateIntersectionCorrectlyWithLonLat() throws Exception {
		Bounds b1 = new Bounds(-10, -10, 10, 10);
		Bounds b2 = new Bounds(9, 9, 19, 19);
		assertTrue(Calc.intersect(b1, b2, Projection.WGS_84));
	}
	
	@Test
	public void shouldCalculateNoIntersectionCorrectlyWithLonLat() throws Exception {
		Bounds b1 = new Bounds(-10, -10, 10, 10);
		Bounds b2 = new Bounds(11, 11, 19, 19);
		assertFalse(Calc.intersect(b1, b2, Projection.WGS_84));
	}

	@Test
	public void shouldCalculateIntersectionCorrectlyWithGoogleMercator() throws Exception {
		Bounds b1 = new Bounds(10, -10, -10, 10);
		Bounds b2 = new Bounds(-9, 9, -19, 19);
		assertTrue(Calc.intersect(b1, b2, new GoogleMercator()));
	}
	
	@Test
	public void shouldCalculateNoIntersectionCorrectlyWithGoogleMercator() throws Exception {
		Bounds b1 = new Bounds(10, -10, -10, 10);
		Bounds b2 = new Bounds(-11, 11, -19, 19);
		assertFalse(Calc.intersect(b1, b2, new GoogleMercator()));
	}

	@Test
	public void shouldCalculateEffectiveExtentCorrectlyWithLonLat() throws Exception {
		Bounds effective = Calc.getEffectiveExtent(new Bounds(-180, -90, 180, 90), 0.001, new LonLat(0, 0), Projection.WGS_84);
		assertEquals(new Bounds(-5.0, -5.0, 5.0, 5.0), effective);
	}
	
	@Test
	public void shouldCalculateEffectiveExtentCorrectlyWithGoogleMercator() throws Exception {
		Bounds effective = Calc.getEffectiveExtent(new Bounds(180, -90, -180, 90), 0.001, new LonLat(0, 0), new GoogleMercator());
		assertEquals(new Bounds(5.0, -5.0, -5.0, 5.0), effective);
	}
	
	@Test
	public void shouldKeepExtentInBoundsCorrectlyWithLonLat() throws Exception {
		Bounds max = new Bounds(-10, -10, 10, 10);
		Bounds bounds = new Bounds(-4, -4, 12, 12);
		
		Bounds result = Calc.keepInBounds(bounds, max, Projection.WGS_84);
		
		assertEquals(new Bounds(-6, -6, 10, 10), result);
	}
	
	@Test
	public void shouldKeepExtentInBoundsCorrectlyWithGoogleMercator() throws Exception {
		Bounds max = new Bounds(10, -10, -10, 10);
		Bounds bounds = new Bounds(4, -4, -12, 12);
		
		Bounds result = Calc.keepInBounds(bounds, max, new GoogleMercator());
		
		assertEquals(new Bounds(6, -6, -10, 10), result);
	}
}

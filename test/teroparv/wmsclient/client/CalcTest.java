package teroparv.wmsclient.client;

import static org.junit.Assert.*;

import org.junit.Test;

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
		
		Point point = Calc.getPoint(lonLat, extent, area);
		
		assertEquals(190, point.getX());
		assertEquals(80, point.getY());
		
		LonLat result = Calc.getLonLat(point, extent, area);
		
		assertEquals(lonLat.getLon(), result.getLon(), 0.01);
		assertEquals(lonLat.getLat(), result.getLat(), 0.01);
	}

}

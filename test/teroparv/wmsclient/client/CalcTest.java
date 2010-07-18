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

}

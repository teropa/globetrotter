package teropa.globetrotter.client;

import static org.junit.Assert.*;

import org.junit.Test;

import teropa.globetrotter.client.Bounds;


public class BoundsTest {

	@Test
	public void widthShouldBeDistanceOfXsWhenOneNegativeAndOnePositive() {
		Bounds bounds = new Bounds(-180, -90, 180, 90);
		assertEquals(360, bounds.getWidth(), 0.0001);
	}
	
	@Test
	public void widthShouldBeDistanceOfXsWhenBothPositive() {
		Bounds bounds = new Bounds(170, -90, 180, 90);
		assertEquals(10, bounds.getWidth(), 0.0001);
	}

	@Test
	public void widthShouldBeDistanceOfXsWhenBothNegative() {
		Bounds bounds = new Bounds(-180, -90, -10, 90);
		assertEquals(170, bounds.getWidth(), 0.0001);
	}

	@Test
	public void heightShouldBeDistanceOfXsWhenOneNegativeAndOnePositive() {
		Bounds bounds = new Bounds(-180, -90, 180, 90);
		assertEquals(180, bounds.getHeight(), 0.0001);
	}
	
	@Test
	public void heightShouldBeDistanceOfXsWhenBothPositive() {
		Bounds bounds = new Bounds(-180, 30, 180, 90);
		assertEquals(60, bounds.getHeight(), 0.0001);
	}

	@Test
	public void heightShouldBeDistanceOfXsWhenBothNegative() {
		Bounds bounds = new Bounds(-180, -90, -10, -30);
		assertEquals(60, bounds.getHeight(), 0.0001);
	}
		
}

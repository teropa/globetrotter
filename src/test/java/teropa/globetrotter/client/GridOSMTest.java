package teropa.globetrotter.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;

import teropa.globetrotter.client.common.Bounds;
import teropa.globetrotter.client.common.Size;
import teropa.globetrotter.client.osm.OpenStreetMapLayer;
import teropa.globetrotter.client.proj.GoogleMercator;


public class GridOSMTest {

	Size tileSize = new Size(256, 256);
	ViewContext ctx = mock(ViewContext.class);
	
	@Test
	public void outerMostResolutionShouldHaveOneTileWhoseExtentIsTheMaxExtent() {
		when(ctx.getProjection()).thenReturn(new GoogleMercator());
		Bounds extent = GoogleMercator.MAX_EXTENT;
		double res = OpenStreetMapLayer.SUPPORTED_RESOLUTIONS[0];
		Grid grid = new Grid(ctx, tileSize, extent, extent, res);
		
		assertEquals(1, grid.getNumCols());
		assertEquals(1, grid.getNumRows());
		
		List<Grid.Tile> tiles = grid.getTiles(extent);
		assertEquals(1, tiles.size());
		assertEquals(extent, tiles.get(0).getExtent());
	}
	
	@Test
	public void secondResolutionShouldHaveFourTiles() {
		when(ctx.getProjection()).thenReturn(new GoogleMercator());
		Bounds extent = GoogleMercator.MAX_EXTENT;
		double res = OpenStreetMapLayer.SUPPORTED_RESOLUTIONS[1];
		Grid grid = new Grid(ctx, tileSize, extent, extent, res);
		
		assertEquals(2, grid.getNumCols());
		assertEquals(2, grid.getNumRows());
		
		List<Grid.Tile> tiles = grid.getTiles(extent);
		assertEquals(4, tiles.size());
		
		Bounds bottomLeft = new Bounds(extent.getLowerLeftX(), extent.getLowerLeftY(), extent.getLowerLeftX() - extent.getWidth() / 2, extent.getLowerLeftY() + extent.getHeight() / 2);
		assertEquals(bottomLeft, tiles.get(0).getExtent());
		Bounds topLeft = new Bounds(extent.getLowerLeftX(), extent.getLowerLeftY() + extent.getHeight() / 2, extent.getLowerLeftX() - extent.getWidth() / 2, extent.getLowerLeftY() + extent.getHeight());
		assertEquals(topLeft, tiles.get(1).getExtent());
		Bounds bottomRight = new Bounds(extent.getLowerLeftX() - extent.getWidth() / 2, extent.getLowerLeftY(), extent.getLowerLeftX() - extent.getWidth(), extent.getLowerLeftY() + extent.getHeight() / 2);
		assertEquals(bottomRight, tiles.get(2).getExtent());
		Bounds topRight = new Bounds(extent.getLowerLeftX() - extent.getWidth() / 2, extent.getLowerLeftY() + extent.getHeight() / 2, extent.getLowerLeftX() - extent.getWidth(), extent.getLowerLeftY() + extent.getHeight());
		assertEquals(topRight, tiles.get(3).getExtent());

	}
	
	@Test
	public void thirdResolutionShouldHaveSixteenTiles() {
		when(ctx.getProjection()).thenReturn(new GoogleMercator());
		Bounds extent = GoogleMercator.MAX_EXTENT;
		double res = OpenStreetMapLayer.SUPPORTED_RESOLUTIONS[2];
		Grid grid = new Grid(ctx, tileSize, extent, extent, res);
		
		assertEquals(4, grid.getNumCols());
		assertEquals(4, grid.getNumRows());
		
		List<Grid.Tile> tiles = grid.getTiles(extent);
		assertEquals(16, tiles.size());
	}
}

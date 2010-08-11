package teropa.globetrotter.client.osm;

import java.util.Collection;
import java.util.List;

import teropa.globetrotter.client.CanvasView;
import teropa.globetrotter.client.Grid;
import teropa.globetrotter.client.Grid.Tile;
import teropa.globetrotter.client.Layer;
import teropa.globetrotter.client.common.Rectangle;
import teropa.globetrotter.client.proj.GoogleMercator;

public class OpenStreetMapLayer extends Layer {

	// based on the zoom levels at http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
	public static final double[] SUPPORTED_RESOLUTIONS = {
		156543.03392804097,
		78271.51696402048,
		39135.75848201024,
		19567.87924100512,
		9783.93962050256,
		4891.96981025128,
		2445.98490512564,
		1222.99245256282,
		611.49622628141,
		305.748113140705,
		152.8740565703525,
		76.43702828517625,
		38.21851414258813,
		19.109257071294063,
		9.554628535647032,
		4.777314267823516,
		2.388657133911758,
		1.194328566955879,
		0.5971642834779395};

	private final String baseUrl;
	
	public OpenStreetMapLayer(String baseUrl, String name, boolean base) {
		super(name, base, new GoogleMercator());
		this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
	}
	
	@Override
	public void addTiles(Collection<Tile> newTiles) {
		for (Tile each : newTiles) {
			String url = getUrl(context.getResolutionIndex(), each.getCol(), each.getRow());
			context.getView().addImage(each.getLeftX(), each.getTopY(), url);
		}
	}
	
	@Override
	public void drawOn(CanvasView canvasView) {
		Grid grid = context.getGrid();
		Rectangle visibleRect = context.getVisibleRectangle();
		List<Grid.Tile> tiles = grid.getTiles(visibleRect);
		int length = tiles.size();
		for (int i=0 ; i<length ; i++) {
			Grid.Tile eachTile = tiles.get(i);
			canvasView.addImage(eachTile.getLeftX(), eachTile.getTopY(), getUrl(context.getResolutionIndex(), eachTile.getCol(), eachTile.getRow()));
		}		
	}
	
	private String getUrl(int zoom, int x, int y) {
		return baseUrl + zoom + "/" + x + "/" + y + ".png";
	}
	

}

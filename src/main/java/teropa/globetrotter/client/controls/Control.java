package teropa.globetrotter.client.controls;

import com.google.gwt.user.client.ui.Widget;

import teropa.globetrotter.client.Map;

public interface Control {

	public void init(Map map);
	
	public Widget asWidget();
}

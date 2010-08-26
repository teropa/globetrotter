package teropa.globetrotter.client.controls;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import teropa.globetrotter.client.Map;

public class CopyrightText implements Control {

	private final HTML text;
	
	public CopyrightText(HTML text) {
		this.text = text;
	}
	
	public void init(Map map) {
	}
	
	public Widget asWidget() {
		return text;
	}
}

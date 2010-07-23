package teropa.globetrotter.client.controls;

import teropa.globetrotter.client.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class Zoomer extends Composite {
	
	private final FlowPanel container = new FlowPanel();
	private final Map map;
	
	public Zoomer(final Map map) {
		this.map = map;
		initWidget(container);
		
		for (int i=0 ; i<map.getResolutions().length ; i++) {
			final int idx = i;
			container.add(new Button(""+(i + 1), new ClickHandler() {
				public void onClick(ClickEvent event) {
					map.setResolutionIndex(idx);
				}
			})); 
		}
	}

}

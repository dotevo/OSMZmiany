package org.openstreetmap.OSMZmiany;

import org.openstreetmap.OSMZmiany.DataContainer.Node;
import org.openstreetmap.gui.jmapviewer.Coordinate;

public interface ZMapWidgetListener {
	void nodeClicked(Node node);
	void boxDrawed(Coordinate c1,Coordinate c2);
}

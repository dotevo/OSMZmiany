package org.openstreetmap.OSMZmiany;

import java.awt.Graphics;
import java.io.Serializable;

import org.openstreetmap.OSMZmiany.DataContainer.Node;

public interface DrawStyle extends Serializable {
	public void drawNode(Graphics g,ZMapWidget map,Node node);
}

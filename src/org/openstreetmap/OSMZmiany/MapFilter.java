package org.openstreetmap.OSMZmiany;

import java.io.Serializable;

import org.openstreetmap.OSMZmiany.DataContainer.Changeset;
import org.openstreetmap.OSMZmiany.DataContainer.Node;
import org.openstreetmap.OSMZmiany.DataContainer.Way;

public interface MapFilter extends Serializable{
	public boolean nodeFilter(Node node);
	public boolean wayFilter(Way way);
	public boolean changesetFilter(Changeset chs);
	
}

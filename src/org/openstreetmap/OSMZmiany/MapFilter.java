package org.openstreetmap.OSMZmiany;

import org.openstreetmap.OSMZmiany.DataContainer.Changeset;
import org.openstreetmap.OSMZmiany.DataContainer.Node;
import org.openstreetmap.OSMZmiany.DataContainer.Way;

public interface MapFilter {
	public boolean nodeFilter(Node node);
	public boolean wayFilter(Way way);
	public boolean changesetFilter(Changeset chs);
	
}

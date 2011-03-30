package org.openstreetmap.OSMZmiany;

import org.openstreetmap.OSMZmiany.DataContainer.Changeset;
import org.openstreetmap.OSMZmiany.DataContainer.Node;
import org.openstreetmap.OSMZmiany.DataContainer.Way;

public class BoundaryMapFilter implements MapFilter{
	double lat1;
	double lon1;
	double lat2;
	double lon2;
	
	public BoundaryMapFilter(double lat1,double lon1,double lat2,double lon2){
		this.lat1=lat1;
		this.lat2=lat2;
		this.lon1=lon1;
		this.lon2=lon2;
	}
	
	public boolean changesetFilter(Changeset chs) {
		return true;
	}

	public boolean nodeFilter(Node node) {		
		if((node.lat>lat2&&node.lat<lat1&&
			node.lon>lon1&&node.lon<lon2)||
			(node.lat<lat2&&node.lat>lat1&&
			node.lon<lon1&&node.lon>lon2))
		{
			return true;
		}
		
		return false;
	}

	public boolean wayFilter(Way way) {
		return true;
	}

}

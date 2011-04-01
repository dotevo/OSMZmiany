package org.openstreetmap.OSMZmiany;

import org.openstreetmap.OSMZmiany.Configuration.Profile;

public interface ConfigurationListener {
	public void profileChanged(Profile p);
}

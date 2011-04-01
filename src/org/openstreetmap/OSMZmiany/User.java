package org.openstreetmap.OSMZmiany;

import java.io.Serializable;

public class User implements Serializable{
	String name;
	long id;		
	public User(long id,String name){this.id=id;this.name=name;}
}
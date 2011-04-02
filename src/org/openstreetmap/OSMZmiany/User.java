package org.openstreetmap.OSMZmiany;

import java.io.Serializable;

public class User implements Serializable{
	private static final long serialVersionUID = 6887541516782439249L;
	String name;
	long id;		
	public User(long id,String name){this.id=id;this.name=name;}
	public boolean equals(User user){return this.id==user.id;}
}
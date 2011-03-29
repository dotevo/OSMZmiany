package org.openstreetmap.OSMZmiany;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.openstreetmap.osmosis.core.xml.common.DateParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.*;

public class DataContainer extends DefaultHandler{
	short mode=0;

	DateParser dp = new DateParser();
	
	class Node{
		long id;
		double lat,lon;
		long changesetId;
		short mode;
		public Node(long id, double lat, double lon, long changesetId, short mode){
			this.id=id;
			this.lat=lat;
			this.lon=lon;
			this.changesetId=changesetId;
			this.mode=mode;
		}
	}
	
	class Way{
		long id;
		long changesetId;
		short mode;		
		public Way(){}
	}
	
	class Changeset{
		long id;
		long userId;
		long time;
		public Changeset(long id,long time,long user){this.id=id;this.userId=user;this.time=time;}
	}
	
	class User{
		String name;
		long id;		
		public User(long id,String name){this.id=id;this.name=name;}
	}
	
	HashMap<Long,Node> nodes=new HashMap<Long,Node>();
	HashMap<Long,Way> ways=new HashMap<Long,Way>();
	HashMap<Long,User> users=new HashMap<Long,User>();
	HashMap<Long,Changeset> changesets=new HashMap<Long,Changeset>();
	
	
	
	
	
	public DataContainer(){       
	}
	
	
	void addData(InputStream i){
		System.out.println("Adding data...");
		
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(false);
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(i, this);
		} catch (IOException e) {
			System.out.println("IOException: " + e);
			e.printStackTrace();
			System.exit(10);
		} catch (SAXException e) {
			System.out.println("SAXException: " + e);
			e.printStackTrace();
			System.exit(10);
		} catch (Exception e) {
			System.out.println("Other Exception: " + e);
			e.printStackTrace();
			System.exit(10);
		}
	}
	
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
		 if (qName.equals("create")) {
			 mode = 0;
		 } else if (qName.equals("modify")) {
			 mode = 1;
		 } else if (qName.equals("delete")) {
			 mode = 2;
		 } else if (qName.equals("node")) {
			 //USER
			 long uid = Long.parseLong(atts.getValue("uid"));
			 String userName = atts.getValue("user");
			 User user=getUser(uid,userName);
			 
			 //CHANGESET
			 long changesetId = Long.parseLong(atts.getValue("changeset"));
			 long time = dp.parse(atts.getValue("timestamp")).getTime();
			 Changeset changeset=getChangeset(changesetId,time,uid);
			 
			 //NODES 
			 double lat = Double.parseDouble(atts.getValue("lat"));
			 double lon = Double.parseDouble(atts.getValue("lon"));
			 long id = Long.parseLong(atts.getValue("id"));
			 Node node=getNode(id,lat,lon,changesetId,mode);
		 }
	}


	private Node getNode(long id, double lat, double lon, long changesetId,
			short mode) {
		
		Node node=nodes.get(changesetId);
		if(node==null){
			node=new Node(id,lat,lon,changesetId,mode);
			nodes.put(id, node);
		}else{
			node.lat=lat;
			node.lon=lon;
			node.mode=mode;
		}
		return node;
	}


	private Changeset getChangeset(long changesetId, long time, long user) {
		Changeset changeset=changesets.get(changesetId);
		if(changeset==null){
			changeset=new Changeset(changesetId,time,user);
			changesets.put(changesetId, changeset);
		}else{
			changeset.time=time;
		}
		return changeset;
	}


	private User getUser(long uid, String username) {
		User user=users.get(uid);
		if(user==null){
			user=new User(uid,username);
			users.put(uid, user);
		}
		return user;
	}
}

package org.openstreetmap.OSMZmiany;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

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
	MapFilter mapfilter=null;
	
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
	
	HashMap<Long,Integer> changesetsIndex=new HashMap<Long,Integer>();
	Vector <Changeset> changesets=new Vector <Changeset>();
	
	
	
	
	
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
			 //TODO It can be faster!
			 
			 //NODES 
			 double lat = Double.parseDouble(atts.getValue("lat"));
			 double lon = Double.parseDouble(atts.getValue("lon"));
			 long id = Long.parseLong(atts.getValue("id"));
			 
			 //USER
			 long uid = Long.parseLong(atts.getValue("uid"));
			 String userName = atts.getValue("user");
			 
			 //CHANGESET
			 long changesetId = Long.parseLong(atts.getValue("changeset"));
			 long time = dp.parse(atts.getValue("timestamp")).getTime();
			 
			 
			 
			 Node node=getNode(id,lat,lon,changesetId,mode);
			 if(mapfilter!=null&&!mapfilter.nodeFilter(node))
				 nodes.remove(id);
			 else{			 
				 Changeset changeset=getChangeset(changesetId,time,uid);
				 User user=getUser(uid,userName);
			 }
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
		Integer changesetI=changesetsIndex.get(changesetId);
		Changeset chs=null;
		if(changesetI==null){
			chs=new Changeset(changesetId,time,user);
			changesets.add(chs);
			changesetsIndex.put(changesetId, changesets.size()-1);
		}else{
			chs=changesets.get(changesetI);
			chs.time=time;
		}
		return chs;
	}


	private User getUser(long uid, String username) {
		User user=users.get(uid);
		if(user==null){
			user=new User(uid,username);
			users.put(uid, user);
		}
		return user;
	}


	public void setNewDataFilter(MapFilter mf) {
		mapfilter=mf;
	}


	public void removeData(MapFilter mf) {
		//Remove data
		
		//nodes
		Iterator<Long> nodeL=nodes.keySet().iterator();
		Vector <Long> toRemove=new Vector <Long>();
		while(nodeL.hasNext()){
			Long t=nodeL.next();
			if(!mf.nodeFilter(nodes.get(t))){
				toRemove.add(t);
			}			
		}
		for(int i=0;i<toRemove.size();i++)
			nodes.remove(toRemove.get(i));
		
		//TODO changesets, ways
	}


	public void clear() {
		this.changesets.clear();
		this.nodes.clear();
		this.ways.clear();
		this.users.clear();
		this.changesetsIndex.clear();
	}
}

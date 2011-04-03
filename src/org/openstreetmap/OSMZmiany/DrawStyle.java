package org.openstreetmap.OSMZmiany;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.openstreetmap.OSMZmiany.Configuration.Profile;
import org.openstreetmap.OSMZmiany.DataContainer.Changeset;
import org.openstreetmap.OSMZmiany.DataContainer.Node;

public class DrawStyle {
	
	private static final long serialVersionUID = 383857840267103977L;
	private Changeset selCh;
	private Node selNode;
	
	
	public DrawStyle(){
	}
	
		
	public void drawNode(Graphics g,ZMapWidget map,Node node) {
		User user=map.dc.users.get(map.dc.changesets.get(map.dc.changesetsIndex.get(node.changesetId)).userId);
		Profile p=Configuration.instance.getSelectedProfile();
		User[] u=p.getUsers();
		//Blacklist
		if(p.getListType()==2||p.getListType()==0){
			for(int i=0;i<u.length;i++)
				if(u[i].id==user.id||p.getListType()==0){
					drawNodeP(g,map,node);
				}
		}else{
			boolean t=false;
			for(int i=0;i<u.length;i++)				
				if(u[i].id==user.id){
					t=true;
				}
			if(!t)
				drawNodeP(g,map,node);
		}		
	}
	private void drawNodeP(Graphics g,ZMapWidget map,Node node){
		switch(node.mode){
			case 0: {
				g.setColor(Color.BLUE);
				break;
			}
			case 1: {
				g.setColor(Color.GREEN);
				break;
			}
			case 2: {
				g.setColor(Color.RED);
				break;
			}
		}
		Point p = map.getMapPosition(node.lat,node.lon);
		if(p!=null){
			if(selCh!=null&&selCh.id==node.changesetId)
				g.drawOval(p.x - 2, p.y - 2, 4, 4);
			else if(selNode!=null&&selNode.id==node.id)
				g.drawOval(p.x - 2, p.y - 2, 4, 4);
			else
				g.drawRect(p.x - 2, p.y - 2, 4, 4);
		}
	}
	
	public void setSelection(Changeset ch){
		selNode=null;
		selCh=ch;
	}
	public void setSelection(Node node){
		selCh=null;
		selNode=node;
	}
	public Node getSelectedNode(){
		return selNode;
	}
	public Changeset getSelectedChangeset(){
		return selCh;
	}


}

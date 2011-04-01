package org.openstreetmap.OSMZmiany;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.openstreetmap.OSMZmiany.DataContainer.Changeset;
import org.openstreetmap.OSMZmiany.DataContainer.Node;

public class SelectedDrawStyle implements DrawStyle{
	private ZMapWidget map;
	
	private Changeset selCh;
	private Node selNode;
	
	public SelectedDrawStyle(){
	}
	
	public void setZMapWidget(ZMapWidget map){
		this.map=map;
	}
	
	public void drawNode(Graphics g,Node node) {
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
		map.refrashOverlay();
	}
	public void setSelection(Node node){
		selCh=null;
		selNode=node;
		map.refrashOverlay();
	}
	public Node getSelectedNode(){
		return selNode;
	}
	public Changeset getSelectedChangeset(){
		return selCh;
	}
}

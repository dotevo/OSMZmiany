package org.openstreetmap.OSMZmiany;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.GZIPInputStream;

import javax.swing.JFrame;

import org.openstreetmap.OSMZmiany.DataContainer.Changeset;
import org.openstreetmap.OSMZmiany.DataContainer.Node;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.DefaultMapController;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.interfaces.MapViewChangeListener;
import org.openstreetmap.gui.jmapviewer.interfaces.OverlayPainter;

import javax.swing.DefaultListModel;
import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

public class OSMZmiany extends JFrame implements MapViewChangeListener, MouseListener, OverlayPainter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2976479683829295126L;
	private JMapViewer map;
	public static OSMZmiany instance;
	private Timer refetchTimer;
	
	public DataContainer dc;
	private Image overlayI;
	
	
	JList list;
	private DefaultListModel model = new DefaultListModel();

	
	private int seqNum;
	
	//box selection:
	private boolean setMapsBounds=false;
	private Coordinate c1;
	private Coordinate c2;
	private JTextField textField;
	private JCheckBox chckbxAutoDiffDownload;
	

	public OSMZmiany() {
		dc=new DataContainer();
		this.setTitle("OSMZmiany");
		
		GridBagLayout gbl = new GridBagLayout();
		gbl.rowWeights = new double[]{1.0};
		gbl.columnWeights = new double[]{1.0};		
		
		getContentPane().setLayout(gbl);
		
		JSplitPane splitPane = new JSplitPane();
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 0;
		getContentPane().add(splitPane, gbc_splitPane);
		
		this.setSize(800, 800);
		
		//MAP
		map = new JMapViewer();
		map.addChangeListener(this);
		map.addOverlayPainter(this);
		map.addMouseListener(this);
		map.setSize(800, 800);		
				
		DefaultMapController mapController = new DefaultMapController(map);		
		mapController.setMovementMouseButton(MouseEvent.BUTTON2);		
		splitPane.setLeftComponent(map);
		
		JPanel panel = new JPanel();
		splitPane.setRightComponent(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("General", null, panel_1, null);
		
		final JButton btnUstawBox = new JButton("Set Box");
		btnUstawBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setMapsBounds=true;
			}
		});
		
		JButton btnRemoveBox = new JButton("Remove Box");
		btnRemoveBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dc.setNewDataFilter(null);
				c1=null;
				c2=null;
				makeOverlay();
			}
		});
		panel_1.setLayout(new MigLayout("", "[96px][118px,grow][87px]", "[24px][][][][][]"));
		
		JLabel lblSetBoundary = new JLabel("Set boundary:");
		panel_1.add(lblSetBoundary, "cell 0 0,alignx left,aligny center");
		panel_1.add(btnRemoveBox, "cell 1 0,alignx left,aligny top");
		
		panel_1.add(btnUstawBox, "cell 2 0,alignx left,aligny top");
		
		JLabel lblData = new JLabel("Data:");
		panel_1.add(lblData, "cell 0 1");
		
		JLabel lblDiffUrl = new JLabel("Diff URL");
		panel_1.add(lblDiffUrl, "cell 0 2,alignx trailing");
		
		textField = new JTextField();
		panel_1.add(textField, "cell 1 2,growx");
		textField.setColumns(10);
		
		JButton btnLoad = new JButton("Load");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getData(textField.getText());
			}
		});
		panel_1.add(btnLoad, "cell 2 2");
		
		chckbxAutoDiffDownload = new JCheckBox("Auto diff download");
		panel_1.add(chckbxAutoDiffDownload, "cell 1 3");
		
		JButton btnNewButton = new JButton("Clear");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dc.clear();	
				makeOverlay();
			}
		});
		panel_1.add(btnNewButton, "cell 2 4");
		panel.add(tabbedPane);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Changesets", null, panel_2, null);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.X_AXIS));
		
		JButton btnShowSite = new JButton("Info");
		panel_2.add(btnShowSite);
		
		list = new JList(model);
		panel_2.add(list);
		setVisible(true);

		overlayI = new BufferedImage(map.getWidth(), map.getHeight(),BufferedImage.TYPE_INT_ARGB);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		refetchTimer = new Timer();
		refetchTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if(chckbxAutoDiffDownload.isSelected())
					getData();
			}
		}, 20000, 30000);

	}

	private void initChangeStream() {
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(
							new BufferedInputStream(
									new URL(
											"http://planet.openstreetmap.org/minute-replicate/state.txt")
											.openStream())));
			br.readLine();
			String seqNumStr = br.readLine();
			seqNum = Integer.parseInt(seqNumStr.substring(seqNumStr
					.indexOf("=") + 1));
			br.readLine();
			br.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		}
	}

	public void getData() {		
			DecimalFormat myFormat = new DecimalFormat("000");
			String url = "http://planet.openstreetmap.org/minute-replicate/"
					+ myFormat.format(seqNum / 1000000) + "/"
					+ myFormat.format((seqNum / 1000) % 1000) + "/"
					+ myFormat.format(seqNum % 1000) + ".osc.gz";
			getData(url);
			seqNum++;			
	}
	
	public void getData(String url){
		try {
			BufferedInputStream bis = new BufferedInputStream(
					new GZIPInputStream(new URL(url).openStream()));
		
			dc.addData(bis);
			System.out.println("->"+dc.nodes.size());
			makeOverlay();
			reloadChangesets();			
		} catch (IOException ioe) {
			if (ioe instanceof FileNotFoundException) {
			} else {
				ioe.printStackTrace();
			}
		}
	}



	public void mapViewChanged() {
		makeOverlay();
		repaint();
	}
	 
	
	public void reloadChangesets(){
		model.clear();
		Iterator<Changeset> iterator = dc.changesets.iterator();
	    while (iterator.hasNext()) {
	    	Changeset ch=iterator.next();
	    	model.add(0, ch.id+":"+dc.users.get(ch.userId).name);  	
	    }
	}
	
	

	public void mouseClicked(MouseEvent me) {
		Iterator<Long> iterator = dc.nodes.keySet().iterator();
	    while (iterator.hasNext()) {
	    	Node node=dc.nodes.get(iterator.next());	    	
	    	Point p = map.getMapPosition(node.lat,node.lon);
	    	if(p!=null){	    		
	    		if(p.x<me.getX()+2&&p.x>me.getX()-2&&
	    		   p.y<me.getY()+2&&p.y>me.getY()-2){
	    			//YEAH!!! Killed
	    			openURL("http://www.openstreetmap.org/browse/changeset/"+node.changesetId);
	    			System.out.println(node.changesetId);
	    			return;	    			
	    		}
	    	}
	    }
	}

	public void mouseEntered(MouseEvent arg0) {		
	}

	public void mouseExited(MouseEvent arg0) {		
	}

	public void mousePressed(MouseEvent arg0) {
		if(setMapsBounds&&arg0.getButton()==1)
			c1=map.getPosition(arg0.getX(), arg0.getY());		
	}

	public void mouseReleased(MouseEvent arg0) {
		if(setMapsBounds&&arg0.getButton()==1){
			c2=map.getPosition(arg0.getX(), arg0.getY());
			
			//Corners
			Point p = map.getMapPosition(c1.getLat(),c1.getLon());
	    	Point p2 = map.getMapPosition(c2.getLat(),c2.getLon());
	    	if(p2.x<p.x&&p2.y<p.y){
	    		Coordinate c3=c1;
	    		c1=c2;
	    		c2=c3;	    		
	    	}else if(p2.x>p.x&&p2.y<p.y){
	    		Coordinate c1A=c1;
	    		Coordinate c2A=c2;	    		
	    		c1=new Coordinate(c2A.getLat(),c1A.getLon());
	    		c2=new Coordinate(c1A.getLat(),c2A.getLon());
	    	}else if(p2.x<p.x&&p2.y>p.y){
	    		Coordinate c1A=c1;
	    		Coordinate c2A=c2;	    		
	    		c1=new Coordinate(c1A.getLat(),c2A.getLon());
	    		c2=new Coordinate(c2A.getLat(),c1A.getLon());
	    	}
			
			MapFilter mf=new BoundaryMapFilter(c1.getLat(),c1.getLon(),c2.getLat(),c2.getLon());
			dc.setNewDataFilter(mf);
			dc.removeData(mf);
			setMapsBounds=false;
			makeOverlay();			
		}
	}
	
	
		
	public void makeOverlay(){
		overlayI = new BufferedImage(getWidth(), getHeight(),BufferedImage.TYPE_INT_ARGB);
		Graphics g = overlayI.getGraphics();
		g.setColor(Color.BLACK);
		
		Iterator<Long> iterator = dc.nodes.keySet().iterator();
	    while (iterator.hasNext()) {
	    	Node node=dc.nodes.get(iterator.next());
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
	    	if(p!=null)
	    		g.drawRect(p.x - 2, p.y - 2, 3, 3);
	    }
	    if(c1!=null&&c2!=null){	    	
	    	Point p = map.getMapPosition(c1.getLat(),c1.getLon());
	    	Point p2 = map.getMapPosition(c2.getLat(),c2.getLon());
	    	if(p!=null&&p2!=null){
	    		g.setColor(Color.orange);
    			g.drawRect(p.x, p.y,p2.x-p.x, p2.y-p.y);
	    	}
	    }
	    repaint();
	}
	
	
	public void paintOverlay(Graphics g) {
		if (overlayI != null) {
			g.drawImage(overlayI, 0, 0, null);
		}
	}
	
	public static void openURL(String url) {
		if( !java.awt.Desktop.isDesktopSupported() ) {
            System.err.println( "Desktop is not supported (fatal)" );
        }
        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        if( !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) {
            System.err.println( "Desktop doesn't support the browse action (fatal)" );
        }
       try {
           java.net.URI uri = new java.net.URI( url );
           desktop.browse( uri );
       }
       catch ( Exception e ) {
            System.err.println( e.getMessage() );
       }
    }
	
	public static void main(String[] args) {
		instance = new OSMZmiany();
		instance.initChangeStream();
		instance.setVisible(true);
	}
}

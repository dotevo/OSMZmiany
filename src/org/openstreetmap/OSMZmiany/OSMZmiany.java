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

public class OSMZmiany extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2976479683829295126L;	
	
	public static OSMZmiany instance;
	
	private Timer refetchTimer;
	private int seqNum;
		
	public DataContainer dc;
	
	
	//Widgets
	private ZMapWidget map;
	private JTextField textField;
	private JCheckBox chckbxAutoDiffDownload;
	private JList list;
	private DefaultListModel model = new DefaultListModel();


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
		map = new ZMapWidget(dc);
		map.setSize(400, 400);		
				
				
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
				map.setBBox();
			}
		});
		
		JButton btnRemoveBox = new JButton("Remove Box");
		btnRemoveBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				map.removeBBox();
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
		} catch (IOException ioe) {
			if (ioe instanceof FileNotFoundException) {
			} else {
				ioe.printStackTrace();
			}
		}
	}


	public void reloadChangesets(){
		model.clear();
		Iterator<Changeset> iterator = dc.changesets.iterator();
	    while (iterator.hasNext()) {
	    	Changeset ch=iterator.next();
	    	model.add(0, ch.id+":"+dc.users.get(ch.userId).name);  	
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

package org.openstreetmap.OSMZmiany;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;


public class Configuration implements Serializable {
	class Profile implements Serializable{
		private static final long serialVersionUID = 1L;
		//Type of list true=whitelist;false=blacklist
		private boolean listType=true;
		private ArrayList<User> users=new ArrayList<User>();
		//Show data
		//-1=ALL;0=created;1=modified;2=deleted
		private short showType=-1;
		private String name;
		private Configuration conf;
		
		private MapFilter mapfilter;
		
		public Profile(String name){
			this.name=name;
		}
		public void setConfList(Configuration a){
			conf=a;
		}
		//SETTERS
		public void setMapFilter(MapFilter mf){
			mapfilter=mf;
			if(conf!=null)
				conf.sendProfileChanged(this);			
		}		
		public void setShowType(short type){
			showType=type;
			if(conf!=null)
				conf.sendProfileChanged(this);			
		}
		public void setListType(boolean type){
			listType=type;
			if(conf!=null)
				conf.sendProfileChanged(this);		
		}
		public void setName(String name){
			this.name=name;
			if(conf!=null)
				conf.sendProfileChanged(this);			
		}
		//GETTERS
		public MapFilter getMapFilter(){
			return mapfilter;
		}		
		public short getShowType(){
			return showType;
		}
		public boolean getListType(){
			return listType;
		}
		public String getName(){
			return name;
		}
		//USERS
		public void addUser(User user){
			//if it doesn't exist on list
			boolean is=false;
			for(int i=0;i<users.size();i++)
				if(user.equals(users.get(i)))
					is=true;
			if(is==false)
				users.add(user);
		}
		public void removeUser(User user){
			for(int i=0;i<users.size();){
				if(user.equals(users.get(i)))
					users.remove(i);
				i++;
			}
		}
		
		public User[] getUsers(){
			//TODO sort
			User []user=new User[users.size()];
			for(int i=0;i<user.length;i++){
				user[i]=users.get(i);
			}
			return user;
		}
	}
	
	private static final long serialVersionUID = 1L;	
	private ArrayList<Profile> profiles=new ArrayList<Profile>();
	private int selectedProfile=0;
	private ArrayList<ConfigurationListener> configurationListeners=new ArrayList<ConfigurationListener>();
	
	public Configuration(){
		//default
		Profile p=new Profile("My profile");
		//p.drawStyle=new SelectedDrawStyle();
		p.mapfilter=null;
		profiles.add(p);
	}
	
	public void addProfile(Profile profile){
		//TODO NAME TEST
		profiles.add(profile);
		profile.setConfList(this);
	}
	
	public void removeProfile(String name){
		//TODO Function
	}
	
	public Profile[] getProfiles(){
		//TODO Sort
		Profile []pros=new Profile[profiles.size()];
		for(int i=0;i<pros.length;i++){
			pros[i]=profiles.get(i);
		}
		return pros;
	}
	
	public Profile getSelectedProfile(){
		return profiles.get(selectedProfile);
	}
	
	public void selectProfile(Profile p){
		int z=profiles.lastIndexOf(p);
		if(z!=-1){
			selectedProfile=z;
			sendProfileChanged(profiles.get(selectedProfile));
		}
	}
	
	public void sendProfileChanged(Profile z){
		for(int j=0;j<configurationListeners.size();j++)
			configurationListeners.get(j).profileChanged(z);
	}
	
	public void addConfigurationListener(ConfigurationListener cl){
		configurationListeners.add(cl);
	}

	public static Configuration instance;
	//////////////////////////LOAD AND SAVE SECTION///////////////////////////////
	private static Configuration loadFromFile(String name) throws IOException, NotSerializableException, ClassNotFoundException{
	    ObjectInputStream ois= new ObjectInputStream(new FileInputStream(name));
	    Configuration c=(Configuration) ois.readObject();
	    ois.close();
		return c;
	}
	
	public static Configuration loadFromFile(){
		//TODO CROSSPLATFORM HOMEDIR
		try {
			instance=loadFromFile(".OSMZMIANY_CONFIG");
			return instance;
		} catch (NotSerializableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		//Create default configuration
		Configuration c=new Configuration();		
		return c;
	}
	
	private void saveToFile(String filename) throws FileNotFoundException, IOException{
		//Clear listeners		
		configurationListeners.clear();
		
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
        oos.writeObject(this);
        oos.close();
	}
	
	public void saveToFile(){
		//TODO CROSSPLATFORM HOMEDIR
		try {
			saveToFile(".OSMZMIANY_CONFIG");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}

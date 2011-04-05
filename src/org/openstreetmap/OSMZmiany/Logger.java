package org.openstreetmap.OSMZmiany;

public class Logger {
	public static boolean printStackTrace_=true;
	
	public static void printStackTrace(Exception e,String msg){
		System.err.println(msg);
		if(printStackTrace_){
			e.printStackTrace();			
		}		
	}
}

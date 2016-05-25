package org.apodhrad.p2diff;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

class Resource {
	public static URL getResource(String path) {
		return new Resource().getFile(path);
	}
	
	public URL getFile(String path)
	{
		return getClass().getClassLoader().getResource(path);
	}
	
	
	/**
	 * Get list of files from a folder and its sub-folders
	 * @param directoryName
	 * @param store
	 */
	public static void listf(String directoryName, ArrayList<File> store) {
	    File directory = new File(directoryName);
	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
	        if (file.isFile()) {
	            store.add(file);
	        } else if (file.isDirectory()) {
	        	store.add(file);
	            listf(file.getPath(), store);
	        }
	    }
	}
}

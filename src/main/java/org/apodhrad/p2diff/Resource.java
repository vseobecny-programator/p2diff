package org.apodhrad.p2diff;

import java.net.URL;

class Resource {
	public static URL getResource(String path) {
		return DiffForFile.class.getClassLoader().getResource(path);
	}
}

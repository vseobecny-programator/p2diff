package org.apodhrad.p2diff;

import java.io.File;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class DiffForZip {
	
	private String source;
	
	DiffForZip(String source1, String source2) throws ZipException {
		this.source = source;
		
		ZipFile zipFile = new ZipFile((new File(org.apodhrad.p2diff.Resource.getResource(source).getFile())));
	    zipFile.extractAll(source + ".temp");
	}
}

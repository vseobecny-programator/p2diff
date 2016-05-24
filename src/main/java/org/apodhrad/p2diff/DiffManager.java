package org.apodhrad.p2diff;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.google.common.io.Files;

import freemarker.template.TemplateException;

public class DiffManager {
	
	private File zip1;
	private File zip2;
	private Map<String, Object> diff = new HashMap<String, Object>();
	
	public final static int SAME = 0;
	public final static int DELETED = 1;
	public final static int ADDED = 2;
	public final static int RENAMED = 3;
	
	DiffManager(File zip1, File zip2)
	{
		this.zip1 = zip1;
		this.zip2 = zip2;
	}
	
	private String getPath(String path, File resource)
	{
		return path.replace(resource.getParent() + "/", "");
	}
	
	private boolean compare(File file1, File file2) throws IOException
	{
		if (!file1.isDirectory() && !file2.isDirectory())
			return Files.asByteSource(file1).contentEquals(Files.asByteSource(file2));
		else
			return file1.getName().equals(file2.getName());
	}
	
	private void generateDiff() throws Exception
	{
		ArrayList<File> filesInZip1 = new ArrayList<File>();
		ArrayList<File> filesInZip2 = new ArrayList<File>();;
		
		Resource.listf(zip1.getParent(), filesInZip1);
		Resource.listf(zip2.getParent(), filesInZip2);
		

		for (File file1 : filesInZip1) {
			for (File file2 : filesInZip2) {
				
				if (file1.getName().equals(file2.getName())) {
					if (compare(file1, file2)) {
						
						if (getPath(file1.getPath(), zip1).equals(getPath(file2.getPath(), zip2))) {
							diff.put(getPath(file1.getPath(), zip1), SAME);
						} else
							diff.put(getPath(file1.getPath(), zip1), RENAMED);
					}
				}
				
			    if ((FilenameUtils.getExtension(file1.getPath()).equals("jar")) && (FilenameUtils.getExtension(file2.getPath()).equals("jar"))) {
					String[] onlyNameF1 = getPath(file1.getPath(), zip1).split("_");
					String[] onlyNameF2 = getPath(file1.getPath(), zip1).split("_");
					
					if (onlyNameF1[0].equals(onlyNameF2[0])) {
						diff.put(getPath(file1.getPath(), zip1), SAME);
						//diff.put(getPath(file1.getPath(), zip1), (new DiffForJar(file1.getPath(), file2.getPath()).generateHTML("layout")));
						FileUtils.writeStringToFile(new File("target/" + getPath(file1.getPath(), zip1) + "-diff.html"), (new DiffForJar(file1.getPath(), file2.getPath()).generateHTML("layout")));
					}
			    }
			    
				if (!diff.containsKey(getPath(file1.getPath(), zip1)))
					diff.put(getPath(file1.getPath(), zip1), DELETED);
			}
		}
		
		for (File file2 : filesInZip2) {
			for (File file1 : filesInZip1) {
				if (!diff.containsKey(getPath(file2.getPath(), zip2)))
					diff.put(getPath(file2.getPath(), zip2), ADDED);
			}
		}
	}
	
	public String generateHTML() throws Exception
	{
		generateDiff();
		
		HTMLGenerator generator = new HTMLGenerator(diff);
		return generator.generateHTML("layout.html");
	}
}

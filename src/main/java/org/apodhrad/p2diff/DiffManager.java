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
	private String filter;
	private Map<String, Object> diff = new HashMap<String, Object>();
	private Map<File, File> prepared = new HashMap<File, File>();
	
	DiffManager(File zip1, File zip2)
	{
		this(zip1, zip2, "(.*)");
	}
	
	DiffManager(File zip1, File zip2, String filter)
	{
		this.zip1 = zip1;
		this.zip2 = zip2;
		this.filter = filter;
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
				System.out.println(file1.getName() + " - " + zip1.getName());
				if (!(file1.getName().equals(zip1.getName()) || file2.getName().equals(zip2.getName()))) {
					if (!(file1.isDirectory() || file2.isDirectory())) {
						if (file1.getName().equals(file2.getName())) {
							if (compare(file1, file2)) {
								if (getPath(file1.getPath(), zip1).equals(getPath(file2.getPath(), zip2))) {
									diff.put(getPath(file1.getPath(), zip1), Status.SAME);
								} else
									diff.put(getPath(file1.getPath(), zip1), Status.RENAMED);
							}
						}
						
					    if ((FilenameUtils.getExtension(file1.getPath()).equals("jar")) && (FilenameUtils.getExtension(file2.getPath()).equals("jar"))) {
							String[] onlyNameF1 = getPath(file1.getPath(), zip1).split("_");
							String[] onlyNameF2 = getPath(file2.getPath(), zip2).split("_");
							
							if (onlyNameF1[0].equals(onlyNameF2[0])) {
								if (file1.getName().matches(filter)) {
									prepared.put(file1, file2);
									diff.put(getPath(file1.getPath(), zip1), Status.PREPARED);
								} else {
									diff.put(getPath(file1.getPath(), zip1), Status.SAME);
								}
							}
					    }
					}
				}
			}
		}
		
		for (File file1 : filesInZip1) {
			if (!(file1.getName().equals(zip1.getName()))) {
				if (!(file1.isDirectory())) {	
					if (!diff.containsKey(getPath(file1.getPath(), zip1))) {
						diff.put(getPath(file1.getPath(), zip1), Status.DELETED);
					}
				}
			}
		}
		
		for (File file2 : filesInZip2) {
			if (!(file2.getName().equals(zip2.getName()))) {
				if (!(file2.isDirectory())) {
					if (!diff.containsKey(getPath(file2.getPath(), zip2)))
						diff.put(getPath(file2.getPath(), zip2), Status.ADDED);
				}
			}
		}
	}
	
	public String generateHTML() throws Exception
	{
		generateDiff();
		
		prepared.forEach((file1, file2) -> {
			try {
				File file = new File("target/diffs/" + getPath(file1.getPath(), zip1) + "-diff.html");
				FileUtils.writeStringToFile(file, (new DiffForJar(file1.getPath(), file2.getPath(), file).generateHTML("inner_layout")));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		HTMLGenerator generator = new HTMLGenerator(diff);
		return generator.generateHTML("layout.html");
	}
}

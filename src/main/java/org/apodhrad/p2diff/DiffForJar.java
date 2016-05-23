package org.apodhrad.p2diff;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apodhrad.jdownload.manager.util.UnpackUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class DiffForJar {
	
	/**
	 * Path to the original file
	 */
	private String originalResource;
	
	/**
	 * Path to the revised file
	 */
	private String revisedResource;
	
	private Map<String, Object> diff = new HashMap<String, Object>();
	private String htmlDiff = "";
	
	private Configuration cfg;
	
	final static int SAME = 0;
	final static int DELETED = 1;
	final static int ADDED = 2;
	public String template = "layout.html";
	

	public DiffForJar(String originalPath, String revisedPath) throws Exception
	{
		this.originalResource = originalPath;
		this.revisedResource = revisedPath;
		
		ArrayList<File> files1 = extractJar(new File(originalResource).getAbsolutePath() + ".temp", this.originalResource);
		ArrayList<File> files2 = extractJar(new File(revisedResource).getAbsolutePath() + ".temp", this.revisedResource);
		
		diff = getDiffFromFiles(files1, files2);
	}
	
	
	/**
	 * Extract JAR archive to a folder and return its files
	 * @param dest
	 * @param jar
	 * @return
	 * @throws IOException
	 */
	private ArrayList<File> extractJar(String dest, String jar) throws IOException
	{
		//UnzipJar uj = new UnzipJar(new File(dest).getPath(), jar);
		UnpackUtils.unpack(jar, dest);
		ArrayList<File> store = new ArrayList<File>();
		Resource.listf(new File(dest).getAbsolutePath(), store);
		return store;
	}
	
	private Map convertArrayToMap(Object[] array) {
		Map<Integer, Object> map = new HashMap<>();
		
		for (int i=0; i < array.length; i++)
			map.put(i, array[i]);
		
		return map;
	}
	
	private String getName(File file, String resource)
	{
		return file.getPath().replace(new File(resource).getAbsolutePath() + ".temp/", "");
	}
	
	private Map getDiffFromFiles(ArrayList<File> files1, ArrayList<File> files2) throws Exception
	{
		for (int i=0; i < files1.size(); i++) {
			for (int y = 0; y < files2.size(); y++) {
				if (getName(files1.get(i), originalResource).equals(getName(files2.get(y), revisedResource))) {
					if (files1.get(i).isDirectory() || files2.get(y).isDirectory()) {
						if (getName(files1.get(i), originalResource).equals(getName(files2.get(y), revisedResource))) {
							diff.put(getName(files1.get(i), originalResource), DiffForJar.SAME);
						}
					} else {
						System.out.println(files1.get(i) + " - " + files2.get(y));
						//DiffForFile dff = new DiffForFile(files1.get(i).getAbsolutePath(), files2.get(y).getAbsolutePath());
						//dff.setTag("span");
						//String generated = dff.generate();
						String generated = "";
						if (generated.equals(DiffForFile.NO_CHANGE))
							diff.put(getName(files1.get(i), originalResource), DiffForJar.SAME);
						else
							//diff.put(getName(files1.get(i), originalResource), dff.generate());
							diff.put(getName(files1.get(i), originalResource), DiffForJar.SAME);
					}
				}
			}

			if (!diff.containsKey(getName(files1.get(i), originalResource))) {
				diff.put(getName(files1.get(i), originalResource), DiffForJar.DELETED);
			}
		}
		
		for (int y = 0; y < files2.size(); y++) {		
			if (!diff.containsKey(getName(files2.get(y), revisedResource)))
				 diff.put(getName(files2.get(y), revisedResource), DiffForJar.ADDED);
		}

		return diff;
	}
	
	public String generateHTML(String template) throws IOException, TemplateException
	{
		HTMLGenerator generator = new HTMLGenerator(diff);
		return generator.generateHTML(template + ".html");
	}
}

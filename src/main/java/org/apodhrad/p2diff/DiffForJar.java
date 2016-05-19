package org.apodhrad.p2diff;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class DiffForJar {
	
	/**
	 * Path to the original file
	 */
	private URL originalResource;
	
	/**
	 * Path to the revised file
	 */
	private URL revisedResource;
	
	private Map<String, Object> diff = new HashMap<String, Object>();
	private String htmlDiff = "";
	
	private Configuration cfg;
	
	final static int SAME = 0;
	final static int DELETED = 1;
	final static int ADDED = 2;
	public String template = "layout.html";
	
	DiffForJar(String originalPath, String revisedPath) throws Exception
	{
		this.originalResource = Resource.getResource(originalPath);
		this.revisedResource = Resource.getResource(revisedPath);
		ArrayList<File> files1 = extractJar(new File(originalResource.getFile()).getAbsolutePath() + ".temp", this.originalResource.getFile());
		ArrayList<File> files2 = extractJar(new File(revisedResource.getFile()).getAbsolutePath() + ".temp", this.revisedResource.getFile());
		
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
		UnzipJar uj = new UnzipJar(dest, jar);
		ArrayList<File> store = new ArrayList<File>();
		listf(new File(dest).getAbsolutePath(), store);
		
		return store;
	}
	
	/**
	 * Get list of files from a folder and its sub-folders
	 * @param directoryName
	 * @param store
	 */
	public void listf(String directoryName, ArrayList<File> store) {
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
	
	private Map convertArrayToMap(Object[] array) {
		Map<Integer, Object> map = new HashMap<>();
		
		for (int i=0; i < array.length; i++)
			map.put(i, array[i]);
		
		return map;
	}
	
	private String getName(File file, URL resource)
	{
		return file.getPath().replace(new File(resource.getFile()).getAbsolutePath() + ".temp/", "");
	}
	
	private Map getDiffFromFiles(ArrayList<File> files1, ArrayList<File> files2) throws Exception
	{
		for (int i=0; i < files1.size(); i++) {
			for (int y = 0; y < files2.size(); y++) {
				if (getName(files1.get(i), originalResource).equals(getName(files2.get(y), revisedResource))) {
					if (files1.get(i).isDirectory() && files2.get(y).isDirectory()) {
						if (getName(files1.get(i), originalResource).equals(getName(files2.get(y), revisedResource))) {
							diff.put(getName(files1.get(i), originalResource), DiffForJar.SAME);
						}
					} else {
						DiffForFile dff = new DiffForFile(files1.get(i).getAbsolutePath(), files2.get(y).getAbsolutePath());
						dff.setTag("span");
						String generated = dff.generate();
						
						if (generated.equals(DiffForFile.NO_CHANGE))
							diff.put(getName(files1.get(i), originalResource), DiffForJar.SAME);
						else
							diff.put(getName(files1.get(i), originalResource), dff.generate());
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
	
	public String generateHTML() throws IOException, TemplateException
	{
		configurateTemplateSystem();
		convertToHTML(diff);
		Template temp = cfg.getTemplate(template);

		Map<String, Object> root = new HashMap<String, Object>();
		root.put("diff", htmlDiff);
		
		StringWriter sw = new StringWriter();
		temp.process(root, sw);
		
		return sw.toString();
	}
	
	private void convertToHTML(Map<String, Object> diff)
	{		
		htmlDiff = "<table>";
		diff.forEach((key, value) -> {
			if (value.equals(0))
				htmlDiff += "<tr class=\"same\"><td>" + key + "</td><td>Hasn't changed</td></tr>\n";
			else if (value.equals(1))
				htmlDiff += "<tr class=\"deleted\"><td>" + key + "</td></tr>\n";
			else if (value.equals(2))
				htmlDiff += "<tr class=\"added\"><td>" + key + "</td></tr>\n";
			else {
				htmlDiff += "<tr class=\"same\"><td>" + key + "</td>\n";
				htmlDiff += "<td id=\""+ key +"-toggle\">show</td></tr>";
				htmlDiff += "<tr><td class=\"hidden\"><pre id=\""+ key + "-hidden\">\n";
				htmlDiff += value;
				htmlDiff += "</pre></td></tr>\n";
			}
		});
		htmlDiff += "</table>";
	}
	
	private void configurateTemplateSystem() throws IOException
	{
		cfg = new Configuration(Configuration.VERSION_2_3_22);
		cfg.setDefaultEncoding("UTF-8");
		cfg.setDirectoryForTemplateLoading(new File(Resource.getResource(".").getFile()));
	}
}

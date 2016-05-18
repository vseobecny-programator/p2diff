package org.apodhrad.p2diff;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import freemarker.template.Configuration;
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
	
	DiffForJar(String originalPath, String revisedPath) throws Exception
	{
		this.originalResource = Resource.getResource(originalPath);
		this.revisedResource = Resource.getResource(revisedPath);
		
		File[] files1 = extractJar(new File(this.originalResource.getFile()).getName(), this.originalResource.getFile());
		File[] files2 = extractJar(new File(this.revisedResource.getFile()).getName(), this.revisedResource.getFile());
		
		diff = getDiffFromFiles(files1, files2);
		System.out.println(diff);
	}
	
	private File[] extractJar(String dest, String jar) throws IOException
	{
		UnzipJar uj = new UnzipJar(dest, jar);
		return new File(dest).listFiles();
	}
	
	private Map convertArrayToMap(Object[] array) {
		Map<Integer, Object> map = new HashMap<>();
		
		for (int i=0; i < array.length; i++)
			map.put(i, array[i]);
		
		return map;
	}
	
	private String getName(File file, URL resource)
	{
		return file.getPath().replace(new File(resource.getFile()).getName() + "/", "");
	}
	
	private Map getDiffFromFiles(File[] files1, File[] files2) throws Exception
	{
		for (int i=0; i < files1.length; i++) {
			
			int added = 1;
			
			for (int y = 0; y < files2.length; y++) {
				if (getName(files1[i], originalResource).equals(getName(files2[y], revisedResource))) {
					if (files1[i].isDirectory() && files2[y].isDirectory()) {
						getDiffFromFiles(files1[i].listFiles(), files2[y].listFiles());
						diff.put(getName(files1[i], originalResource), DiffForJar.SAME);
					} else {
						DiffForFile dff = new DiffForFile(files1[i].getAbsolutePath(), files2[y].getAbsolutePath());
						String generated = dff.generate();
						
						if (generated.equals(DiffForFile.NO_CHANGE))
							diff.put(getName(files1[i], originalResource), DiffForJar.SAME);
						else
							diff.put(getName(files1[i], originalResource), dff.generate());
					}
				}
			}

			if (!diff.keySet().contains(getName(files1[i], originalResource)))
				diff.put(getName(files1[i], revisedResource), DiffForJar.DELETED);
		}
		
		for (int y = 0; y < files2.length; y++) {		
			if (diff.containsKey(getName(files2[y], originalResource)))
				 diff.put(getName(files2[y], revisedResource), DiffForJar.ADDED);
		}
		
		return diff;
	}
	
	public void generateHTML() throws IOException
	{
		configurateTemplateSystem();
		convertToHTML(diff);
	}
	
	private void convertToHTML(Map<String, Object> diff)
	{	
		diff.forEach((key, value) -> {
			
		});
	}
	
	private void configurateTemplateSystem() throws IOException
	{
		cfg = new Configuration(Configuration.VERSION_2_3_22);
		cfg.setDefaultEncoding("UTF-8");
		cfg.setDirectoryForTemplateLoading(new File(Resource.getResource(".").getFile()));
	}
}

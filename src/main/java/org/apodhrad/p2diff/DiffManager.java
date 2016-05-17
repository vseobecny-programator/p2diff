package org.apodhrad.p2diff;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.*;

import difflib.DiffUtils;
import difflib.Patch;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class DiffManager {
	
	/**
	 * Path to the original file
	 */
	private String originalPath;
	
	/**
	 * Path to the revised file
	 */
	private String revisedPath;
	
	private List<String> originalLines;
	private List<String> revisedLines;
	
	/**
	 * Name of the tag which wraps the lines of a diff
	 */
	private String tag = "span";
	
	private List<String> unifiedDiff;
	private String title = "Diff View";
	private String cssPath = "css/style.css";
	
	private Configuration cfg;
	
	/**
	 * Assign paths and load files
	 * @param originalPath
	 * @param revisedPath
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	DiffManager(String originalPath, String revisedPath) throws IOException, URISyntaxException
	{
		URL originalResource = getResource(originalPath);
		URL revisedResource = getResource(revisedPath); 
		
		originalLines = FileUtils.readLines(new File(originalResource.getFile()));
		revisedLines = FileUtils.readLines(new File(revisedResource.getFile()));
	}
	
	/**
	 * Converts diff format to HTML
	 * @param unifiedDiff
	 * @return
	 */
	private String convertToHTML(String unifiedDiff)
	{
		String result;
		
		if (unifiedDiff.startsWith("+++") || unifiedDiff.startsWith("---"))
			result = generateTag("info", unifiedDiff);
		else if (unifiedDiff.startsWith("@@") && unifiedDiff.endsWith("@@"))
			result = generateTag("range", unifiedDiff);
		else if (unifiedDiff.startsWith("+"))
			result = generateTag("added", unifiedDiff);
		else if (unifiedDiff.startsWith("-"))
			result = generateTag("deleted", unifiedDiff);
		else
			result = unifiedDiff;
		
		return result;
	}
	
	/**
	 * Generates TAG
	 * @param className Name of a CSS class
	 * @param content Tag content
	 * @return
	 */
	private String generateTag(String className, String content)
	{
		return "<" + tag + " class=\"" + className + "\">" + content + "</" + tag + ">";
	}
	
	/**
	 * Generate HTML file
	 * @return
	 * @throws IOException 
	 * @throws TemplateException 
	 */
	public String generate() throws IOException, TemplateException
	{
		Patch patch = DiffUtils.diff(originalLines, revisedLines);
		unifiedDiff = DiffUtils.generateUnifiedDiff(originalPath, revisedPath, originalLines, patch, 1);
		List<String> htmlDiff = new ArrayList<String>();
		
		for (String line : unifiedDiff) {
			htmlDiff.add(this.convertToHTML(line));
		}
		
		configurateTemplate();
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("title", title);
		root.put("diff", htmlDiff);
		root.put("cssPath", cssPath);
		
		Template tmp = cfg.getTemplate("layout.html");
		
		StringWriter out = new StringWriter();
    	tmp.process(root, out);
    	
		return out.toString();
	}
	
	public URL getResource(String path) {
		return DiffManager.class.getClassLoader().getResource(path);
	}
	
	private void configurateTemplate() throws IOException
	{
		cfg = new Configuration(Configuration.VERSION_2_3_22);
		cfg.setDirectoryForTemplateLoading(new File(getResource("").getFile()));
		cfg.setDefaultEncoding("UTF-8");
	}
	
	//// SETTERS AND GETTERS
	
	public void setTag(String tag)
	{
		this.tag = tag; 
	}
	
	public void setTitle(String title) 
	{
		this.title = title;
	}
	
	public void setCssPath(String cssPath) 
	{
		this.cssPath = cssPath;
	}
	
	public String getTag()
	{
		return this.tag;
	}
	
	public List<String> getDiff()
	{
		return unifiedDiff;
	}
}

package org.apodhrad.p2diff;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.*;

import difflib.DiffUtils;
import difflib.Patch;

public class DiffManager {
	
	/**
	 * Cesta k původnímu souboru
	 */
	private String originalPath;
	
	/**
	 * Cesta k upravenému souboru
	 */
	private String revisedPath;
	
	private List<String> originalLines;
	private List<String> revisedLines;
	
	/**
	 * Jméno tagu, kterým se budou obalovat řádky
	 */
	private String tag = "span";
	
	private List<String> unifiedDiff;
	private String headerPath = "target/header.html";
	private String footerPath = "target/footer.html";
	
	/**
	 * Přiřaď cesty a načti soubory
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
	 * Převede Diff formát do HTML
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
	 * Vygeneruj tag
	 * @param className Název CSS třídy
	 * @param content Obsah tagu
	 * @return
	 */
	private String generateTag(String className, String content)
	{
		return "<" + tag + " class=\"" + className + "\">" + content + "</" + tag + ">";
	}
	
	/**
	 * Vygeneruj HTML soubor
	 * @return
	 * @throws IOException 
	 * @throws TemplateException 
	 */
	public List<String> generate() throws IOException
	{
		Patch patch = DiffUtils.diff(originalLines, revisedLines);
		unifiedDiff = DiffUtils.generateUnifiedDiff(originalPath, revisedPath, originalLines, patch, 1);
		List<String> htmlDiff = new ArrayList<String>();
		
		File header = new File(headerPath);
		htmlDiff.addAll(FileUtils.readLines(header));
		
		for (String line : unifiedDiff) {
			htmlDiff.add(this.convertToHTML(line));
		}
		
		File footer = new File(footerPath);
		htmlDiff.addAll(FileUtils.readLines(footer));
		
		return htmlDiff;
	}
	
	/**
	 * Získej zdroj
	 * @param path Cesta k souboru
	 * @return
	 */
	public URL getResource(String path) {
		return DiffManager.class.getClassLoader().getResource(path);
	}
	
	//// SETTERY A GETTERY
	
	public void setTag(String tag)
	{
		this.tag = tag; 
	}
	
	public void setHeader(String headerPath)
	{
		this.headerPath = headerPath;
	}
	
	public void setFooter(String footerPath)
	{
		this.footerPath = footerPath;
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

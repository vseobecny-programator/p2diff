package org.apodhrad.p2diff;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class HTMLGenerator {
	
	private Map<String, Object> diff = new HashMap<String, Object>();
	private Configuration cfg;
	private String htmlDiff;
	
	HTMLGenerator(Map<String, Object> diff) {
		this.diff = diff;
	}
	public String generateHTML(String template) throws IOException, TemplateException
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
			if (value.equals(DiffManager.SAME))
				htmlDiff += "<tr class=\"same\"><td>" + key + "</td></tr>\n";
			else if (value.equals(DiffManager.DELETED))
				htmlDiff += "<tr class=\"deleted\"><td>" + key + "</td></tr>\n";
			else if (value.equals(DiffManager.ADDED))
				htmlDiff += "<tr class=\"added\"><td>" + key + "</td></tr>\n";
			else if (value.equals(DiffManager.RENAMED))
				htmlDiff += "<tr class=\"renamed\"><td>" + key + "</td></tr>\n";
			else {
				htmlDiff += "<tr class=\"same\"><td>" + key + "</td></tr>\n";
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

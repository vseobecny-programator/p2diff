package org.apodhrad.p2diff;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import freemarker.template.TemplateException;

public class DiffForJarTest {
	
	@Test
	public void test() throws Exception
	{
		DiffForJar dfj = new DiffForJar("640.jar", "641.jar");
		ArrayList<String> htmlLines = new ArrayList<String>();
		htmlLines.add(dfj.generateHTML());
		
		FileUtils.writeLines(new File("target/diff.html"), htmlLines);
	}
}

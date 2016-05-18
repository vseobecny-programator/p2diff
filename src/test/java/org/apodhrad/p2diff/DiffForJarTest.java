package org.apodhrad.p2diff;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

import freemarker.template.TemplateException;

public class DiffForJarTest {
	
	@Test
	public void test() throws Exception
	{
		DiffForJar dfj = new DiffForJar("640.jar", "test.jar");
		dfj.generateHTML();
	}
}

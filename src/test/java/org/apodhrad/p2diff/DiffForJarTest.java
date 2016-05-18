package org.apodhrad.p2diff;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apodhrad.jdownload.manager.JDownloadManager;
import org.apodhrad.jdownload.manager.hash.MD5Hash;
import org.junit.Test;

import freemarker.template.TemplateException;
import net.lingala.zip4j.core.ZipFile;

public class DiffForJarTest {
	@Test
	public void test() throws Exception
	{
		JDownloadManager jdm = new JDownloadManager();
		
		File zip1 = jdm.download("http://download.jboss.org/jbosstools/mars/development/updates/integration-stack/jbosstools-integration-stack-4.3.0.Beta1-earlyaccess.zip", new File("target/beta"), true, new MD5Hash("51d16f5daf6aab209a9f3fc7d4ce41c4"));
		File zip2 = jdm.download("http://download.jboss.org/jbosstools/mars/stable/updates/integration-stack/jbosstools-integration-stack-4.3.0.Final.zip", new File("target/final"), true, new MD5Hash("85a2a5079de5e1525872e2537b9eb8e6"));
	
		DiffForJar dfj = new DiffForJar(zip1.getParent() + "/plugins/org.drools.eclipse.source_6.4.0.201601201107.jar", zip2.getParent() + "/plugins/org.drools.eclipse.source_6.4.1.Final-v20160503-1355-B205.jar");
		ArrayList<String> htmlLines = new ArrayList<String>();
		htmlLines.add(dfj.generateHTML());
		
		FileUtils.writeLines(new File("target/diff.html"), htmlLines);
	}
}

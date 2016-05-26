package org.apodhrad.p2diff;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apodhrad.jdownload.manager.JDownloadManager;
import org.apodhrad.jdownload.manager.hash.MD5Hash;
import org.apodhrad.jdownload.manager.util.UnpackUtils;
import org.junit.Test;

public class DiffManagerTest {
	
	@Test
	public void general() throws IOException, Exception
	{
		JDownloadManager jdm = new JDownloadManager();
		
		File target1 = new File("target/beta");
		File target2 = new File("target/final");
		
		File zip1 = jdm.download("http://download.jboss.org/jbosstools/mars/development/updates/integration-stack/jbosstools-integration-stack-4.3.0.Beta1-earlyaccess.zip", target1, false, new MD5Hash("51d16f5daf6aab209a9f3fc7d4ce41c4"));
		File zip2 = jdm.download("http://download.jboss.org/jbosstools/mars/stable/updates/integration-stack/jbosstools-integration-stack-4.3.0.Final.zip", target2, false, new MD5Hash("85a2a5079de5e1525872e2537b9eb8e6"));
		File zip2ea = jdm.download("http://download.jboss.org/jbosstools/mars/stable/updates/integration-stack/jbosstools-integration-stack-4.3.0.Final-earlyaccess.zip", target2, false);
		
		File unpackZip1 = new File(target1.getPath() + "/zip1-ext");
		File unpackZip2 = new File(target2.getPath() + "/zip2-ext");
		
		UnpackUtils.unpack(zip1, unpackZip1);
		UnpackUtils.unpack(zip2, unpackZip2);
		UnpackUtils.unpack(zip2ea, unpackZip2);
		//File zip2 = jdm.download("http://java-sourcecode.weebly.com/uploads/7/3/2/6/7326864/library_ma218756952010.zip", new File("target/beta"), true);
		//File zip1 = jdm.download("http://java-sourcecode.weebly.com/uploads/7/3/2/6/7326864/memory_mon2191991142010.zip", new File("target/final"), true);
		
		File diff = new File("target/diffs/diff.html");
		DiffManager dm = new DiffManager(unpackZip1, unpackZip2, "(.*)kie(.*)", diff);
		FileUtils.writeStringToFile(diff, dm.generateHTML());
	}
}

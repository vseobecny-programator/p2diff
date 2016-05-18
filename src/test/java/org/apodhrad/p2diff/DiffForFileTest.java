package org.apodhrad.p2diff;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

public class DiffForFileTest {
	
	private DiffForFile dm;
	
	@Test
	public void generateHTMLTest() throws Exception
	{
		//System.out.println(Resource.getResource("lorem.txt").getFile());
		dm = new DiffForFile(Resource.getResource("lorem.txt").getFile(), Resource.getResource("lorem2.txt").getFile());
		dm.setTag("p");
		
		String html = dm.generate();
		ArrayList<String> htmlLines = new ArrayList<String>();
		htmlLines.add(html);
		
		FileUtils.writeLines(new File("target/diff.html"), htmlLines);
	}
	
	private String readFileByBytes(String path) throws IOException
	{
		byte[] bytes = Files.readAllBytes(Paths.get(Resource.getResource(path).getPath()));
		return new String(bytes);
	}
}

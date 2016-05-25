package org.apodhrad.p2diff;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class DiffForFileTest {
	public void testGenerating() throws Exception
	{
		DiffForFile dff = new DiffForFile(new File(getClass().getClassLoader().getResource("text1.txt").getFile()).getPath(), new File(getClass().getClassLoader().getResource("text2.txt").getFile()).getPath());
		System.out.println(dff.generate());
	}
}

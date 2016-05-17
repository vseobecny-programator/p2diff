package org.apodhrad.p2diff;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Test {
	public static void main(String[] args) throws Exception {
		try {
			DiffManager dm = new DiffManager("lorem.txt", "lorem2.txt");
			dm.setTag("p");
			List<String> html = dm.generate();
			FileUtils.writeLines(new File("target/diff.html"), html);
		} catch (Exception e) {
			throw e;
		}
	}
}
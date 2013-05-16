package com.jini.server;

import java.io.File;
import java.util.List;

import com.jini.FileUtils;

public class Exporter {

	private HTMLParser htmlParser;

	public Exporter() {
		htmlParser = new HTMLParser();
	}

	public void exportStatic(String directory) throws Exception {
		File dir = new File(directory);
		File buildDir = new File(dir, "./../build");
		buildDir.mkdir();
		List<File> listFiles = FileUtils.listFiles(dir, true);
		for (File file : listFiles) {
			if (file.getName().endsWith(".html")) {
				if (!file.getName().startsWith("_")) {
					htmlParser.setResourceFile(file);
					String parse = htmlParser.parse();
					String relativePath = getRelativePath(file, dir);
					FileUtils.writeToFile(new File(buildDir, relativePath),
							parse);
				} else {
					// Skip the partials
				}
			} else {
				String relativePath = getRelativePath(file, dir);
				FileUtils.copyFiles(file, new File(buildDir, relativePath));
			}
		}
	}

	private String getRelativePath(File file, File dir) {
		String dirPath = dir.getAbsolutePath();
		String filePath = file.getAbsolutePath();
		return filePath.substring(dirPath.length() + 1);
	}
}

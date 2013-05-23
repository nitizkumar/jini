package com.jini.server;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.jcoffeescript.JCoffeeScriptCompileException;
import org.jcoffeescript.JCoffeeScriptCompiler;
import org.jcoffeescript.Option;

import com.jini.FileUtils;

public class Exporter {

	private HTMLParser htmlParser;

	private JCoffeeScriptCompiler compiler;

	public Exporter() {
		htmlParser = new HTMLParser();
		Collection<Option> options = new LinkedList<Option>();
		options.add(Option.BARE);
		compiler = new JCoffeeScriptCompiler(options);
	}

	public void exportStatic(String directory) throws Exception {
		File dir = new File(directory);
		File buildDir = new File(dir, "./../build");
		buildDir.mkdir();
		List<File> listFiles = FileUtils.listFiles(dir, true);
		MessageBox.addMessage("Exporting " + listFiles.size() + " Files");
		for (File file : listFiles) {
			MessageBox.addMessage("Exporting " + file.getName());
			if (file.getName().endsWith(".html")) {
				exportHTML(dir, buildDir, file);
			} else if (file.getName().endsWith(".js")) {
				exportJS(dir, buildDir, file);
			} else if (file.getName().endsWith(".coffee")) {
				exportCoffee(dir, buildDir, file);
			} else {
				String relativePath = getRelativePath(file, dir);
				FileUtils.copyFiles(file, new File(buildDir, relativePath));
			}
		}
		MessageBox.addMessage("Export Completed");
	}

	private void exportCoffee(File dir, File buildDir, File file)
			throws Exception {
		String relativePath = getRelativePath(file, dir);
		String replace = relativePath.replace(".coffee", ".js");
		File destFile = new File(buildDir, replace);
		if (destFile.exists()) {
			System.out.println(destFile.getName() + " exists");
			return;
		}
		String readToString = FileUtils.readToString(file);
		String compile = compiler.compile(readToString);
		destFile.getParentFile().mkdirs();
		FileUtils.writeToFile(destFile, compile);
	}

	private void exportJS(File dir, File buildDir, File file) throws Exception {
		String relativePath = getRelativePath(file, dir);
		FileUtils.copyFiles(file, new File(buildDir, relativePath));
	}

	private void exportHTML(File dir, File buildDir, File file)
			throws Exception {
		if (!file.getName().startsWith("_")) {
			htmlParser.setResourceFile(file);
			String parse = htmlParser.parse();
			String relativePath = getRelativePath(file, dir);
			FileUtils.writeToFile(new File(buildDir, relativePath), parse);
		} else {
			// Skip the partials
		}
	}

	private String getRelativePath(File file, File dir) {
		String dirPath = dir.getAbsolutePath();
		String filePath = file.getAbsolutePath();
		return filePath.substring(dirPath.length() + 1);
	}
}

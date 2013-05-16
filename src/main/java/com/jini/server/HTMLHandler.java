package com.jini.server;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import com.jini.FileUtils;

public class HTMLHandler extends ResourceHandler {

	private HTMLParser htmlParser;

	private File tempDir;

	private HashMap<String, Long> fileModTs = new HashMap<String, Long>();
	private HashMap<String, List<String>> fileParts = new HashMap<String, List<String>>();

	public HTMLHandler() {
		htmlParser = new HTMLParser();
	}

	public void setTempDir(File tempDir) {
		this.tempDir = tempDir;
	}

	@Override
	public Resource getResource(String path) throws MalformedURLException {
		Resource resource = super.getResource(path);
		if (path.endsWith(".html")) {
			try {
				long lastModified = resource.getFile().lastModified();
				if (isFileChangedOnDisk(resource.getFile())) {
					htmlParser.setResourceFile(resource.getFile());
					String parse = htmlParser.parse();
					File file = new File(tempDir, path);
					file.createNewFile();
					FileUtils.writeToFile(file, parse);
					fileModTs.put(resource.getFile().getAbsolutePath(),
							lastModified);
					return super.getResource(File.separator + tempDir.getName()
							+ File.separator + path.substring(1));
				} else {

				}
			} catch (Exception e) {
				MessageBox.addMessage(e.getMessage());
				e.printStackTrace();
			}
		}
		return resource;
	}

	private boolean isFileChangedOnDisk(File file) {
		return true;
	}
}

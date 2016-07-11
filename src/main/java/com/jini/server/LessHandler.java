package com.jini.server;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import com.asual.lesscss.LessEngine;
import com.jini.FileUtils;

public class LessHandler extends ResourceHandler {
	private File tempDir;
	private HashMap<String, String> fileCache = new HashMap();
	private File combinedCSSFile = null;
	LessEngine engine = new LessEngine();

	public void setTempDir(File tempDir) {
		this.tempDir = tempDir;
		this.combinedCSSFile = new File(tempDir, "combined.css");
		try {
			this.combinedCSSFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Resource getResource(String path) throws MalformedURLException {
		Resource originalResouce = super.getResource(path);
		String lessFilePath = path.replace(".css", ".less");
		Resource altResource = super.getResource(lessFilePath);
		if (altResource.exists()) {
			try {
				File absoluteFile = super.getResource(lessFilePath).getFile().getAbsoluteFile();

				String md5Checksum = FileUtils.getMD5Checksum(absoluteFile.getAbsolutePath());
				// if (md5Checksum.equals(this.fileCache.get(path))) {
				// return super.getResource("/" + this.tempDir.getName() +
				// path);
				// }

				MessageBox.addMessage("Compiling " + lessFilePath);

				File file = new File(this.tempDir, path);
				file.getParentFile().mkdirs();
				file.createNewFile();

				String readToString = FileUtils.readToString(altResource.getFile());
				engine.compile(altResource.getFile(), file);

				this.fileCache.put(path, md5Checksum);

				// file.delete();

				MessageBox.addMessage("Compiling completed for " + lessFilePath);

				Resource resource2 = super.getResource("/" + this.tempDir.getName() + path);
				return resource2;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if (originalResouce.exists()) {
				return originalResouce;
			}
		}
		return null;
	}
}

package com.jini.server;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.jcoffeescript.JCoffeeScriptCompiler;
import org.jcoffeescript.Option;

import com.jini.FileUtils;

public class JavascriptHandler extends ResourceHandler {

	private File tempDir;

	private HashMap<String, String> fileCache = new HashMap<String, String>();

	public void setTempDir(File tempDir) {
		this.tempDir = tempDir;
	}

	@Override
	public Resource getResource(String path) throws MalformedURLException {
		Resource resource = super.getResource(path);
		if (resource.exists()) {
			return resource;
		}
		String coffeeFilePath = path.replace(".js", ".coffee");
		resource = super.getResource(coffeeFilePath);
		if (resource.exists()) {
			try {
				if (super.getResource("/" + tempDir.getName() + path).exists()) {
					File absoluteFile = super.getResource(coffeeFilePath)
							.getFile().getAbsoluteFile();
					String md5Checksum = FileUtils.getMD5Checksum(absoluteFile
							.getAbsolutePath());
					if (md5Checksum.equals(fileCache.get(path))) {
//						MessageBox.addMessage("Using cached copy of  " + coffeeFilePath);
						return super
								.getResource("/" + tempDir.getName() + path);
					} else {
						MessageBox.addMessage("Compiling " + coffeeFilePath);
						Collection<Option> options = new LinkedList<Option>();
						options.add(Option.BARE);
						String readToString = FileUtils.readToString(resource
								.getFile());
						String compile = new JCoffeeScriptCompiler(options)
								.compile(readToString);
						File file = new File(tempDir, path);
						file.getParentFile().mkdirs();
						file.createNewFile();
						FileUtils.writeToFile(file, compile);
						fileCache.put(path, md5Checksum);
						MessageBox.addMessage("Compiling completed for " + coffeeFilePath);
						return super
								.getResource("/" + tempDir.getName() + path);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}

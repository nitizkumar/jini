package com.jini.server;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.webapp.WebAppContext;

public class CustomResourceHandler extends AbstractHandler {

	private File dir = null;
	private WebAppContext context;
	private HTMLHandler htmlHandler;
	private JavascriptHandler jsHandler;

	public CustomResourceHandler(String directory) {
		dir = new File(directory);
		if (dir.isFile())
			throw new IllegalArgumentException("Invalid Directory");
		new File(dir, ".work").mkdir();
		htmlHandler = new HTMLHandler();
		jsHandler = new JavascriptHandler();
	}

	@Override
	public void handle(String path, Request arg1, HttpServletRequest arg2,
			HttpServletResponse arg3) throws IOException, ServletException {
		if (path.endsWith("favicon.ico"))
			return;
		if (path.contains("/jini/")) {
			return;
		}
		if (path.endsWith(".html")) {
			htmlHandler.setTempDir(new File(dir, ".work"));
			htmlHandler.setResourceBase(dir.getAbsolutePath());
			htmlHandler.handle(path, arg1, arg2, arg3);
			return;
		}
		if (path.endsWith(".js")) {			
			jsHandler.setTempDir(new File(dir, ".work"));
			jsHandler.setResourceBase(dir.getAbsolutePath());
			jsHandler.handle(path, arg1, arg2, arg3);
			return;
		}

		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setResourceBase(dir.getAbsolutePath());
		resourceHandler.handle(path, arg1, arg2, arg3);

		// System.out.println(path);
	}

}

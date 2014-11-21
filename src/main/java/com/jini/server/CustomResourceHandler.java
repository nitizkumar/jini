package com.jini.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.webapp.WebAppContext;

public class CustomResourceHandler
  extends AbstractHandler
{
  public static File dir = null;
  private WebAppContext context;
  private HTMLHandler htmlHandler;
  private JavascriptHandler jsHandler;
  
  public CustomResourceHandler(String directory)
  {
    dir = new File(directory);
    if (dir.isFile()) {
      throw new IllegalArgumentException("Invalid Directory");
    }
    new File(dir, ".work").mkdir();
    this.htmlHandler = new HTMLHandler();
    this.jsHandler = new JavascriptHandler();
  }
  
  public void handle(String path, Request arg1, HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    if (path.endsWith("favicon.ico")) {
      return;
    }
    if (path.contains("/jini/")) {
      return;
    }
    Lorem.getInstance().setBaseDir(dir);
    if (path.endsWith(".html"))
    {
      this.htmlHandler.setTempDir(new File(dir, ".work"));
      this.htmlHandler.setResourceBase(dir.getAbsolutePath());
      this.htmlHandler.handle(path, arg1, request, response);
      return;
    }
    if (path.endsWith(".js"))
    {
      this.jsHandler.setTempDir(new File(dir, ".work"));
      this.jsHandler.setResourceBase(dir.getAbsolutePath());
      this.jsHandler.handle(path, arg1, request, response);
      return;
    }
    String appPath = (String)MainServer.appProp.get("API_PATH");
    String resourcePath = (String)MainServer.appProp.get("API_RESOURCE_PATH");
    if ((appPath != null) && ((path.contains(appPath)) || (path.contains(resourcePath))))
    {
      RemoteResourceHandler remoteResourceHandler = new RemoteResourceHandler();
      remoteResourceHandler.setDir(dir);
      remoteResourceHandler.handle(path, arg1, request, response);
      return;
    }
    ResourceHandler resourceHandler = new ResourceHandler();
    resourceHandler.setDirectoriesListed(true);
    resourceHandler.setResourceBase(dir.getAbsolutePath());
    resourceHandler.handle(path, arg1, request, response);
    
    System.out.println(path);
  }
}

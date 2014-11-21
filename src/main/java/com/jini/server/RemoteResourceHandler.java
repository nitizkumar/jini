package com.jini.server;

import com.jini.FileUtils;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.eclipse.jetty.server.Request;

public class RemoteResourceHandler
{
  private File dir = null;
  
  public void setDir(File dir)
  {
    this.dir = dir;
  }
  
  public void handle(String path, Request arg1, HttpServletRequest request, HttpServletResponse response)
    throws IOException
  {
    String appPath = (String)MainServer.appProp.get("API_PATH");
    String resourcePath = (String)MainServer.appProp.get("API_RESOURCE_PATH");
    
    String remoteServerURL = (String)MainServer.appProp.get("API_REMOTE_SERVER_URL");
    
    String remoteResourceURL = (String)MainServer.appProp.get("API_REMOTE_RESOURCE_URL");
    
    Boolean useCache = Boolean.valueOf((String)MainServer.appProp.get("CACHE_SERVER"));
    if (path.startsWith(resourcePath))
    {
      String originalPath = path;
      
      File workDir = new File(this.dir, ".work");
      File cachedFile = new File(workDir, originalPath.replace("/", "_"));
      if ((cachedFile.exists()) && (useCache.booleanValue()))
      {
        ServletOutputStream os = response.getOutputStream();
        ImageIO.write(ImageIO.read(cachedFile), "png", os);
        return;
      }
      path = path.replace(resourcePath, remoteResourceURL);
      
      BufferedImage image = ImageIO.read(new URL(path));
      try
      {
        ImageIO.write(image, "png", cachedFile);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    if (path.startsWith(appPath))
    {
      String originalPath = path;
      path = path.replace(appPath, remoteServerURL);
      
      File workDir = new File(this.dir, ".work");
      File cachedFile = new File(workDir, originalPath.replace("/", "_") + "_" + arg1.getMethod());
      if ((cachedFile.exists()) && (useCache.booleanValue()))
      {
        ServletOutputStream os = response.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bsw = new BufferedWriter(osw);
        bsw.write(FileUtils.readToString(cachedFile));
        bsw.close();
        response.flushBuffer();
        return;
      }
      BufferedReader reader = arg1.getReader();
      String line = null;
      StringBuffer stringBuffer = new StringBuffer();
      while ((line = reader.readLine()) != null)
      {
        System.out.println(line);
        stringBuffer.append(line);
      }
      HttpClient client = new HttpClient();
      if (arg1.getMethod().toLowerCase().equals("get"))
      {
        HttpMethod method = new GetMethod(path);
        setHeaders(arg1, method);
        














        client.executeMethod(method);
        String responseBodyAsString = method.getResponseBodyAsString();
        System.out.println(responseBodyAsString);
        ServletOutputStream os = response.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bsw = new BufferedWriter(osw);
        bsw.write(responseBodyAsString);
        bsw.close();
        response.flushBuffer();
        try
        {
          FileUtils.writeToFile(cachedFile, responseBodyAsString);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
      if (arg1.getMethod().toLowerCase().equals("post"))
      {
        PostMethod method = new PostMethod(path);
        RequestEntity entity = new StringRequestEntity(stringBuffer.toString());
        
        method.setRequestEntity(entity);
        setHeaders(arg1, method);
        client.executeMethod(method);
        System.out.println(path);
        InputStreamReader in2 = new InputStreamReader(method.getResponseBodyAsStream(), "UTF-8");
        
        StringWriter sw = new StringWriter();
        int x;
        while ((x = in2.read()) != -1) {
          sw.write(x);
        }
        in2.close();
        String responseAsString = sw.toString();
        ServletOutputStream os = response.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bsw = new BufferedWriter(osw);
        bsw.write(responseAsString);
        System.out.println(responseAsString);
        bsw.close();
        response.flushBuffer();
        try
        {
          FileUtils.writeToFile(cachedFile, responseAsString);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
      if (arg1.getMethod().toLowerCase().equals("put"))
      {
        PutMethod method = new PutMethod(path);
        RequestEntity entity = new StringRequestEntity(stringBuffer.toString());
        
        method.setRequestEntity(entity);
        setHeaders(arg1, method);
        client.executeMethod(method);
        String responseBodyAsString = method.getResponseBodyAsString();
        System.out.println(responseBodyAsString);
        ServletOutputStream os = response.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bsw = new BufferedWriter(osw);
        bsw.write(responseBodyAsString);
        bsw.close();
        response.flushBuffer();
        try
        {
          FileUtils.writeToFile(cachedFile, responseBodyAsString);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
      if (arg1.getMethod().toLowerCase().equals("delete"))
      {
        DeleteMethod method = new DeleteMethod(path);
        setHeaders(arg1, method);
        client.executeMethod(method);
        String responseBodyAsString = method.getResponseBodyAsString();
        System.out.println(responseBodyAsString);
        ServletOutputStream os = response.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bsw = new BufferedWriter(osw);
        bsw.write(responseBodyAsString);
        bsw.close();
        response.flushBuffer();
        try
        {
          FileUtils.writeToFile(cachedFile, responseBodyAsString);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
      System.out.println(path);
    }
  }
  
  private void setHeaders(Request arg1, HttpMethod method)
  {
    if (arg1.getHeader("Content-Type") == null) {
      method.addRequestHeader(new Header("Content-Type", "application/json"));
    } else {
      method.addRequestHeader(new Header("Content-Type", arg1.getHeader("Content-Type")));
    }
    if (arg1.getHeader("Cookie") != null) {
      method.addRequestHeader(new Header("Cookie", arg1.getHeader("Cookie")));
    }
    String headerParams = (String)MainServer.appProp.get("HEADER_PARAMS");
    if (headerParams != null)
    {
      String[] split = headerParams.split(",");
      if (split.length > 0) {
        for (int i = 0; i < split.length; i++) {
          method.addRequestHeader(new Header(split[i], arg1.getHeader(split[i])));
        }
      }
    }
  }
}

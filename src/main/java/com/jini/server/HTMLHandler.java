package com.jini.server;

import com.jini.FileUtils;
import java.io.File;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

public class HTMLHandler
  extends ResourceHandler
{
  private HTMLParser htmlParser;
  private File tempDir;
  private HashMap<String, Long> fileModTs = new HashMap();
  private HashMap<String, List<String>> fileParts = new HashMap();
  
  public HTMLHandler()
  {
    this.htmlParser = new HTMLParser();
  }
  
  public void setTempDir(File tempDir)
  {
    this.tempDir = tempDir;
  }
  
  public Resource getResource(String path)
    throws MalformedURLException
  {
    System.out.println("HTMLHandler.getResource()");
    Resource resource = super.getResource(path);
    if (path.endsWith(".html")) {
      try
      {
        long lastModified = resource.getFile().lastModified();
        if (isFileChangedOnDisk(resource.getFile()))
        {
          this.htmlParser.setResourceFile(resource.getFile());
          String parse = this.htmlParser.parse();
          File file = new File(this.tempDir, path);
          file.getParentFile().mkdirs();
          file.createNewFile();
          FileUtils.writeToFile(file, parse);
          this.fileModTs.put(resource.getFile().getAbsolutePath(), Long.valueOf(lastModified));
          if (hasLayout())
          {
            File[] layoutFiles = getLayoutFiles();
            LayoutParser lp = new LayoutParser();
            lp.setResourceFile(file);
            lp.setLayoutFile(getApplicableLayout(parse, layoutFiles));
            String parse2 = lp.parse();
            file = new File(this.tempDir, path);
            file.createNewFile();
            FileUtils.writeToFile(file, parse2);
          }
          return super.getResource("/" + this.tempDir.getName() + path);
        }
      }
      catch (Exception e)
      {
        MessageBox.addMessage(e.getMessage());
        e.printStackTrace();
      }
    }
    return resource;
  }
  
  public File getApplicableLayout(String content, File[] layouts)
  {
    Pattern compile = Pattern.compile("<!--(\\s)Layout\\s(.[^-]*)\\s-->", 10);
    
    Matcher matcher = compile.matcher(content);
    if (matcher.find())
    {
      String group = matcher.group(2);
      for (File file : layouts) {
        if (file.getName().startsWith(group)) {
          return file;
        }
      }
    }
    return layouts[0];
  }
  
  private boolean hasLayout()
  {
    File file = CustomResourceHandler.dir;
    File layoutFolder = new File(file, "layout");
    if (layoutFolder.exists())
    {
      File[] listFiles = layoutFolder.listFiles();
      if (listFiles.length == 0) {
        return false;
      }
      return true;
    }
    return false;
  }
  
  private File[] getLayoutFiles()
  {
    File file = CustomResourceHandler.dir;
    File layoutFolder = new File(file, "layout");
    if (layoutFolder.exists())
    {
      File[] listFiles = layoutFolder.listFiles();
      if (listFiles.length == 0) {
        return new File[0];
      }
      return listFiles;
    }
    return new File[0];
  }
  
  private boolean isFileChangedOnDisk(File file)
  {
    return true;
  }
}

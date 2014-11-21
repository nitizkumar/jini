package com.jini.server;

import com.jini.FileUtils;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.jcoffeescript.JCoffeeScriptCompiler;
import org.jcoffeescript.Option;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

public class JavascriptHandler
  extends ResourceHandler
{
  private File tempDir;
  private HashMap<String, String> fileCache = new HashMap();
  private File combinedJSFile = null;
  
  public void setTempDir(File tempDir)
  {
    this.tempDir = tempDir;
    this.combinedJSFile = new File(tempDir, "combined.js");
  }
  
  public Resource getResource(String path)
    throws MalformedURLException
  {
    Resource resource = super.getResource(path);
    if (resource.exists()) {
      return resource;
    }
    String coffeeFilePath = path.replace(".js", ".coffee");
    resource = super.getResource(coffeeFilePath);
    if (resource.exists()) {
      try
      {
        if (!super.getResource("/" + this.tempDir.getName() + path).exists())
        {
          File absoluteFile = super.getResource(coffeeFilePath).getFile().getAbsoluteFile();
          
          String md5Checksum = FileUtils.getMD5Checksum(absoluteFile.getAbsolutePath());
          if (md5Checksum.equals(this.fileCache.get(path))) {
            return super.getResource("/" + this.tempDir.getName() + path);
          }
          MessageBox.addMessage("Compiling " + coffeeFilePath);
          Collection<Option> options = new LinkedList();
          options.add(Option.BARE);
          String readToString = FileUtils.readToString(resource.getFile());
          
          String compile = new JCoffeeScriptCompiler(options).compile(readToString);
          
          File file = new File(this.tempDir, path);
          file.getParentFile().mkdirs();
          file.createNewFile();
          
          Reader in = new StringReader(compile);
          
          JavaScriptCompressor jsComp = new JavaScriptCompressor(in, new ErrorReporter()
          {
            public void warning(String arg0, String arg1, int arg2, String arg3, int arg4) {}
            
            public EvaluatorException runtimeError(String arg0, String arg1, int arg2, String arg3, int arg4)
            {
              return null;
            }
            
            public void error(String arg0, String arg1, int arg2, String arg3, int arg4) {}
          });
          FileUtils.writeToFile(file, compile);
          this.fileCache.put(path, md5Checksum);
          MessageBox.addMessage("Compiling completed for " + coffeeFilePath);
          
          Resource resource2 = super.getResource("/" + this.tempDir.getName() + path);
          try
          {
            FileUtils.appendToFile(this.combinedJSFile, resource2.getInputStream());
          }
          catch (Exception e)
          {
            e.printStackTrace();
          }
          return resource2;
        }
        Resource resource2 = super.getResource("/" + this.tempDir.getName() + path);
        try
        {
          FileUtils.appendToFile(this.combinedJSFile, resource2.getInputStream());
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
        return resource2;
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    return null;
  }
}

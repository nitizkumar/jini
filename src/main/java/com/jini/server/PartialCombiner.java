package com.jini.server;

import com.jini.FileUtils;
import java.io.File;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PartialCombiner
{
  private File sourceFile;
  private File originalFile;
  
  public PartialCombiner() {}
  
  public PartialCombiner(File sourceFile, File originalFile)
  {
    this.sourceFile = sourceFile;
    this.originalFile = originalFile;
  }
  
  public String resolvePartials()
    throws Exception
  {
    String readToString = FileUtils.readToString(this.sourceFile);
    Pattern pattern = Pattern.compile("(<%=\\s(partial)\\s\"([^%]+)?%>)", 10);
    
    Matcher matcher = pattern.matcher(readToString);
    String replacement = readToString;
    while (matcher.find())
    {
      String group = matcher.group(3);
      String path = "";
      if (group.contains("/"))
      {
        String[] split = group.split("/");
        String lastFragment = split[(split.length - 1)];
        for (int i = 0; i < split.length - 1; i++) {
          path = path + split[i] + "/";
        }
        String trim = lastFragment.trim();
        path = path + "_" + trim.substring(0, trim.length() - 1) + ".html";
      }
      else
      {
        String trim = group.trim();
        path = path + "_" + trim.substring(0, trim.length() - 1) + ".html";
      }
      File partialFile = new File(this.sourceFile.getParentFile(), path);
      System.out.println(path);
      if (path.contains("input")) {
        System.out.println(path);
      }
      if (!partialFile.exists())
      {
        System.out.println("Relative path " + partialFile.getAbsolutePath() + " not found ");
        
        partialFile = new File(this.originalFile.getParentFile(), path);
        System.out.println("Original Path " + partialFile.getAbsolutePath() + "");
        if (!partialFile.exists()) {
          System.out.println("Path=" + partialFile.getAbsolutePath());
        }
      }
      PartialCombiner child = new PartialCombiner(partialFile, this.originalFile);
      
      String resolvePartials = child.resolvePartials();
      readToString = readToString.replace(matcher.group(0), resolvePartials);
    }
    return readToString;
  }
  
  public File getSourceFile()
  {
    return this.sourceFile;
  }
  
  public void setSourceFile(File sourceFile)
  {
    this.sourceFile = sourceFile;
    if (this.originalFile == null) {
      this.originalFile = sourceFile;
    }
  }
}

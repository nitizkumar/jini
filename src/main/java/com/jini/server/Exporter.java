package com.jini.server;

import com.jini.FileUtils;
import java.io.File;
import java.util.List;

public class Exporter
{
  private HTMLParser htmlParser;
  
  public Exporter()
  {
    this.htmlParser = new HTMLParser();
  }
  
  public void exportStatic(String directory)
    throws Exception
  {
    File dir = new File(directory);
    File buildDir = new File(dir, "./../build");
    buildDir.mkdir();
    List<File> listFiles = FileUtils.listFiles(dir, true);
    for (File file : listFiles) {
      if (file.getName().endsWith(".html"))
      {
        if ((!file.getParentFile().isDirectory()) || (!file.getParentFile().getAbsolutePath().equals(directory + "\\layout"))) {
          if (!file.getName().startsWith("_"))
          {
            this.htmlParser.setResourceFile(file);
            String relativePath = getRelativePath(file, dir);            
            File srcFile = new File(dir, ".work/" + relativePath);
            // Src File does not exist in work folder
            if(!srcFile.exists()){
            	FileUtils.copyFiles(file, new File(buildDir, relativePath));
            }else
            	FileUtils.copyFiles(srcFile, new File(buildDir, relativePath));
          }
        }
      }
      else
      {
        String relativePath = getRelativePath(file, dir);
        FileUtils.copyFiles(file, new File(buildDir, relativePath));
      }
    }
  }
  
  private String getRelativePath(File file, File dir)
  {
    String dirPath = dir.getAbsolutePath();
    String filePath = file.getAbsolutePath();
    return filePath.substring(dirPath.length() + 1);
  }
}

package com.jini.server;

import com.jini.FileUtils;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LayoutParser
{
  private File resourceFile;
  private File layoutFile;
  
  public void setLayoutFile(File layoutFile)
  {
    this.layoutFile = layoutFile;
  }
  
  public void setResourceFile(File resourceFile)
  {
    this.resourceFile = resourceFile;
  }
  
  public String parse()
    throws Exception
  {
    HTMLParser parser = new HTMLParser();
    parser.setResourceFile(this.layoutFile);
    String readToString = parser.parse();
    Pattern pattern = Pattern.compile("(&lt;%=\\s(nested)\\s?%&gt;)", 10);
    
    Matcher matcher = pattern.matcher(readToString);
    while (matcher.find())
    {
      String group = matcher.group(1);
      readToString = matcher.replaceFirst(FileUtils.readToString(this.resourceFile));
    }
    return readToString;
  }
}

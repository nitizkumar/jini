package com.jini.js;

import com.jini.FileUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

public class JSCombiner
{
  private static String dir;
  private static List<String> includedFiles = new ArrayList();
  
  public static void main(String[] args)
    throws Exception
  {
    dir = "C:\\projects\\insight\\HTML-2.0";
    File propFile = new File(dir, "js.json");
    BufferedReader reader = new BufferedReader(new FileReader(propFile));
    String line = null;
    StringBuffer buffer = new StringBuffer();
    while ((line = reader.readLine()) != null)
    {
      buffer.append(line);
      buffer.append("\n");
    }
    JSONObject obj = new JSONObject(buffer.toString());
    processJSON(obj);
    


    processHTML(obj);
  }
  
  private static void processHTML(JSONObject obj)
    throws Exception
  {
    List<String> readLines = FileUtils.readLines(new File(dir, "index.html.backup"));
    
    Pattern compile = Pattern.compile("(<script type=\"text/javascript\" src=\"([^>]+)\">)");
    
    ArrayList<String> arrayList = new ArrayList();
    for (String string : readLines)
    {
      Matcher matcher = compile.matcher(string);
      if (matcher.find())
      {
        String group = matcher.group(2);
        if (!includedFiles.contains(group)) {
          arrayList.add(string);
        }
      }
      else
      {
        arrayList.add(string);
      }
    }
    FileUtils.writeLines(new File(dir, "index.html"), arrayList);
  }
  
  protected static void processJSON(JSONObject obj)
    throws Exception
  {
    JSONArray jsonArray = obj.getJSONArray("files");
    for (int i = 0; i < jsonArray.length(); i++)
    {
      JSONObject elem = (JSONObject)jsonArray.get(i);
      JSONArray files = elem.getJSONArray("files");
      File jsFile = new File(dir, "js/min/" + elem.getString("name"));
      jsFile.createNewFile();
      StringBuffer stringBuffer = new StringBuffer();
      for (int j = 0; j < files.length(); j++)
      {
        String file = (String)files.get(j);
        File file2 = new File(dir, file);
        String readToString = FileUtils.readToString(file2);
        if (file.startsWith("/")) {
          includedFiles.add(file.substring(1));
        } else {
          includedFiles.add(file);
        }
        stringBuffer.append("\n");
        stringBuffer.append("/**--" + file + "--**/");
        stringBuffer.append("\n");
        stringBuffer.append(readToString);
      }
      FileUtils.writeToFile(jsFile, stringBuffer.toString());
    }
  }
}

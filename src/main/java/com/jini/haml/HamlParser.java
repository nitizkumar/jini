package com.jini.haml;

import com.cadrlife.jhaml.JHaml;
import com.jini.FileUtils;
import java.io.File;
import java.io.PrintStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public class HamlParser
{
  private File resourceFile;
  
  public void parse()
    throws Exception
  {
    String readToString = FileUtils.readToString(this.resourceFile);
    String parse = new JHaml().parse(readToString);
    System.out.println(parse);
  }
  
  public static void main(String[] args)
    throws Exception
  {
    HamlParser parser = new HamlParser();
    parser.resourceFile = new File("/home/nitish/mysite/source/index.html.haml");
    parser.parse();
  }
  
  private static void doSomething()
  {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    try
    {
      DocumentBuilder db = dbf.newDocumentBuilder();
      
      Document dom = db.newDocument();
      DOMImplementation domImpl = dom.getImplementation();
      Element html = dom.createElement("html");
      Element body = dom.createElement("body");
      html.appendChild(body);
      dom.appendChild(html);
      System.out.println(((DOMImplementationLS)domImpl).createLSSerializer().writeToString(dom));
    }
    catch (Exception te)
    {
      System.out.println(te.getMessage());
    }
  }
}

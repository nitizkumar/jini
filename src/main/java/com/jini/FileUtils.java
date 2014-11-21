package com.jini;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class FileUtils
{
  public static String readToString(File file)
    throws FileNotFoundException, IOException
  {
    BufferedReader bufferedReader = null;
    try
    {
      bufferedReader = new BufferedReader(new FileReader(file));
      String line = null;
      StringBuffer buffer = new StringBuffer();
      while ((line = bufferedReader.readLine()) != null)
      {
        buffer.append(line);
        buffer.append("\n");
      }
      return buffer.toString();
    }
    finally
    {
      if (bufferedReader != null) {
        bufferedReader.close();
      }
    }
  }
  
  public static List<String> readLines(File file)
    throws FileNotFoundException, IOException
  {
    BufferedReader bufferedReader = null;
    try
    {
      bufferedReader = new BufferedReader(new FileReader(file));
      String line = null;
      List<String> list = new ArrayList();
      while ((line = bufferedReader.readLine()) != null) {
        list.add(line);
      }
      return list;
    }
    finally
    {
      if (bufferedReader != null) {
        bufferedReader.close();
      }
    }
  }
  
  public static void writeLines(File file, List<String> content)
    throws Exception
  {
    BufferedWriter writer = null;
    try
    {
      writer = new BufferedWriter(new FileWriter(file));
      for (String string : content)
      {
        writer.write(string);
        writer.write("\n");
      }
    }
    finally
    {
      if (writer != null)
      {
        writer.flush();
        writer.close();
      }
    }
  }
  
  public static void writeToFile(File file, String content)
    throws Exception
  {
    BufferedWriter writer = null;
    try
    {
      writer = new BufferedWriter(new FileWriter(file));
      writer.write(content);
    }
    finally
    {
      if (writer != null)
      {
        writer.flush();
        writer.close();
      }
    }
  }
  
  public static void writeToFile(File file, InputStream content)
    throws Exception
  {
    FileOutputStream writer = null;
    try
    {
      writer = new FileOutputStream(file);
      byte[] buffer = new byte[1024];
      int len = 0;
      while ((len = content.read(buffer)) != -1) {
        writer.write(buffer, 0, len);
      }
    }
    finally
    {
      if (writer != null)
      {
        writer.flush();
        writer.close();
      }
    }
  }
  
  public static void appendToFile(File file, InputStream content)
    throws Exception
  {
    FileOutputStream writer = null;
    String existingContent = readToString(file);
    try
    {
      writer = new FileOutputStream(file);
      byte[] buffer = new byte[1024];
      int len = 0;
      
      byte[] bytes = existingContent.getBytes();
      writer.write(bytes, 0, bytes.length);
      while ((len = content.read(buffer)) != -1) {
        writer.write(buffer, 0, len);
      }
    }
    finally
    {
      if (writer != null)
      {
        writer.flush();
        writer.close();
      }
    }
  }
  
  public static List<File> listFiles(File directory, boolean recursive)
  {
    if (directory.isFile()) {
      throw new IllegalArgumentException("Invalid Directory");
    }
    List<File> allFiles = new ArrayList();
    File[] listFiles = directory.listFiles();
    for (File file : listFiles)
    {
      if (file.isFile()) {
        allFiles.add(file);
      }
      if ((file.isDirectory()) && (recursive) && 
        (!file.getName().startsWith("."))) {
        allFiles.addAll(listFiles(file, recursive));
      }
    }
    return allFiles;
  }
  
  public static void copyFiles(File srcFile, File destFile)
    throws Exception
  {
    destFile.getParentFile().mkdirs();
    InputStream fileReader = new FileInputStream(srcFile);
    byte[] buffer = new byte[1024];
    int len = 0;
    FileOutputStream os = new FileOutputStream(destFile);
    try
    {
      while ((len = fileReader.read(buffer)) != -1) {
        os.write(buffer, 0, len);
      }
    }
    finally
    {
      os.flush();
      os.close();
    }
  }
  
  public static void main(String[] args)
    throws Exception
  {
    System.out.println(getMD5Checksum("/home/nitish/projects/solver/HTML-2.0/js/data.coffee"));
    
    System.out.println(getMD5Checksum("/home/nitish/projects/solver/HTML-2.0/js/data.coffee"));
  }
  
  public static byte[] createChecksum(String filename)
    throws Exception
  {
    InputStream fis = new FileInputStream(filename);
    
    byte[] buffer = new byte[1024];
    MessageDigest complete = MessageDigest.getInstance("MD5");
    int numRead;
    do
    {
      numRead = fis.read(buffer);
      if (numRead > 0) {
        complete.update(buffer, 0, numRead);
      }
    } while (numRead != -1);
    fis.close();
    return complete.digest();
  }
  
  public static String getMD5Checksum(String filename)
    throws Exception
  {
    byte[] b = createChecksum(filename);
    String result = "";
    for (int i = 0; i < b.length; i++) {
      result = result + Integer.toString((b[i] & 0xFF) + 256, 16).substring(1);
    }
    return result;
  }
}

package com.jini.server;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

public class Lorem
{
  String phrase = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
  private static Lorem instance = new Lorem();
  private String[] words = null;
  private File baseDir = null;
  private String[] names = { "Rebecca Lopez", "Wanda Patterson", "Amanda Nelson", "Louise Flores", "Betty Anderson", "Janet Collins", "Janice Torres", "Rebecca Gonzalez", "Karen Anderson", "Dorothy Jones", "Susan Brown", "Joan Ramirez", "Margaret Smith", "Ashley Rivera", "Annie Gonzales", "Paula Griffin", "Janet Mitchell", "Laura Allen", "Kathryn Flores", "Julia Henderson", "Janice Ramirez", "Kathleen Carter", "Wanda Powell", "Ashley Richardson", "Amy Carter", "Tina Griffin", "Melissa Lee", "Nancy White", "Heather Bailey", "Nicole Peterson", "Ruby Jenkins", "Jessica King", "Amanda Adams", "Linda Johnson", "Jacqueline Hughes", "Janice Ramirez", "Wanda Hughes", "Laura Hernandez", "Norma Butler", "Ashley Torres", "Donna Thomas", "Sharon Martinez", "Sharon Anderson", "Laura Lewis", "Jacqueline Jenkins", "Michelle Hernandez", "Helen Thomas", "Louise Henderson", "Linda Taylor", "Shirley Hernandez", "Philip Griffin", "William Moore", "Jerry Gonzalez", "Richard Brown", "Larry Lee", "Dennis Nelson", "Gregory Hill", "Johnny Simmons", "Matthew Lee", "Craig Perry", "Jeffrey Clark", "Jose Lewis", "Frank Nelson", "Steven Thompson", "Phillip Perry", "Carlos Jenkins", "Timothy Allen", "Jason Lewis", "Brian Garcia", "Sean Hayes", "Timothy Hall", "Craig Flores", "Douglas Turner", "Phillip Hughes", "Joshua Scott", "Paul Thomas", "Sean Diaz", "Albert Sanchez", "Arthur Evans", "Carlos Flores", "Walter Turner", "Martin Coleman", "Earl Butler", "Mark Jackson", "Gregory Hill", "Brandon Cox", "Benjamin Ramirez", "Robert Davis", "Daniel Jackson", "Sean Foster", "Dennis Lopez", "Douglas Phillips", "Jose Hernandez", "Lawrence Ward", "Chris Butler", "Philip Butler", "Donald Anderson", "Jerry Nelson", "Daniel Thompson", "Juan Bell" };
  
  private Lorem()
  {
    this.words = this.phrase.split(" ");
  }
  
  public static Lorem getInstance()
  {
    return instance;
  }
  
  public void setBaseDir(File baseDir)
  {
    this.baseDir = baseDir;
  }
  
  public File getBaseDir()
  {
    return this.baseDir;
  }
  
  public String word(int len)
  {
    String text = "";
    for (int i = 0; i < len; i++) {
      text = text + this.words[i] + " ";
    }
    return text;
  }
  
  public String full()
  {
    return this.phrase;
  }
  
  public String name()
  {
    Random r = new Random();
    int index = r.nextInt(this.names.length);
    return this.names[index];
  }
  
  public String firstName()
  {
    return name().split(" ")[0];
  }
  
  public String lastName()
  {
    return name().split(" ")[1];
  }
  
  public String getImageURL(int width, int height)
  {
    String path = "/img/ph_" + width + "x" + height + ".jpg";
    File phImage = new File(this.baseDir, path);
    if (phImage.exists()) {
      return path;
    }
    try
    {
      int[] iArray = { 0, 0, 0, 255 };
      
      BufferedImage img = new BufferedImage(width, height, 1);
      

      WritableRaster raster = img.getRaster();
      Random r = new Random();
      for (int i = 0; i < width; i++) {
        for (int j = 0; j < height; j++)
        {
          int v = i * j;
          iArray[0] = (v << 1);
          iArray[1] = (v << 2);
          iArray[2] = (v << 3);
          raster.setPixel(i, j, iArray);
        }
      }
      phImage.getParentFile().mkdirs();
      ImageIO.write(img, "JPG", phImage);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    return path;
  }
}

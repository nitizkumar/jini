package com.jini.server;

import java.io.PrintStream;
import javax.swing.JTextArea;

public class MessageBox
{
  private static JTextArea jTextArea = null;
  
  public static void setTextArea(JTextArea jTextArea)
  {
    MessageBox.jTextArea = jTextArea;
  }
  
  public static synchronized void addMessage(String message)
  {
    jTextArea.setText(jTextArea.getText() + "\n" + message);
    System.out.println(message);
  }
}

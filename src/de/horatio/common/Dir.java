/*
 * Created on 01.02.2006
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.horatio.common;
import java.io.File;
import java.io.FilenameFilter;

/**
 * @author Dümchen
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Dir implements FilenameFilter {

  private String match = ".txt";

  public boolean accept(File f, String s) {
    return s.toLowerCase().endsWith(match);
  }
  public void setMatch(String match) {
    this.match = match;
  }
  /**
   * 
   */
  public Dir() {
    super();
  }
  public static void main(String[] args) {
    System.out.println(HoraFile.getFilesFromDir("d:\\kfz\\logs", ".log"));
  }
}

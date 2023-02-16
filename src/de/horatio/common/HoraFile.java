/*
 * Created on 30.03.2005
 *
 * Datei: HoraFile.java
 * xxx
 * Letzte Änderung von $Author: schleese $
 * $Date: 2005/11/24 11:02:40 $
 * 
 * $Revision: 1.7 $
 * Copyright: Horatio GmbH, Berlin 2005 
 */
package de.horatio.common;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

/**
 * TODO Doku fehlt noch
 * 
 * @author A. Lüth (aus uniProcs)
 */
public class HoraFile {

  /**
   * Diese Funktion schreibt den Inhalt einer ArrayList in eine Datei.
   * 
   * @param aL ArrayList deren Inhalt in eine Datei geschrieben werden soll
   * @param Dateiname der Datei, die den ArrayList-Inhalt aufnehmen soll.
   * @return boolean
   * @since
   */
  public static boolean ArrayListToFile(ArrayList aL, String Dateiname) {
    return ArrayListToFile(aL, Dateiname, System.out);
  }
  /**
   * Diese Funktion schreibt den Inhalt einer ArrayList in eine Datei.
   * 
   * @param aL ArrayList deren Inhalt in eine Datei geschrieben werden soll
   * @param Dateiname Name der Datei, die den ArrayList-Inhalt aufnehmen soll.
   * @param out AusgabeStream für Fehler
   * @return boolean
   * @since
   */
  public static boolean ArrayListToFile(ArrayList aL, String Dateiname, PrintStream out) {
    boolean b = true;
    try {
      File f = new File(Dateiname);
      b = f.exists();
      if (!b) {
        String sH = f.getAbsolutePath();
        int i = sH.lastIndexOf(getFileSeparator());
        sH = sH.substring(0, i);
        File dir = new File(sH);
        dir.mkdirs();
        dir = null;
        b = f.createNewFile();
      }
      if (b) {
        BufferedWriter bw = new BufferedWriter(new FileWriter(f.getAbsolutePath(), false));
        // kein append !!
        try {
          for (int i = 0; i < aL.size(); i++) {
            //						String sH = (String) aL.get(i);
            bw.write((aL.get(i)).toString());
            bw.newLine();
          }
          bw.flush();
          return true;
        } finally {
          bw.close();
        }
      }
    } catch (Exception ex) {
      //			Monitor.printErr("Exception ArrayListToFile (Datei = " + Dateiname);
      //			Monitor.printErr(ex.toString());
      out.println("Exception ArrayListToFile (Datei = " + Dateiname);
      out.println(ex.toString());
    }
    return false;
  }
  /**
   * Alle Dateien werden sortiert in DateienListe von ältester Datei Index 0 zu neuster Datei aufwärts.
   * @param files 
   * @return
   */
  public static File[] sortFileListeVonAltbisNeu(File[] files) {
    //Arndt 04.03.2014 aus dem Web
    Arrays.sort(files, new Comparator<Object>() {

      public int compare(Object o1, Object o2) {
        File f0 = (File) o1, f1 = (File) o2;
        long last0 = f0.lastModified(), last1 = f1.lastModified();
        if (last0 > last1) {
          return 1;
        } else
          if (last0 < last1) {
            return -1;
          } else {
            return 0;
          }
      }
    });
    return files;
  }
  /**
   * Diese Funktion löscht eine Datei.
   * 
   * @param Dateiname String
   * @return boolean
   * @since
   */
  public static boolean deleteFile(String Dateiname) {
    File Datei = new File(Dateiname);
    boolean result = !Datei.exists();
    if (result) {
      // Datei ist gar nicht da, OK.
    } else {
      // Datei löschen
      result = Datei.delete();
    }
    return result;
  }
  /**
   * alles hinter dem letzten Backslash
   * 
   * @param sDatei
   * @return String
   * @since
   */
  public static String extractFileName(String sDatei) {
    int i = sDatei.lastIndexOf(getFileSeparator());
    return sDatei.substring(i + 1);
  }
  /**
   * directory der datei
   * @param sDatei
   * @return
   */
  public static String extractFileDirectory(String sDatei) {
    int i = sDatei.lastIndexOf(HoraFile.getFileSeparator());
    return sDatei.substring(0, i);
  }
  /**
   * Diese Funktion liefert das Alter einer Datei.
   * 
   * @param sDatei Datei, deren Alter bestimmt werden soll.
   * @return long
   * @since
   */
  public static long fileAge(String sDatei) {
    File f = new File(sDatei);
    return f.lastModified();
  }
  /**
   * Diese Funktion fügt einer Datei eine Zeile hinnzu.
   * 
   * @param Dateiname String, Datei der Text hinzugefügt werden soll.
   * @param sZeile String, der angehängt werden soll
   * @return boolean
   * @since
   */
  public static boolean fileAppend(String Dateiname, String sZeile) {
    boolean b = true;
    try {
      File Datei = new File(Dateiname);
      b = Datei.exists();
      if (!b) {
        makeDirForFile(Dateiname);
        b = Datei.createNewFile();
      }
      if (b) {
        BufferedWriter bw = new BufferedWriter(new FileWriter(Datei.getAbsolutePath(), true));
        // append !!
        try {
          bw.write(sZeile);
          bw.newLine();
          return true;
        } finally {
          bw.close();
        }
      }
    } catch (Exception ex) {
      System.err.println("fileAppend: " + ex.toString());
    }
    return false;
  }
  /**
   * Diese Funktion prüft die Existenz einer Datei.
   * Rückgabe mit true oder false.
   * 
   * Datei in die DB verarbeiten. zuerst .work verarbeiten dann dat zu
   * dat.work umbenennen und verarbeiten. dann .error neu versuchen zu
   * verarbeiten
   * 
   * @param sDatei Datei, die gesucht werden soll
   * @return boolean
   * @since
   */
  public static boolean fileExists(String sDatei) {
    File f = new File(sDatei);
    return f.exists();
  }
  /**
   * Diese Funktion gibt die Größe einer Datei zurück.
   * 
   * @param sDatei Datei, deren Größe bestimmt werden soll
   * @return int
   * @since
   */
  public static int fileSize(String sDatei) {
    File f = new File(sDatei);
    return (int) f.length();
  }
  /**
   * Diese Funktion fügt den Inhalt einer Datei einer ArrayList hinzu.
   * 
   * @param sDatei Datei, deren Inhalt in eine ArrayList gefügt werden soll
   * @param aL ArrayList, die mit Dateiinhalt gefüllt werden soll
   * @return boolean
   * @since
   */
  public static boolean FillDateiToArrayList(String sDatei, ArrayList<String> aL) {
    return FillDateiToArrayList(sDatei, aL, System.out);
  }
  /**
   * Diese Funktion fügt den Inhalt einer Datei einer ArrayList hinzu.
   * 
   * @param sDatei Datei, deren Inhalt in eine ArrayList gefügt werden soll
   * @param aL ArrayList, die mit Dateiinhalt gefüllt werden soll
   * @param unicode String unicode für den Schriftcode zum Bsp. UTF-8
   * @return boolean
   * @since
   */
  public static boolean FillDateiToArrayList(String sDatei, ArrayList<String> aL, String unicode) {
    return FillDateiToArrayList(sDatei, aL, unicode, System.out);
  }
  /**
   * Diese Funktion fügt den Inhalt einer Datei einer ArrayList hinzu.
   * 
   * @param sDatei Datei, deren Inhalt in eine ArrayList gefügt werden soll
   * @param aL ArrayList, die mit Dateiinhalt gefüllt werden soll
   * @param out PrintStream out für Ausschriften
   * @return boolean
   * @since
   */
  public static boolean FillDateiToArrayList(String sDatei, ArrayList<String> aL, PrintStream out) {
    boolean result = false;
    String line;
    File file = new File(sDatei);
    if (file.exists()) {
      try {
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
          while ((line = br.readLine()) != null) {
            aL.add(line);
          }
          // alles korrekt eingelesen
          result = true;
        } finally {
          br.close();
        }
      } catch (IOException e) {
        //				System.err.println("FillDateiToArrayList: " + e.toString());
        out.println("FillDateiToArrayList: " + e.toString());
      }
    }
    return result;
  }
  /**
   * Diese Funktion fügt den Inhalt einer Datei einer ArrayList hinzu.
   * 
   * @param sDatei Datei, deren Inhalt in eine ArrayList gefügt werden soll
   * @param aL ArrayList, die mit Dateiinhalt gefüllt werden soll
   * @param unicode String unicode für den Schriftcode zum Bsp. UTF-8
   * @param out PrintStream out für Ausschriften
   * @return boolean
   * @since
   */
  public static boolean FillDateiToArrayList(String sDatei, ArrayList<String> aL, String unicode, PrintStream out) {
    boolean result = false;
    String line;
    File file = new File(sDatei);
    if (file.exists()) {
      try {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sDatei), unicode)); //Arndt 05.03.2014 kann jetzt Umlaute.
        try {
          while ((line = br.readLine()) != null) {
            aL.add(line);
          }
          // alles korrekt eingelesen
          result = true;
        } finally {
          br.close();
        }
      } catch (Exception e) {
        System.err.println("FillDateiToArrayList1: " + e.toString());
      }
    }
    return result;
  }
  /**
   * 
   * 
   * @param sDatei String
   * @param aL ArrayList
   * @return result boolean
   * @since
   */
  public static boolean fillDateiToArrayList(String sDatei, ArrayList aL) {
    boolean result = false;
    String line;
    File file = new File(sDatei);
    if (file.exists()) {
      try {
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
          while ((line = br.readLine()) != null) {
            aL.add(line);
          }
          // alles korrekt eingelesen
          result = true;
        } finally {
          br.close();
        }
      } catch (IOException e) {
        System.err.println("fillDateiToArrayList2: " + e.toString());
      }
    }
    return result;
  }
  /**
   * 
   * 
   * @param sName
   * @return String
   * @since
   */
  public static String getFilePath(String sName) {
    return getFilePath(sName, System.out);
  }
  /**
   * 
   * 
   * @param sName
   * @param out
   * @return String
   * @since
   */
  public static String getFilePath(String sName, PrintStream out) {
    File f = new File(sName);
    String dateiPfad = f.getAbsolutePath();
    //		Monitor.printLn(dateiPfad);
    out.println(dateiPfad);
    return dateiPfad;
  }
  /**
   * 
   * @param sName
   * @return
   */
  public static String getCanonicalPath(String sName) {
    File f = new File(sName);
    String dateiPfad = sName;
    try {
      dateiPfad = f.getCanonicalPath();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return dateiPfad;
  }
  /**
   * 
   * 
   * @return String
   * @since
   */
  public static String getFileSeparator() {
    return System.getProperty("file.separator");
  }
  /**
   * von der Selbstheilung rübergeschaufelt
   * @param vonDatei
   * @param nachDatei
   * @since
   */
  public static void KopiereDatei(String vonDatei, String nachDatei) {
    try {
      File von = new File(vonDatei);
      File nach = new File(nachDatei);
      nach.createNewFile();
      DataInputStream vonIn = new DataInputStream(new FileInputStream(von));
      FileOutputStream nachFout = new FileOutputStream(nach);
      byte[] data = new byte[(int) von.length()];
      vonIn.readFully(data);
      vonIn.close();
      nachFout.write(data);
      nachFout.close();
    } catch (Exception e) {
      System.err.println("KopiereDatei: " + e.toString());
      //System.out.println("Exception beim Kopieren der Datei: " +
      // vonDatei);
      //System.out.println(Ex);
      /*
       ClnThrd.println(sContrNr, 9, "SelHe(" + SHNr + "): Exception beim Kopieren der Datei: "
       + vonDatei);
       ClnThrd.println(sContrNr, 9, "SelHe(" + SHNr + "): " + Ex);
       Data.prot.addProtokoll(9, Integer.parseInt(sContrNr), "004-13", "SelHe(" + SHNr
       + "): Exception beim Kopieren der Datei: " + vonDatei);
       Data.prot.addProtokoll(9, Integer.parseInt(sContrNr), "004-13", "SelHe(" + SHNr + "): "
       + Ex);
       */
    }
  }
  /**
   * Diese Funktion
   * 
   * @return result
   * @since
   * @see HoraFile#getFileSeparator()
   */
  public static String getMyDir() {
    File file = new File("xyz");
    String sH = file.getAbsolutePath();
    int i = sH.lastIndexOf(HoraFile.getFileSeparator());
    sH = sH.substring(0, i);
    return sH;
  }
  /**
   * Diese Funktion
   * 
   * @return result
   * @since
   * @see HoraFile#getMyDir()
   */
  public static String getMyPrivatePfad() {
    String result = "";
    // ExeName.Boot.ini
    final String JARNAME = "leporello";
    final String BOOTINI = "boot.ini";
    // gibt es eine persönliche BootIni?
    // dort steht der PrivatPfad (und der Name der eigenen INI)
    String sH = JARNAME + "." + BOOTINI;
    result = HoraIni.LeseIniString(sH, "Startparameter", "PrivatPfad", "", true);
    // wenn leer dann in die boot.ini
    if (result.equals("")) {
      sH = BOOTINI;
      result = HoraIni.LeseIniString(sH, "Startparameter", "PrivatPfad", "", true);
    }
    if (result.equals("")) {
      // das eigene Verzeichnis
      result = getMyDir();
    }
    // System.getProperties().list(System.out);
    return result;
  }
  /**
   * 
   * 
   * @param sDir
   * @return boolean
   * @since
   */
  public static boolean makeDir(String sDir) {
    File dir = new File(sDir);
    return dir.mkdirs();
  }
  /**
   * 
   * 
   * @param sFile
   * @return boolean
   * @since
   * @see
   * HoraFile#getFileSeparator()
   */
  public static boolean makeDirForFile(String sFile) {
    File file = new File(sFile);
    String sH = file.getAbsolutePath();
    int i = sH.lastIndexOf(getFileSeparator());
    sH = sH.substring(0, i);
    return makeDir(sH);
  }
  /**
   * 
   * 
   * @param sQuelle ursprünglicher Name
   * @param sZiel neuer Name
   * @return boolean
   * @since
   */
  public static boolean renameFile(String sQuelle, String sZiel) {
    File q = new File(sQuelle);
    File z = new File(sZiel);
    return q.renameTo(z);
  }
  /**
   * 
   * @param sDatei
   * @param sSubDir
   * @return boolean
   * @since
   * @see
   * HoraTime#getUsersTimeZone()
   * @see
   * HoraFile#getFileSeparator()
   * @see
   * HoraFile#makeDirForFile(String)
   * @see
   * HoraFile#renameFile(String, String)
   */
  public static boolean renameToSubDirWithTimestamp(String sDatei, String sSubDir) {
    SimpleDateFormat fmt = new SimpleDateFormat();
    fmt.applyPattern("dd.MM.yyyy HH.mm.ss");
    fmt.setTimeZone(TimeZone.getTimeZone(HoraTime.getUsersTimeZone()));
    String sStempel = fmt.format(new java.util.Date());
    String sNameNeu = sSubDir + getFileSeparator() + "_bis_" + sStempel + ".txt";
    makeDirForFile(sNameNeu);
    return renameFile(sDatei, sNameNeu);
  }
  /**
   * 
   * @param sDir
   * @return boolean
   * @since
   * @see
   * HoraString#ohneBackSlash(String)
   * @see
   * HoraFile#schreibTestFile(String)
   * @see
   * HoraFile#getFileSeparator()
   */
  public static boolean schreibTestDir(String sDir) {
    boolean result = false;
    final String TEST = "HORATEST.TXT";
    sDir = HoraString.ohneBackSlash(sDir);
    result = schreibTestFile(sDir + getFileSeparator() + TEST);
    return result;
  }
  /**
   * 
   * @param sDatei
   * @return boolean
   * @since
   * @see
   * HoraFile#fileAppend(String, String)
   * @see
   * HoraFile#deleteFile(String)
   */
  static boolean schreibTestFile(String sDatei) {
    boolean result = false;
    if (fileAppend(sDatei, "Das ist ein Schreibtest.")) {
      if (deleteFile(sDatei)) {
        result = true;
      }
    }
    return result;
  }
  /**
   * @param dir
   * @param match
   * @return
   */
  public static ArrayList getFilesFromDir(String dir, String match) {
    ArrayList result = new ArrayList();
    File userdir = new File(dir);
    Dir tf = new Dir();
    tf.setMatch(match);
    String entries[] = userdir.list(tf);
    for (int i = 0; i < entries.length; i++) {
      result.add(dir + "\\" + entries[i]);
    }
    return result;
  }
  /**
   * Bereinigung der Backupordner -> alles älter als xx Tage wird gelöscht
   * Rückgabe true: das Datum wurde aktualisiert und kann gemerkt werden.
   * @param pfad
   * @param lastPflegeDatum
   * @param fileExt
   * @param days
   * @return 
   * 
   */
  public static boolean backupPflege(String pfad, int days, Date lastPflegeDatum, String fileExt) {
    long thirtyDays = days * HoraTime.C1TAG;// 2592000000l;
    ArrayList files = new ArrayList(0);
    boolean doIt = false;
    if (lastPflegeDatum == null) {
      doIt = true;
    }
    if (lastPflegeDatum.getTime() < (HoraTime.milliToDate(HoraTime.Heute())).getTime()) {
      doIt = true;
    }
    if (!doIt) {
      return false;
    }
    // aufräumen
    files = new ArrayList(0);
    // Personal oder Buchung bereinigen?
    // if (isPersonal)
    files = HoraFile.getFilesFromDir(pfad, fileExt);
    if (files != null) {
      if (files.size() != 0) {
        for (Iterator it = files.iterator(); it.hasNext();) {
          String datei = it.next().toString();
          if (HoraFile.fileAge(datei) + thirtyDays < HoraTime.Heute()) {
            HoraFile.deleteFile(datei);
          }
        }
      }
    }
    // Datum aktualisieren
    lastPflegeDatum = HoraTime.milliToDate(HoraTime.Heute());
    return true;
    // 
  }
  public static void printMemory() {
    int MegaBytes = 1024 * 1024;
    System.out.println("freeMemory  : " + (Runtime.getRuntime().freeMemory() / MegaBytes) + "MB");
    System.out.println("totalMemory : " + (Runtime.getRuntime().totalMemory() / MegaBytes) + "MB");
    System.out.println("maxMemory   : " + (Runtime.getRuntime().maxMemory() / MegaBytes) + "MB");
    System.out.println("PermGen     : " + (getPermGenMax() / MegaBytes) + "MB");
  }
  public static long getPermGenMax() {
    //System.out.println(ManagementFactory.getMemoryPoolMXBeans());
    for (MemoryPoolMXBean mx : ManagementFactory.getMemoryPoolMXBeans()) {
      if ("Perm Gen".equals(mx.getName())) {
        return mx.getUsage().getMax();
      }
    }
    throw new RuntimeException("Perm gen not found");
  }
  public static void listDir(File dir) {
    FileFilter fi = new FileFilter() {
      ;

      @Override
      public boolean accept(File pathname) {
        return pathname.getAbsolutePath().toUpperCase().contains(".css");
      }
    };
    File[] files = dir.listFiles();
    int z = 0;
    if (files != null) {
      for (int i = 0; i < files.length; i++) {
        if (files[i].isDirectory()) {
          //System.out.print(" (Ordner)\n");
          listDir(files[i]); // ruft sich selbst mit dem 
          // Unterverzeichnis als Parameter auf
        } else {
          if (files[i].getAbsolutePath().toLowerCase().contains(".css")) {
            try {
              System.out.print(".");
              z++;
              if (z > 50) {
                z = 0;
                System.out.println();
              }
              repairOpacity(files[i]);
            } catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
        }
      }
    }
  }
  private static void repairOpacity(File file) throws IOException {
    String datei = file.getAbsolutePath();
    InputStream in = new FileInputStream(file);
    String inhalt = HoraString.slurp(in);
    in.close();
    String[] newinhalt = new String[1];
    newinhalt[0] = inhalt;
    int i = 0;
    while (i >= 0)
      i = ersetze(i, newinhalt, "alpha(");
    i = 0;
    while (i >= 0)
      i = ersetze(i, newinhalt, "Alpha(");
    if (inhalt.equals(newinhalt[0]))
      return;
    PrintWriter out = new PrintWriter(datei);
    out.println(newinhalt[0]);
    out.flush();
    out.close();
  }
  private static int ersetze(int index, String[] inhalte, String suche) {
    String inhalt = inhalte[0];
    int v = inhalt.indexOf(suche, index);
    if (v == -1)
      return -1;
    v = v + suche.length();
    int b = inhalt.indexOf(")", v);
    String replace = inhalt.substring(v, b);
    if (!replace.contains(" "))
      return b;
    replace = replace.replaceAll(" ", "");
    replace = replace.replaceAll("\t", "");
    inhalt = inhalt.substring(0, v) + replace + inhalt.substring(b, inhalt.length());
    inhalte[0] = inhalt;
    return b;
  }
  public static void main(final String[] args) {
    System.out.println("Formatterfehler Alpha Opacity beseitigen.\n");
    File pfad = new File("");
    String p = pfad.getAbsolutePath();
    int i = p.lastIndexOf("\\");
    p = p.substring(0, i) + "\\sinfoserver";
    System.out.println("Verzeichnis: " + p);
    listDir(new File(p));
    System.out.println("\nfertig.");
  }
}

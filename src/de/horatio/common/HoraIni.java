/*
 * Created on 30.03.2005
 *
 * Datei: HoraIni.java
 * 
 * Letzte Änderung von $Author: schleese $
 * $Date: 2005/11/24 11:02:40 $
 * 
 * $Revision: 1.9 $
 * Copyright: Horatio GmbH, Berlin 2005
 */
package de.horatio.common;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Aufnahme aller für die Bearbeitung von für INI-Dateien
 * wichtigen Funktionen.
 * 
 * @author B. Geppert 
 */
public class HoraIni {

  private ArrayList<String>           commKopf;               // Kommentar Kopfteil
  private ArrayList<String>           iniComment;             // hier temp. alle Zeilen ohne "[xx]" und "="
  // sammeln
  private HashMap<String, IniSection> sections;               // ALLE Sektionen, key = [xx], val = Objekt
  // IniSection
  private IniSection                  iniSection;             // EIN Section-Objekt
  private IniKey                      iniKey;                 // EIN KEY-Objekt
  private File                        iniDat;
  private String                      iniName;
  private File                        iniTmp;
  private String                      iniTemp = "IniTemp.ini";
  private String                      line;
  private String                      sHilf;
  private boolean                     bKopf   = true;
  private boolean                     bLoad   = false;
  int                                 i;

  /**
   * Konstruktor
   */
  public HoraIni() {
  }
  /**
   * Konstruktor
   * Öffnen einer vorhandenen Datei oder Neuerstellung der angegebenen Datei.
   * Wenn die angegebene Datei vorhanden ist, wird sie eingelesen. Andernfalls
   * wird eine neue Datei erstellt.
   *
   * @param datname Pfad und Name der Ini-Datei.
   */
  public HoraIni(String datname) {
    BufferedReader br;
    iniName = datname;
    iniDat = new File(iniName);
    try {
      br = new BufferedReader(new FileReader(iniDat.toString()));
      try {
        commKopf = new ArrayList<String>(); //
        iniComment = new ArrayList<String>();
        sections = new HashMap<String, IniSection>(); //
        while ((line = br.readLine()) != null) {
          if (bKopf) {
            if (line.equals("")) {
              bKopf = false;
              iniComment.add(line); // 1. Leerzeile gehört zum Kopf
              commKopf = iniComment; // Kopf-Kommentar merken
              iniComment = new ArrayList<String>(); // neuen Komentarsammler anlegen
            } else {
              if (line.startsWith("[") && line.endsWith("]")) {
                bKopf = false;
                sections.put(line.toUpperCase(), iniSection = new IniSection()); // ein
                // IniData-Objekt
                // für
                // eine
                // Section
                iniSection.comment = iniComment;
                iniComment = new ArrayList<String>();
              } else {
                iniComment.add(line); // Kopf-Kommentarzeilen sammeln
              }
            }
          } else { // Kopf-Kommentar erledigt
            if (line.startsWith("[") && line.endsWith("]")) { // Section
              // gefunden
              sections.put(line.toUpperCase(), iniSection = new IniSection()); // ein
              // IniData-Objekt
              // für
              // eine
              // Section
              iniSection.comment = iniComment;
              iniComment = new ArrayList<String>();
            } else {
              if ((!line.startsWith("#")) && ((i = line.indexOf("=")) > 0)) { // Key
                // gefunden
                sHilf = line.substring(0, i);
                iniSection.data.put(sHilf.toUpperCase(), iniKey = new IniKey());
                iniKey.data = line.substring(i + 1, line.length());
                iniKey.comment = iniComment;
                iniComment = new ArrayList<String>();
              } else { // also Kommentarzeile
                iniComment.add(line);
              }
            }
          }
        } // while
        bLoad = true;
      } finally {
        br.close();
      }
    } catch (IOException e) {
      //      S_ystem.out.println(" IOException IniDatei : " + e.toString());
    }
  }
  /**
   * Prüfung der Instantiierung dieser Klasse. Nach der Instantiierung dieser
   * Klasse ist diese Methode unbedingt aufzurufen, um das erfolgreiche öffnen
   * bzw. erstellen der Ini-Datei zu prüfen.
   * <p>
   *
   * @return true, wenn das Öffnen einer vorhanden Datei bzw. die Neuerstellung
   *         erfolgreich war. Andernfalls false.
   */
  public boolean isLoaded() {
    return bLoad;
  }
  /**
   * @param sec
   * @return erg
   */
  // liefert alle Keys UND Werte einer gegebenen Section
  public HashMap ReadSection(String sec) {
    HashMap<String, String> erg = new HashMap<String, String>();
    sec = sec.toUpperCase();
    if (!sec.startsWith("[")) {
      sec = "[" + sec;
    } // Sonderservice
    if (!sec.endsWith("]")) {
      sec = sec + "]";
    }
    iniSection = sections.get(sec);
    if (iniSection != null) {
      Iterator it = iniSection.data.entrySet().iterator();
      while (it.hasNext()) {
        Map.Entry entry = (Map.Entry) it.next();
        iniKey = (IniKey) entry.getValue();
        erg.put((String) entry.getKey(), iniKey.data);
      }
    }
    return erg;
  }
  /**
   * liefert alle Keys einer gegebenen Section
   * @param sec
   * @return erg
   */
  public ArrayList ReadSectionKeys(String sec) {
    ArrayList<String> erg = new ArrayList<String>();
    sec = sec.toUpperCase();
    if (!sec.startsWith("[")) {
      sec = "[" + sec;
    } // Sonderservice
    if (!sec.endsWith("]")) {
      sec = sec + "]";
    }
    iniSection = sections.get(sec);
    if (iniSection != null) {
      Iterator it = iniSection.data.entrySet().iterator();
      while (it.hasNext()) {
        Map.Entry entry = (Map.Entry) it.next();
        erg.add((String) entry.getKey());
      }
    }
    erg.trimToSize(); // Damit bei Auswertung die Größe stimmt
    return erg;
  }
  /**
   * liefert alle Werte einer gegebenen Section
   * aufheben section = (HashMap)secobj.data.get( sec );
   * 
   * @param sec
   * @return erg
   */
  public ArrayList ReadSectionValues(String sec) {
    ArrayList<String> erg = new ArrayList<String>();
    sec = sec.toUpperCase();
    if (!sec.startsWith("[")) {
      sec = "[" + sec;
    } // Sonderservice
    if (!sec.endsWith("]")) {
      sec = sec + "]";
    }
    iniSection = sections.get(sec);
    if (iniSection != null) {
      Iterator it = iniSection.data.entrySet().iterator();
      while (it.hasNext()) {
        Map.Entry entry = (Map.Entry) it.next();
        iniKey = (IniKey) entry.getValue();
        erg.add(iniKey.data);
      }
    }
    erg.trimToSize(); // Damit bei Auswertung die Größe stimmt
    return erg;
  }
  /**
   * @param sec
   * @param val
   * @return erg
   */
  public String ReadSectionKey(String sec, String val) {
    String erg = "";
    sec = sec.toUpperCase();
    if (!sec.startsWith("[")) {
      sec = "[" + sec;
    } // Sonderservice
    if (!sec.endsWith("]")) {
      sec = sec + "]";
    }
    iniSection = sections.get(sec);
    if (iniSection != null) {
      Iterator it = iniSection.data.entrySet().iterator();
      while (it.hasNext()) { // hier while notwendig
        Map.Entry entry = (Map.Entry) it.next();
        iniKey = (IniKey) entry.getValue();
        if (iniKey.data.equals(val)) {
          erg = entry.getKey().toString();
          break;
        }
      }
    }
    return erg;
  }
  /**
   * @param sec
   * @param key
   * @return erg
   */
  public String ReadSectionValue(String sec, String key) {
    String erg = "";
    sec = sec.toUpperCase();
    key = key.toUpperCase();
    if (!sec.startsWith("[")) {
      sec = "[" + sec;
    } // Sonderservice
    if (!sec.endsWith("]")) {
      sec = sec + "]";
    }
    iniSection = sections.get(sec);
    if (iniSection != null) {
      iniKey = iniSection.data.get(key);
      if (iniKey != null) {
        erg = iniKey.data;
      }
    }
    return erg;
  }
  /**
   * @param sec
   * @param key
   * @return erg
   */
  public String ReadSectionValueNull(String sec, String key) {
    String erg = null;
    sec = sec.toUpperCase();
    key = key.toUpperCase();
    if (!sec.startsWith("[")) {
      sec = "[" + sec;
    } // Sonderservice
    if (!sec.endsWith("]")) {
      sec = sec + "]";
    }
    iniSection = sections.get(sec);
    if (iniSection != null) {
      iniKey = iniSection.data.get(key);
      if (iniKey != null) {
        erg = iniKey.data;
      }
    }
    return erg;
  }
  /**
   * @param sec
   * @param key
   * @return erg
   */
  public int ReadSectionValueInt(String sec, String key) {
    int erg = 0;
    sec = sec.toUpperCase();
    key = key.toUpperCase();
    if (!sec.startsWith("[")) {
      sec = "[" + sec;
    } // Sonderservice
    if (!sec.endsWith("]")) {
      sec = sec + "]";
    }
    iniSection = sections.get(sec);
    if (iniSection != null) {
      iniKey = iniSection.data.get(key);
      if (iniKey != null) {
        try {
          if (!iniKey.data.equals("")) {
            erg = Integer.parseInt(iniKey.data);
          }
        } catch (Exception ex) {
        }
      }
    }
    return erg;
  }
  /**
   * Wenn sec UND key vorhanden, dann update Wenn comm != null, dann
   * Eintrag/Übrschreiben vorhandener Kommentare
   * 
   * @param sec
   * @param key
   * @param val
   * @param secComm
   * @param keyComm
   * @return writeIni()
   */
  private boolean WriteSectionValue(String sec, String key, String val, ArrayList secComm, ArrayList keyComm) {
    sec = sec.toUpperCase();
    key = key.toUpperCase();
    if (!sec.startsWith("[")) {
      sec = "[" + sec;
    } // Sonderservice
    if (!sec.endsWith("]")) {
      sec = sec + "]";
    }
    iniSection = sections.get(sec);
    if (iniSection != null) {
      if (secComm != null) { // Section-Kommentar überschreiben
        iniSection.comment = secComm;
      }
      iniKey = iniSection.data.get(key);
      if (iniKey != null) {
        if (keyComm != null) { // Key-Kommentar überschreiben
          iniKey.comment = keyComm;
        }
        iniKey.data = val;
      } else { // neuen Key anlegen
        iniSection.data.put(key, iniKey = new IniKey());
        if (iniKey != null) {
          iniKey.comment = keyComm;
          iniKey.data = val;
        }
      }
    } else { // neue Section anlegen
      sections.put(sec, iniSection = new IniSection());
      if (iniSection != null) {
        iniSection.comment = secComm;
        iniSection.data.put(key, iniKey = new IniKey()); // neuen Key anlegen
        if (iniKey != null) {
          iniKey.comment = keyComm;
          iniKey.data = val;
        }
      }
    }
    return writeIni();
  }
  private boolean writeIni() {
    BufferedWriter bw;
    boolean erg = false;
    iniTmp = new File(iniTemp);
    try {
      bw = new BufferedWriter(new FileWriter(iniTmp.toString()));
      try {
        for (int i = 0; i < commKopf.size(); i++) { // Kopf schreiben
          bw.write(commKopf.get(i).toString());
          bw.newLine();
        }
        Iterator itSec = sections.entrySet().iterator();
        while (itSec.hasNext()) {
          Map.Entry entrySec = (Map.Entry) itSec.next();
          iniSection = (IniSection) entrySec.getValue();
          if (iniSection.comment != null) {
            for (int i = 0; i < iniSection.comment.size(); i++) {
              bw.write(iniSection.comment.get(i).toString()); // Kommentar der
              // Section
              bw.newLine();
            }
          }
          bw.write(entrySec.getKey().toString());
          bw.newLine();
          Iterator itKey = iniSection.data.entrySet().iterator();
          while (itKey.hasNext()) {
            Map.Entry entryKey = (Map.Entry) itKey.next();
            iniKey = (IniKey) entryKey.getValue();
            if (iniKey.comment != null) {
              for (int i = 0; i < iniKey.comment.size(); i++) {
                bw.write(iniKey.comment.get(i).toString()); // Kommentar des
                // Keys
                bw.newLine();
              }
            }
            bw.write(entryKey.getKey().toString() + "=" + iniKey.data);
            bw.newLine();
          }
        }
        for (int i = 0; i < iniComment.size(); i++) { // den Rest schreiben
          bw.write(iniComment.get(i).toString());
          bw.newLine();
        }
      } finally {
        bw.close(); // hier kein Halt bei exception !!
        //
        if (iniTmp.exists()) {
          iniDat.delete();
          iniTmp.renameTo(iniDat);
        }
      }
    } catch (Exception ex) {
      //		Monitor.printErr("   IOException IniDatei A : " + ex.toString());
    }
    return erg;
  }
  /**
   * @param sDatei
   * @param Abschnitt
   * @param Parameter
   * @param sDefault
   * @param bWrite
   * @return sH
   */
  public static String LeseIniString(String sDatei, String Abschnitt, String Parameter, String sDefault, boolean bWrite) {
    String sH = null;
    File inidat = new File(sDatei);
    if (!inidat.exists()) {
      //		Monitor.printLn("Datei wird neu erzeugt: " + sDatei);
      HoraFile.fileAppend(sDatei, "# " + sDatei);
    }
    if (!inidat.exists()) { // wenn keine Multi.ini besteht, dann anlegen
      //		Monitor.printErr("   Datei MULTI.INI konnte NICHT erstellt werden !");
    } else {
      try {
        HoraIni myini = new HoraIni(sDatei);
        if (true) { //(myini.isLoaded()) {
          sH = myini.ReadSectionValueNull("[" + Abschnitt + "]", Parameter);
          if (sH == null) {
            sH = sDefault;
            if (bWrite) {
              myini.WriteSectionValue("[" + Abschnitt + "]", Parameter, sH, null, null);
            }
          }
        }
      } catch (Exception ex) {
        //		Monitor.printErr("   EXCEPTION : " + ex.toString());
      }
    }
    return sH;
  }
  /**
   * @param sDatei
   * @param Abschnitt
   * @param sParameter
   * @param iDefault
   * @param bWrite
   * @return erg
   */
  public static int LeseIniInt(String sDatei, String Abschnitt, String sParameter, int iDefault, boolean bWrite) {
    int erg = 0;
    try {
      String sH = LeseIniString(sDatei, Abschnitt, sParameter, Integer.toString(iDefault), bWrite);
      if (sH == null) {
        sH = "0";
      }
      erg = Integer.parseInt(sH);
    } catch (Exception ex) {
      System.out.println("Exception beim einlesen von " + sDatei + ", Zeile: " + ex);
    }
    return erg;
  }
  /**
   * Wandelt true oder false in 1 oder 0 um.
   * 
   * @param b true oder false
   * @return 1, wenn b gleich true, ansonsten 0
   */
  public static int boolToInt(boolean b) {
    if (b)
      return 1;
    return 0;
  }
  /**
   * Wandelt den Wert 1 in true um, ansonsten false.
   * @param i 0 oder 1
   * @return true oder false
   */
  public static boolean intToBool(int i) {
    return (i == 1);
  }
  /**
   * Wandelt true in "1" oder false in "0" um.
   * 
   * @param b true oder false
   * @return "1", wenn b gleich true, ansonsten "0"
   */
  public static String boolToString(boolean b) {
    if (b)
      return "1";
    return "0";
  }
  /**
   * @param sDatei
   * @param Abschnitt
   * @param sParameter
   * @param bDefault
   * @param bWrite
   * @return sH
   */
  public static boolean LeseIniBool(String sDatei, String Abschnitt, String sParameter, boolean bDefault, boolean bWrite) {
    String sH = LeseIniString(sDatei, Abschnitt, sParameter, boolToString(bDefault), bWrite);
    if (sH == null) {
      sH = "0";
    }
    return (sH.equals("1"));
  }
  /**
   * Versucht sNewParameter genauso einzulesen wie LeseIniInt, wenn der Parameter aber
   * nicht vorhanden ist, dann wird zunächst versucht sOldParameter einzulesen. Wenn
   * sOldParameter gelesen werden konnte wird sNewParameter mit dem gelesenen Wert
   * zurückgeschrieben, ansonsten wird er mit iDefault zurückgeschrieben.
   * 
   * @param sDatei
   * @param Abschnitt
   * @param sNewParameter
   * @param sOldParameter
   * @param iDefault
   * @param bWrite
   * @return erg
   */
  public static int readReplaceIniInt(String sDatei, String Abschnitt, String sNewParameter, String sOldParameter, int iDefault,
      boolean bWrite) {
    int erg = 0;
    try {
      String sH = LeseIniString(sDatei, Abschnitt, sNewParameter, null/*Integer.toString(iDefault)*/, false);
      if (sH == null) {
        //sH = "0";
        sH = LeseIniString(sDatei, Abschnitt, sOldParameter, Integer.toString(iDefault), false);
        if (sH == null) {
          sH = LeseIniString(sDatei, Abschnitt, sNewParameter, Integer.toString(iDefault), true);
        } else {
          LeseIniString(sDatei, Abschnitt, sNewParameter, sH, true);
        }
      }
      erg = Integer.parseInt(sH);
    } catch (Exception ex) {
      System.out.println("Exception beim einlesen von " + sDatei + ", Zeile: " + ex);
    }
    return erg;
  }
  /**
   * 
   * @return result
   */
  public static String getBootstrapIniName() {
    // hallo.exe hat hallo.boot.ini in seinem Verzeichnis
    String result = "leporello";
    result = result + ".boot.ini";
    return result;
  }
  /**
   * Diese Funktion
   * 
   * @param startClassName
   * @param privPfad
   * @return result
   * @since
   */
  public static String getMyIniName(String startClassName, String privPfad) {
    // in der Regel wird der ExeName verwendet.
    // wenn nichts gefunden, dann im Pfad der EXE eine Ini suchen
    // dort wird alternativ die Bootstrap beschrieben
    String result = HoraIni.LeseIniString(HoraIni.getBootstrapIniName(), "Startparameter", "IniDatei", "", true);
    if (result.length() == 0) {
      result = startClassName;
    }
    result = privPfad + HoraFile.getFileSeparator() + result;
    return result;
  }
  /**
   * den eigenen Ini bestimmen
   * 
   * @param startClassName
   * @return result
   * @since
   */
  //public static String getMyIniName (String startClassName) throws Exception{
  public static String getMyIniName(String startClassName) {
    return getMyIniName(startClassName, HoraFile.getMyPrivatePfad());
  }
  /**
   * Liest direkt aus der INI-Datei eine Eigenschaft(Key) aus.
   *
   * @param Path Dateiobjekt
   * @param Section String
   * @param Key String
   * @return gefundener Wert, ansonsten Key
   */
  public static String leseIniZeile(File Path, String Section, String Key) {
    String Data = "", Data2 = "";
    int zahl = 0;
    BufferedReader in = null;
    try {
      in = new BufferedReader(new FileReader(Path));
      do {
        Data = in.readLine();
        if (Data == null)
          return null;
        for (int i = 1; i <= Data.toCharArray().length - 2; ++i) {
          Data2 = Data2 + Data.toCharArray()[i];
        }
        if (Data2.equals(Section)) {
          Data2 = "";
          break;
        } else
          if (Data2 == "" | Data2 == null) {
            return null;
          } else {
            Data2 = "";
          }
      } while (Data != null);
      while (Data != null) {
        Data = in.readLine();
        if (Data == null)
          return null;
        if (Data.toCharArray().length < Key.toCharArray().length) {
          continue;
        }
        for (int i = 0; i <= Key.toCharArray().length - 1; ++i) {
          Data2 = Data2 + Data.toCharArray()[i];
          zahl = i;
        }
        if (Data2.equals(Key)) {
          break;
        } else
          if (Data2 == "" | Data2 == null) {
            return null;
          } else {
            Data2 = "";
          }
      }
      in.close();
      Data2 = "";
      if (Data == null)
        return null;
      for (int f = zahl + 2; f <= Data.toCharArray().length - 1; ++f) {
        Data2 = Data2 + Data.toCharArray()[f];
      }
    } catch (FileNotFoundException e) {
      // 
      //			e.printStackTrace();
    } catch (IOException e) {
      //			e.printStackTrace();
    } finally {
      if (in != null)
        try {
          in.close();
        } catch (IOException e) {
        }
    }
    return Data2;
  }
  /**
   * Ändert direkt einen Wert in der INI-Datei.
   * 
   * @param Path Dateiobjekt
   * @param Section Name des Bereichs in der INI-Datei als String
   * @param Key Zu ändernde Eigenschaft als String
   * @param Value Wert der zuändernden Eigenschaft als 
   * @return true, wenn erfolgreich, ansonsten false
   */
  public static boolean SchreibeIniString(File Path, String Section, String Key, String Value) {
    Section = Section.toUpperCase();
    Key = Key.toUpperCase();
    String Data = "", Data2 = "";
    boolean second = false, erledigt = false;
    String nichts = "";
    try {
      BufferedReader in = new BufferedReader(new FileReader(Path));
      //while (in.ready())System.out.println(in.ready());
      /*
       * Section suchen
       */
      String trest = "";
      while (trest != null) {
        trest = in.readLine();
        if (trest == null)
          break;
        Data = Data + trest + ";";
      }
      in.close();
      String[] trest3 = HoraString.loeseStringAuf(Data, ";");
      while (!Path.canWrite()) {
      }
      PrintWriter out = new PrintWriter(new FileWriter(Path));
      for (int i = 0; i <= trest3.length - 1; ++i) {
        if (trest3[i] == null | trest3[i].equals(nichts))
          continue;
        if (trest3[i].equals("[" + Section + "]")) {
          second = true;
          out.println(trest3[i]);
          out.println(Key + "=" + Value);
          erledigt = true;
          continue;
        }
        if (second) {
          if (trest3[i].startsWith("[")) {
            second = false;
            out.println(trest3[i]);
            continue;
          } else
            if (trest3[i].toCharArray().length < Key.toCharArray().length) {
              out.println(trest3[i]);
              continue;
            }
          Data2 = "";
          for (int f = 0; f <= Key.toCharArray().length - 1; ++f) {
            Data2 = Data2 + trest3[i].toCharArray()[f];
          }
          if (Key.equals(Data2)) {
            second = false;
            continue;
            //out.println(Key + "=" + Value);
          }
          out.println(trest3[i]);
        } else {
          out.println(trest3[i]);
        }
      }
      if (!erledigt) {
        out.println("[" + Section + "]");
        out.println(Key + "=" + Value);
      }
      out.close();
    } catch (FileNotFoundException e) {
    } catch (IOException e) {
    }
    return true;
  }
  /**
   * Die Funktion ist nicht eindeutig!!!
   * 
   * @param Path Dateiobjekt
   * @param Section String
   * @return
   */
  public static boolean LoescheAusIni(File Path, String Section) {
    String Data = "";
    boolean second = false;
    String nichts = "";
    try {
      BufferedReader in = new BufferedReader(new FileReader(Path));
      //while (in.ready())System.out.println(in.ready());
      /*
       * Section suchen
       */
      String trest = "";
      while (trest != null) {
        trest = in.readLine();
        if (trest == null)
          break;
        Data = Data + trest + ";";
      }
      in.close();
      String[] trest3 = HoraString.loeseStringAuf(Data, ";");
      while (!Path.canWrite()) {
      }
      PrintWriter out = new PrintWriter(new FileWriter(Path));
      for (int i = 0; i <= trest3.length - 1; ++i) {
        if (trest3[i] == null | trest3[i].equals(nichts))
          continue;
        if (trest3[i].equals("[" + Section + "]")) {
          second = true;
          continue;
        }
        if (second) {
          if (trest3[i].startsWith("[")) {
            second = false;
            out.println(trest3[i]);
            continue;
          } else {
            continue;
          }
        } else {
          out.println(trest3[i]);
        }
      }
      out.close();
    } catch (FileNotFoundException e) {
    } catch (IOException e) {
    }
    return true;
  } //LoescheAusIni()
  public static void SchreibeIniString(String ini, String section, String key, String s) {
    File inidat = new File(ini);
    SchreibeIniString(inidat, section, key, s);
  }
} //class HoraIni

/**
 * Eine INI-Section mit Section-Kommentarzeile und den Keys
 */
class IniSection {

  /**
   * Comment for <code>data</code>
   */
  public TreeMap<String, IniKey> data;
  /**
   * Comment for <code>comment</code>
   */
  public ArrayList               comment;

  /**
   * 
   */
  public IniSection() {
    data = new TreeMap<String, IniKey>();
    comment = new ArrayList();
  }
} //class IniSection

/**
 * Ein INI-Key mit den Kommentarzeilen
 */
class IniKey {

  /**
   * Comment for <code>data</code>
   */
  public String    data;
  /**
   * Comment for <code>comment</code>
   */
  public ArrayList comment;

  /**
   * 
   */
  public IniKey() {
    data = "";
    comment = new ArrayList(5);
  }
} //class IniKey

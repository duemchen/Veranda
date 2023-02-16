/*
 * Created on 30.03.2005
 *
 * Datei: HoraString.java
 * 
 * Letzte Änderung von $Author: schleese $
 * $Date: 2005/11/24 11:02:40 $
 * 
 * $Revision: 1.12 $
 * Copyright: Horatio GmbH, Berlin 2005
 */
package de.horatio.common;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO Doku
 * @author A. Luth (aus uniProcs)
 */
public class HoraString {

  /**
   * Diese Funktion verschlüsselt einen String.
   * Sie enthält weitere Codierungsfunktionen.
   * 
   * @param sKennwort zu codierender String
   * @return result verschlüsselter String
   * @since
   * @see
   * HoraString#codiereXorString(String)
   * @see
   * HoraString#StrToHexAscii(String)
   */
  public static String codiere(String sKennwort) {
    String result = sKennwort;
    result = codiereXorString(result);
    result = StrToHexAscii(result);
    return result;
  }
  /**
   * Diese Funktion verschlüsselt einen String.
   * 
   * @param sKennwort zu codierender String
   * @return result verschlüsselter String
   * @since
   */
  public static String codiereXorString(String sKennwort) {
    String result = "";
    try {
      for (int i = 0; i < sKennwort.length(); i++) {
        // ^ = bitweises XOR
        result = result + ((char) (0xFF ^ sKennwort.charAt(i)));
      }
    } catch (Exception ex) {
    }
    return result;
  }
  /**
   * Diese Funktion decodiert einen String.
   * 
   * @param sKennwort String der decodiert werden soll
   * @return result decodierter String
   * @since
   * @see
   * HoraString#hexAsciiToStr(String)
   * @see
   * HoraString#codiereXorString(String)
   */
  public static String deCodiere(String sKennwort) {
    String result = sKennwort;
    result = hexAsciiToStr(result);
    result = codiereXorString(result);
    return result;
  }
  /**
   * 
   * 
   * @param sZeile String der verkürzt werden soll
   * @return sZeile verkürzter String
   * @since
   */
  public static String fKillLeerzeichen(String sZeile) {
    // Leerzeichen raus.
    while (true) {
      int i = sZeile.indexOf(' ');
      if (i == -1) {
        break;
      } else {
        sZeile = sZeile.substring(0, i) + sZeile.substring(i + 1);
      }
    }
    return sZeile;
  }
  /**
   * Diese Funktion entfernt Leerzeichen.
   * 
   * @param sH String der verkürzt werden soll
   * @return sH verkürzter String
   * @since
   */
  public static String fKillSpaces(String sH) {
    if (sH == null) {
      sH = "";
    }
    if (sH.indexOf(' ') > -1) {
      sH.trim();
    }
    if (sH.equals(" ")) {
      sH = "";
    }
    return sH;
  }
  /**
   * 
   * 
   * @param sH String der verlängert werden soll
   * @param sZeichen String mit dem verlängert werden soll
   * @param sollLänge gewünschte Länge
   * @return verlängerter String
   * @since
   */
  public static String fVorLängen(String sH, String sZeichen, int sollLänge) {
    if (!(sZeichen == null)) {
      if (!(sZeichen.length() == 0)) {
        while (sH.length() < sollLänge) {
          sH = sZeichen + sH;
        }
      }
    }
    return sH;
  }
  /**
   * Diese Funktion macht aus false --> 0 und aus true --> 1.
   *  
   * @param b umzuwandelnder boolean-Wert
   * @return String 0 oder 1
   * @since
   */
  public static String flagToStr(boolean b) {
    if (b) {
      return "1";
    } else {
      return "0";
    }
  }
  /**
   * 
   * 
   * @param sH String der verlängert werden soll
   * @param sZeichen Zeichen mit dem verlängert werden soll
   * @param sollLänge Länge des gewünschten Strings
   * @return sH verlängerter String
   * @since
   */
  public static String fNachLängen(String sH, String sZeichen, int sollLänge) {
    if (!(sZeichen == null)) {
      if (!(sZeichen.length() == 0)) {
        while (sH.length() < sollLänge) {
          sH = sH + sZeichen;
        }
      }
    }
    return sH;
  }
  /**
   * Diese Funktion setzt Vornullen.<br>
   * Beispiel:
   * <pre>
   * 146288 --> 146288
   * 544    --> 000544</pre>
   * 
   * @param sH String der verlängert werden soll
   * @param sollLänge int, gewünschte Länge
   * @return sH String mit Solllänge
   * @since
   */
  public static String fVornullen(String sH, int sollLänge) {
    if (sH == null) {
      sH = "";
    }
    while (sH.length() < sollLänge) {
      sH = "0" + sH;
    }
    return sH;
  }
  /**
   * @param sH
   * @param sollLänge
   * @param zeichen
   * @return
   */
  public static String fVornullen(String sH, int sollLänge, char zeichen) {
    if (sH == null) {
      sH = "";
    }
    while (sH.length() < sollLänge) {
      sH = zeichen + sH;
    }
    return sH;
  }
  public static String fVornullenStellengenau(String sH, int sollLänge, String zeichen) {
    //Arndt 21.07.2015 immer genau die übergebene Solllänge wie in Delphi.
    if (sH == null) {
      return "";
    }
    while (sH.length() > sollLänge) {
      if (!sH.substring(0, 1).equals(zeichen))
        break; // wenn vorn schon zuviel Aufgefüllt wurde -> entfernen
      sH = sH.substring(1);
    }
    if (sH.length() > sollLänge) {
      String sh = sH.substring(0, sollLänge);
      return sh;
    }
    while (sH.length() < sollLänge) {
      sH = zeichen + sH;
    }
    return sH;
  }
  /**
   * Funktion holt den Teilstring oder "" an der Position
   * iPos aus den mit sSeparator getrennten Gesamtstring .
   * 
   * @param sLine
   * @param sSeparator
   * @param iPos
   * @return
   */
  public static String GetASubStrAt(String sLine, String sSeparator, int iPos) {
    int i;
    ArrayList<String> arrList = new ArrayList<String>();
    String sHilf = "";
    for (i = 0; i < sLine.length(); i++) {
      if (sLine.charAt(i) == sSeparator.charAt(0)) {
        arrList.add(sHilf);
        sHilf = "";
      } else {
        sHilf = sHilf + sLine.charAt(i);
      }
    }
    if (!sHilf.equals("")) { // falls kein Trennzeichen am Ende !!
      arrList.add(sHilf);
    }
    arrList.trimToSize();
    for (i = 0; i < arrList.size(); i++) {
      if (i == iPos)
        return (arrList.get(i));
    }
    return ("");
  }
  /**
   * Diese Funktion sucht einen Parameter hinter einer bestimmten Kennung.
   * bis zum nächsten Semikolon.
   * Beispiel:
   * ;BAKO; --> KO
   * 
   * @param sKennung zu suchende Kennung
   * @param sZeile zu durchsuchende Zeile
   * @return sZeile Wert, Inhalt der Kennung
   * @since
   */
  public static String xGetParam(String sKennung, String sZeile) {
    // sucht die Kennung bis zum nächsten Semikolon
    // ;BAKO; liefert KO
    int i = sZeile.indexOf(sKennung);
    if (i == -1) {
      return "";
    }
    sZeile = sZeile.substring(i + sKennung.length());
    i = sZeile.indexOf(';');
    if (i == -1) {
      return sZeile;
    }
    sZeile = sZeile.substring(0, i);
    return sZeile;
  }
  /**
   * Diese Funktion sucht einen Parameter hinter einer bestimmten Kennung.
   * bis zum nächsten Semikolon.
   * Beispiel:
   * ;BAKO; --> KO
   * 
   * @param sKennung zu suchende Kennung
   * @param sZeile zu durchsuchende Zeile
   * @return sZeile Wert, Inhalt der Kennung
   * @since
   */
  public static String GetParam(String sKennung, String sZeile) {
    // sucht die Kennung bis zum nächsten Semikolon
    // ;BAKO; liefert KO
    //sZeile = "BAKO;" + sZeile; //Arndt 07.05.2013 Test muss auch noch funktinieren macht es auch.
    //sKennung = "BA"; //Test //Arndt 07.05.2013 Test muss auch noch funktinieren macht es auch.
    String sKennungOld = sKennung;
    String sZeileOld = sZeile;
    sZeile = ";" + sZeile; //Arndt 07.05.2013 ;xyD= und ;D= sollen unterscheidbar sein.
    sKennung = ";" + sKennung; //Arndt 07.05.2013 ;xyD= und ;D= sollen unterscheidbar sein.
    int i = sZeile.indexOf(sKennung);
    if (i == -1) {
      return "";
    }
    sZeile = sZeile.substring(i + sKennung.length());
    i = sZeile.indexOf(';');
    if (i == -1) {
      return sZeile;
    }
    sZeile = sZeile.substring(0, i);
    //  Arndt 17.05.2013 Test wegen Umbau dieser Funktion
    //    String sParamOld = xGetParam(sKennungOld, sZeileOld);
    //    if (!sZeile.equals(sParamOld)) {
    //      HoraFile.fileAppend("D:\\ParamUnterschiede.txt", "");
    //      HoraFile.fileAppend("D:\\ParamUnterschiede.txt", "Gesamte Paramzeile:" + sZeileOld);
    //      HoraFile.fileAppend("D:\\ParamUnterschiede.txt", "Parameteralt=" + sKennung + sParamOld + "  ParameterNeu=" + sKennung
    //          + sZeile);
    //    }
    return sZeile;
  }
  /**
   * Sucht im String nach Param=Wert; Parameter bitte mit Gleichheitszeichen
   * angeben Auch Anwendbar bei ParamWERT;Param2Wert2; usw.
   * 
   * @param sParamMitGleich
   * @param sZeile
   * @return result String
   * @since
   * @see
   * HoraString#fKillLeerzeichen(String)
   * 
   */
  public static String getsParam(String sParamMitGleich, String sZeile) {
    final char CTRENN = ';';
    String result = "";
    sParamMitGleich = fKillLeerzeichen(sParamMitGleich);
    sZeile = ";" + sZeile; //Arndt 07.05.2013 ;xyD= und ;D= sollen unterscheidbar sein.
    sParamMitGleich = ";" + sParamMitGleich; //Arndt 07.05.2013 ;xyD= und ;D= sollen unterscheidbar sein.
    sZeile = sZeile.toUpperCase(); //Arndt 11.09.2014 Das muss sein falls Groß u. Kleinschreibung unterschiedlich ist.
    sParamMitGleich = sParamMitGleich.toUpperCase(); //Arndt 11.09.2014 Das muss sein falls Groß u. Kleinschreibung unterschiedlich ist.
    int i = sZeile.indexOf(sParamMitGleich);
    if (!(i == -1)) {
      i = i + sParamMitGleich.length();
      result = sZeile.substring(i);
      i = result.indexOf(CTRENN);
      if (i > -1) {
        result = result.substring(0, i);
      }
    }
    return result;
  }
  /**
   * erzeugt den Lineseparator welches das System bereitstellt. (zum Bsp.\n oder \r\n oder \r)
   */
  public static String getLineSeparator() {
    return System.getProperty("line.separator");
  }
  /**
   * Verfahren aus leporello übernommen, um "verschleierte" DB-Passworte zu
   * dekodieren Wegen Unicode-Wandlung wird Passwort in Hex-ASCII übertragen
   * 
   * @param sKennwort String
   * @return sKennwort String
   * @since
   * @see
   * HoraString#hexAsciiToStr(String, PrintStream)
   */
  public static String hexAsciiToStr(String sKennwort) {
    sKennwort = hexAsciiToStr(sKennwort, System.out);
    return sKennwort;
  }
  /**
   * Verfahren aus leporello übernommen, um "verschleierte" DB-Passworte zu
   * dekodieren Wegen Unicode-Wandlung wird Passwort in Hex-ASCII übertragen
   * 
   * @param sKennwort String
   * @param out Ausgabe-Stream für Fehler
   * @return sKennwort String
   * @since
   * @see
   * HoraTime#ZeitStempel()
   */
  public static String hexAsciiToStr(String sKennwort, PrintStream out) {
    StringBuffer sB_1 = new StringBuffer(50);
    int bB_16;
    int bB_1;
    try {
      for (int i = 0; i < sKennwort.length() / 2; i++) {
        if (sKennwort.charAt(i * 2) < 0x3A) {
          bB_16 = ((byte) sKennwort.charAt(i * 2) - 0x30) * 16;
        } else {
          bB_16 = ((byte) sKennwort.charAt(i * 2) - 0x37) * 16;
        }
        if (sKennwort.charAt(i * 2 + 1) < 0x3A) {
          bB_1 = (byte) sKennwort.charAt(i * 2 + 1) - 0x30;
        } else {
          bB_1 = (byte) sKennwort.charAt(i * 2 + 1) - 0x37;
        }
        sB_1.append((char) (bB_16 + bB_1));
      }
      sKennwort = sB_1.toString();
    } catch (Exception ex) {
      //			Monitor.printErr(HoraTime.ZeitStempel() + " hexAsciiToStr: " + ex.toString());
      out.println(HoraTime.ZeitStempel() + " hexAsciiToStr: " + ex.toString());
    }
    return sKennwort;
    /*
     * for (int i = 0; i < sKennwort.length(); i++) { sB_2.append((char)
     * (0xFF ^ sKennwort.charAt(i))); // ^ = bitweises XOR }
     * 
     * return sB_2.toString();
     */
  }
  /**
   * Diese Funktion macht aus einem hexadezimal-Wert einen ASCII-String.
   * 
   * @param hexWert int hexadezimal-Wert
   * @return result String ASCII-Zeichenfolge
   * @since
   */
  public static String hexToASCII(int hexWert) {
    // aus 1A 31 41 machen
    int b = 0;
    String s = "";
    String result = "";
    b = hexWert;
    b = (b & (0xF0));
    b = (b / 16);
    if (b > 9) {
      b = (0x37 + b);
    } else {
      b = (0x30 + b);
    }
    char[] aChar = {(char) b};
    s = new String(aChar);
    result = s;
    b = hexWert;
    b = (b & (0x0F));
    if (b > 9) {
      b = (0x37 + b);
    } else {
      b = (0x30 + b);
    }
    char[] bChar = {(char) b};
    s = new String(bChar);
    result = result + s;
    return result;
  }
  /**
   * @param sDatei
   * @param sAbs
   * @param Var
   * @param Wert
   */
  public static void schreibeIniString(String sDatei, String sAbs, String Var, String Wert) {
    File file = new File(sDatei);
    sAbs = sAbs.toUpperCase();
    Var = Var.toUpperCase();
    HoraIni.SchreibeIniString(file, sAbs, Var, Wert);
  }
  /**
   * @param sDatei
   * @param sAbs
   * @param Var
   * @param sDefault
   * @param bSchreib
   * @return
   */
  public static String leseIniStrCodiert(String sDatei, String sAbs, String Var, String sDefault, boolean bSchreib) {
    String result = "";
    String sH = HoraIni.LeseIniString(sDatei, sAbs, Var, sDefault, bSchreib);
    if (isCodiert(sH)) {
      result = deCodiere(sH);
    } else {
      result = sH;
      sH = codiere(sH);
      schreibeIniString(sDatei, sAbs, Var, sH);
    }
    return result;
  }
  /**
   * Diese Funktion wandelt int-Typen in Strings.
   * 
   * @param i int
   * @return i String
   * @since
   */
  public static String intToStr(int i) {
    return Integer.toString(i);
  }
  /**
   * Test, ob ein String nur Ziffern 0..9 oder Zeichen A..Z, a..z enthält
   * 
   * @param sTest
   * @return true / false
   */
  public static boolean isAlphaNumerik(String sTest) {
    StringBuffer sBuff = new StringBuffer(sTest);
    for (int i = 0; i < sBuff.length(); i++) {
      if (((sBuff.charAt(i) < '0') || (sBuff.charAt(i) > '9')) && ((sBuff.charAt(i) < 'A') || (sBuff.charAt(i) > 'Z'))
          && ((sBuff.charAt(i) < 'a') || (sBuff.charAt(i) > 'z'))) {
        return (false); // kein break erforderlich
      }
    }
    return (true);
  }
  /**
   * Diese Funktion
   * 
   * @param sTest
   * @return true / false
   */
  public static boolean isAlphaNumerikSpace(String sTest) {
    StringBuffer sBuff = new StringBuffer(sTest);
    for (int i = 0; i < sBuff.length(); i++) {
      if (((sBuff.charAt(i) < '0') || (sBuff.charAt(i) > '9')) && ((sBuff.charAt(i) < 'A') || (sBuff.charAt(i) > 'Z'))
          && ((sBuff.charAt(i) < 'a') || (sBuff.charAt(i) > 'z')) && (sBuff.charAt(i) < ' ')) {
        return (false); // kein break erforderlich
      }
    }
    return (true);
  }
  /**
   * 
   * 
   * @param sH String
   * @return result boolean
   * @since
   */
  public static boolean isCodiert(String sH) {
    // woran erkennen , ob schon codiert?
    // nur Zahlen 0..9 A..F
    String muster = "0123456789ABCDEF";
    boolean result = true;
    char c;
    for (int i = 0; i < sH.length(); i++) {
      c = sH.charAt(i);
      if (muster.indexOf(c) == -1) {
        // ein falsches Zeichen reicht.
        result = false;
        break;
      }
    }
    return result;
  }
  /**
   * 
   * 
   * @param sH String
   * @return result String
   * @since
   * @see
   * HoraFile#getFileSeparator()
   */
  public static String ohneBackSlash(String sH) {
    String result = sH;
    if (!(sH.length() == 0)) {
      int i = sH.length();
      if ((sH.substring(i - 1, i)).equals(HoraFile.getFileSeparator())) {
        result = sH.substring(0, i - 1);
      }
    }
    return result;
  }
  /**
   * Verfahren aus leporello übernommen, um "verschleierte" DB-Passworte zu
   * dekodieren Wegen Unicode-Wandlung wird Passwort in Hex-ASCII übertragen
   * 
   * @param sKennwort
   * @return sB_2.toString()
   */
  public static String kodiere_dekodiereText(String sKennwort) {
    StringBuffer sB_1 = new StringBuffer(50);
    StringBuffer sB_2 = new StringBuffer(50);
    int bB_16;
    int bB_1;
    for (int i = 0; i < sKennwort.length() / 2; i++) {
      if (sKennwort.charAt(i * 2) < 0x3A) {
        bB_16 = ((byte) sKennwort.charAt(i * 2) - 0x30) * 16;
      } else {
        bB_16 = ((byte) sKennwort.charAt(i * 2) - 0x37) * 16;
      }
      if (sKennwort.charAt(i * 2 + 1) < 0x3A) {
        bB_1 = (byte) sKennwort.charAt(i * 2 + 1) - 0x30;
      } else {
        bB_1 = (byte) sKennwort.charAt(i * 2 + 1) - 0x37;
      }
      sB_1.append((char) (bB_16 + bB_1));
    }
    sKennwort = sB_1.toString();
    for (int i = 0; i < sKennwort.length(); i++) {
      sB_2.append((char) (0xFF ^ sKennwort.charAt(i)));
      // ^ = bitweises XOR
    }
    return sB_2.toString();
  }
  /**
   * Test, ob ein String nur Ziffern 0..9 enthält
   * 
   * @param sTest
   * @return true / false
   */
  public static boolean isNumerik(String sTest) {
    StringBuffer sBuff = new StringBuffer(sTest);
    for (int i = 0; i < sBuff.length(); i++) {
      if ((sBuff.charAt(i) < '0') || (sBuff.charAt(i) > '9')) {
        return (false); // kein break erforderlich
      }
    }
    return (true);
  }
  /**
   * Diese Funktion
   * 
   * @param output
   * @param sInput
   * @return true / false
   */
  public static boolean SendeString(DataOutputStream output, String sInput) {
    byte[] token = new byte[2000]; //
    try {
      token = sInput.getBytes();
      output.write(token);
      output.flush();
      return true;
    } catch (IOException ex) {
      return false;
    }
  }
  /**
   * Diese Funktion sorgt für eine Übersichtlichkeit von Ausschriften etc..
   * Der übergebene String wird mit Leerzeichen verlängert.
   * Beispiel:
   * 11 ab 4.0				--> 11   ab        4.0
   * 5777 abteilung 237.64	--> 5777 abteilung 237.64
   * 
   * @param sH String
   * @param iPosition int
   * @return result verlängerter String
   * @since Feb 05
   */
  public static String setPlatzhalter(String sH, int iPosition) {
    String result = sH;
    for (int i = 0; i < iPosition - sH.length(); i++) {
      result += " ";
    }
    return result;
  }
  /**
   * Diese Funktion zerteilt einen String an jedem Semikolon.
   * Trennzeichen ist ein Semikolon und wird in der Funktion fest vorgegeben.
   * 
   * @param sLine Zeichenfolge, die geteilt werden soll
   * @return aL ArrayList
   * @since
   * @see
   * HoraString#StringEmptyTokenizer(String, char, PrintStream)
   */
  public static ArrayList<String> StringEmptyTokenizer(String sLine) {
    final char CTRENN = ';';
    return StringEmptyTokenizer(sLine, CTRENN, System.out, String.class);
  }
  /**
   * Diese Funktion zerteilt einen String an jedem Semikolon.
   * Trennzeichen wird an Funktion übergeben.
   * 
   * @param sLine String, Zeichenfolge, die geteilt werden soll
   * @param cTrenn char, Zeichen, an dem getrennt werden soll
   * @return erg ArrayList
   * @since
   * @see
   * HoraString#StringEmptyTokenizer(String, char, PrintStream)
   */
  public static ArrayList<String> StringEmptyTokenizer(String sLine, char cTrenn) {
    return StringEmptyTokenizer(sLine, cTrenn, System.out, String.class);
  }
  /**
   * 
   * @param sLine
   * @param cTrenn
   * @param clazz
   * @return
   */
  public static <T> ArrayList<T> StringEmptyTokenizer(String sLine, char cTrenn, Class<T> clazz) {
    return StringEmptyTokenizer(sLine, cTrenn, System.out, clazz);
  }
  /**
   * Diese Funktion zerteilt einen String an jedem Trenner.
   * 
   * @param sLine String, ZZeichenfolge, die geteilt werden soll
   * @param cTrenn char, Zeichen, an dem getrennt werden soll
   * @param out PrintStream zur Fehlerausschrift
   * @param ausgabeForm Class
   * @return aL ArrayList
   * @since
   */
  public static <T> ArrayList<T> StringEmptyTokenizer(String sLine, char cTrenn, PrintStream out, Class<T> ausgabeForm) {
    ArrayList<T> erg = new ArrayList<T>();
    String sHilf = "";
    try {
      for (int i = 0; i < sLine.length(); i++) {
        if (sLine.charAt(i) == cTrenn) {
          erg.add(getValue(sHilf, ausgabeForm));
          sHilf = "";
        } else {
          sHilf = sHilf + sLine.charAt(i);
        }
      }
      if (!sHilf.equals("")) { // falls kein Trennzeichen am Ende !!
        erg.add(getValue(sHilf, ausgabeForm));
      }
    } catch (Exception ex) {
      //            Monitor.printErrES3("Allgemein uniProcs.java - " + ex.toString() + " - sLine: " + sLine);
      //            Monitor.printErr(ex.toString());
      out.println("HoraString.StringEmptyTokenizer");
      out.println("sLine: " + sLine);
    }
    erg.trimToSize();
    return erg;
  }
  private static <T> T getValue(String val, Class<T> clazz) {
    if (clazz.equals(Integer.class))
      return (T) Integer.valueOf(val);
    return (T) val;
  }
  /**
   * Diese Funktion 
   * 
   * @param sSource
   * @param iStart
   * @param iEnd
   * @param sCompare
   * @return erg
   * @since 041209
   * @author Horn
   */
  public static boolean SubStringIqualsIgnoreCase(String sSource, int iStart, int iEnd, String sCompare) {
    boolean erg = false;
    if (iEnd < sSource.length() + 1)
      erg = sSource.substring(iStart, iEnd).equalsIgnoreCase(sCompare);
    return erg;
  }
  /**
   * Diese Funktion macht aus einem hexadezimal-Wert einen ASCII-String.
   * 
   * @param sH String, hexadezimal-Wert
   * @return result String, ASCII-Zeichenfolge
   * @since 19.11.2003
   * @see
   * HoraString#hexToASCII(int)
   * 
   * aus 123 wird 313233  
   * 
   */
  public static String StrToHexAscii(String sH) {
    // Dümchen 19.11.2003 aus 123 wird 313233
    String result = "";
    try {
      for (int i = 0; i < sH.length(); i++) {
        result = result + hexToASCII(sH.charAt(i));
      }
    } catch (Exception ex) {
    }
    return result;
  }
  /**
   * Diese Funktion liefert Fehlerausschriften
   * 
   * @param sStr String
   * @since
   * @see
   * HoraString#ZeigeStackTrace(String, PrintStream)
   */
  public static void ZeigeStackTrace(String sStr) {
    ZeigeStackTrace(sStr, System.out);
  }
  /**
   * Diese Funktion liefert Fehlerausschriften
   * 
   * @param sStr String
   * @param out PrintStream für Fehlerausschrift
   * @since
   */
  public static void ZeigeStackTrace(String sStr, PrintStream out) {
    StackTraceElement trace[] = new Throwable().getStackTrace();
    for (int i = 0; i < trace.length; i++) {
      StackTraceElement ste = trace[i];
      out.println(ste);
    }
  }
  /**
   * Funktion sollte schnell ersetzt werden!
   * 
   * @param text
   * @param trenner
   * @return
   */
  public static String[] loeseStringAuf(String text, String trenner) {
    String[] rückgabe = new String[2];
    int f = 0;
    String wert = "";
    String nichts = "";
    String[] halten;
    if (!text.endsWith(trenner))
      text = text + trenner;
    for (int i = 0; i <= text.toCharArray().length - 1; ++i) {
      //System.err.println("Nach " + i +" anläufen noch da! Array: ");
      if (text.toCharArray()[i] == trenner.toCharArray()[0]) {
        if (wert.equals(nichts))
          continue;
        rückgabe[f] = wert;
        wert = "";
        halten = new String[rückgabe.length];
        for (int z = 0; z <= rückgabe.length - 1; ++z) {
          if (rückgabe[z] == null)
            break;
          halten[z] = rückgabe[z];
          //System.err.println("Array "+ z + ". : " + halten[z]);
        }
        rückgabe = new String[rückgabe.length + 1];
        ++f;
        for (int g = 0; g <= halten.length - 1; ++g) {
          if (halten[g] == null)
            break;
          rückgabe[g] = halten[g];
        }
        continue;
      }
      wert = wert + text.toCharArray()[i];
    }
    halten = rückgabe;
    rückgabe = new String[rückgabe.length - 2];
    for (int c = 0; c <= rückgabe.length - 1; ++c) {
      //if (rückgabe[c] == null)
      rückgabe[c] = halten[c];
    }
    return rückgabe;
  } //loeseStringAuf()
  /**
   * Verschlüsselt einen String, und gibt den verschlüsselten String zurück.
   * 
   * @param text
   * @return
   * @throws NoSuchAlgorithmException
   * @throws NoSuchProviderException
   */
  public static String createMD5HashCode(String text) throws NoSuchAlgorithmException, NoSuchProviderException {
    String result = text;
    if (text != null) {
      StringBuffer code = new StringBuffer();
      //the hash code
      MessageDigest messageDigest = MessageDigest.getInstance("MD5", "SUN");
      String plain = text;
      byte bytes[] = plain.getBytes();
      byte digest[] = messageDigest.digest(bytes);
      //create code
      for (int i = 0; i < digest.length; ++i) {
        code.append(Integer.toHexString(0x0100 + (digest[i] & 0x00FF)).substring(1));
      }
      result = code.toString();
    }//else: input unavailable
    return result;
  }//createMD5HashCode
  /**
   * Returns a MessageDigest for the given <code>algorithm</code>.
   * ("MD5", "SHA")
   * @param algorithm The MessageDigest algorithm name.
   * @param sPass Password
   * @return Hash.
   * @throws NoSuchAlgorithmException if algorithm is unknown
   */
  public static String codeHash(String algorithm, byte[] sPass) throws NoSuchAlgorithmException {
    MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
    byte[] erg = messageDigest.digest(sPass);
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < erg.length; i++) {
      sb.append(Integer.toHexString((erg[i] & 0xf0) >> 4));
      sb.append(Integer.toHexString(erg[i] & 0x0f));
    }
    return sb.toString();
  }
  /**
   * Returns a MessageDigest for the given <code>algorithm</code>.
   * ("MD5", "SHA")
   * @param algorithm The MessageDigest algorithm name.
   * @param sPass Password
   * @return Hash.
   * @throws NoSuchAlgorithmException if algorithm is unknown
   */
  public static String codeHash(String algorithm, String sPass) throws NoSuchAlgorithmException {
    return codeHash(algorithm, sPass.getBytes());
  }
  /**
   * Returns a MessageDigest for algorithm "SHA".
   * @param sPass Password
   * @return Hash.
   * @throws NoSuchAlgorithmException if algorithm  "SHA" is unknown
   */
  public static String codeSHA1Hash(String sPass) throws NoSuchAlgorithmException {
    return codeHash("SHA", sPass.getBytes());
  }
  /**
   * Diese Funktion ersetzt Stellen in einem Text. 
   * 
   * @param s String zu durchsuchen
   * @param search String zu ersetzen
   * @param replace String ersetzen mit
   * @return result String ersetzter String
   */
  public static String replace(String s, String search, String replace) {
    int start = 0, pos = 0;
    StringBuffer result = new StringBuffer(s.length());
    while ((pos = s.indexOf(search, start)) >= 0) {
      result.append(s.substring(start, pos));
      result.append(replace);
      start = pos + search.length();
    }
    result.append(s.substring(start));
    return result.toString();
  }
  /**
   * alle + zu leerzeichen alle Umlaute %FC ü
   * 
   * @param html
   * @return
   */
  public static String xhtmlToText(String html) {
    // TODO Alexander weitere Sonderzeichen und Tab usw.
    // fehlen hier mit sicherheit.
    String result = html;
    if (result == null)
      return result;
    result = replace(result, "%09", " ");
    result = replace(result, "%0D", " ");
    result = replace(result, "%0A", " ");
    result = replace(result, "+", " ");
    result = replace(result, "%E4", "ä");
    result = replace(result, "%F6", "ö");
    result = replace(result, "%FC", "ü");
    result = replace(result, "%DF", "ß");
    result = replace(result, "%C4", "Ä");
    result = replace(result, "%D6", "Ö");
    result = replace(result, "%DC", "Ü");
    // http://www.flyingtux.de/html/tools_html_sonderzeichen.html
    result = replace(result, "%2C", ",");
    result = replace(result, "%3B", ";");
    result = replace(result, "%21", "!");
    result = replace(result, "%3F", "?");
    result = replace(result, "%3A", ":");
    result = replace(result, "%2B", "+");
    result = replace(result, "%28", "(");
    result = replace(result, "%29", ")");
    // UTF-8 //UTF-8//UTF-8//UTF-8//UTF-8//UTF-8//UTF-8//UTF-8//UTF-8
    result = replace(result, "%C3%A4", "ä");
    result = replace(result, "%C3%B6", "ö");
    result = replace(result, "%C3%BC", "ü");
    result = replace(result, "%C3%9F", "ß");
    result = replace(result, "%C3%84", "Ä");
    result = replace(result, "%C3%96", "Ö");
    result = replace(result, "%C3%9C", "Ü");
    return result;
  }
  public static String replaceZeichenInAnfuerungsZeichen(String xmlString, String von, String zu) {
    //Arndt 05.12.2012 Übergebene Zeichen oder Zeichenfolgen 
    //werden im xmlString zwischen Anführungszeichen gesucht und durch übergebenen zu ausgewechselt
    //HoraFile.deleteFile("D:/kleiner0.txt");
    //HoraFile.fileAppend("D:/kleiner0.txt", xmlString);
    //final String alt = "\".*?" + von + ".*?\""; //sucht nach 
    //final String alt = "\".*?<.*?\""; //sucht nach 
    //final String alt = "\".*" + von + ".*\"";
    //    final String alt = "\".*?.*?\"";
    final String alt = "\".*?\"";
    String neu = ""; // und ersetzt 
    Pattern pattern = Pattern.compile(alt);
    Matcher matcher = pattern.matcher(xmlString);
    int i = 0;
    while (true) {
      // Matcher matcher = pattern.matcher(xmlString);
      if (!matcher.find()) {
        // bis nicht mehr gefunden
        break;
      }
      neu = matcher.group();
      int beginIndex = neu.indexOf(von);
      if (neu.indexOf(von) == -1)
        continue;
      String sh = neu.substring(beginIndex + 1, beginIndex + von.length() + 1); // Eine Stelle nach der länge des Suchstrings darf der Suchstring nicht mehr gefunden werden.
      if (sh.equals(von))
        continue;
      int v = matcher.toMatchResult().start();
      int b = matcher.toMatchResult().end();
      int ivon = v + i;
      int ibis = b + i;
      //neu = matcher.group();
      neu = HoraString.replace(neu, von, zu);
      // einzeln ersetzen 
      // böses rausschneiden und durch gewünschtes ersetzen
      //xmlString = xmlString.substring(0, v) + neu + xmlString.substring(b, xmlString.length());
      xmlString = xmlString.substring(0, ivon) + neu + xmlString.substring(ibis, xmlString.length());
      i = i + (zu.length() - (von.length()));
    }
    //HoraFile.deleteFile("D:/kleiner.txt");
    //HoraFile.fileAppend("D:/kleiner.txt", xmlString);
    return xmlString;
  }
  public static String replaceVerboteneXMLZeichenInAnfuerungsZeichen(String xmlString) {
    //Arndt 05.12.2012 verbotene XML-Zeichen (noch erweiterbar) oder Zeichenfolgen 
    //werden im xmlString zwischen Anführungszeichen gesucht und durch übergebenen zu ausgewechselt
    //HoraFile.deleteFile("D:/kleiner0.txt");
    //HoraFile.fileAppend("D:/kleiner0.txt", xmlString);
    xmlString = replaceZeichenInAnfuerungsZeichen(xmlString, "<", "kleiner");
    xmlString = replaceZeichenInAnfuerungsZeichen(xmlString, ">", "groeßer");
    //HoraFile.deleteFile("D:/kleiner.txt");
    //HoraFile.fileAppend("D:/kleiner.txt", xmlString);
    return xmlString;
  }
  /**
   * @param sXML
   * @return
   */
  public static String xmlCleaner(String sXML) {
    // intelligent Groess schreiben (Bis zu den Trennern und wieder
    // danach, Feldinhalte also belassen.)
    // TODO korrekte behandlung der Sonderzeichen
    // TODO Filter für Sonderzeichen
    // sH = sH.replaceAll("&", "+");
    // sH = sH.replaceAll("<", "*");
    StringBuffer result = new StringBuffer();
    boolean flag = true;
    char anFühr = '"';
    char zei;
    for (int i = 0; i < sXML.length(); i++) {
      zei = sXML.charAt(i);
      if (zei == anFühr) {
        if (!flag) {
          flag = true;
        } else {
          if (flag) {
            flag = false;
          }
        }
      }
      if (flag) {
        result.append(zei);
      } else {
        char aChar = zei;
        switch (aChar) {
          case '°' :
            result.append("&#176;");
            break;
          case '§' :
            result.append("&#167;");
            break;
          case '&' :
            // result.append("+");
            result.append("&#38;");
            break;
          case 'Ä' :
            result.append("&#196;");
            break;
          case 'Ö' :
            result.append("&#214;");
            break;
          case 'Ü' :
            result.append("&#220;");
            break;
          case 'ä' :
            result.append("&#228;");
            break;
          case 'ö' :
            result.append("&#246;");
            break;
          case 'ü' :
            result.append("&#252;");
            break;
          case 'ß' :
            result.append("&#223;");
            break;
          /*        ReplaceString(sZeile, '&', '&amp;');
           ReplaceString(sZeile, '''', '&apos;');
           ReplaceString(sZeile, '<', '&lt;');
           ReplaceString(sZeile, '>', '&gt;');
           //        ReplaceString(sZeile, '"', '&quot;');
           * 
           */
          case '\'' :
            result.append("&apos");
            break;
          case '<' :
            //result.append("*");
            result.append("&#60;");
            break;
          case '>' :
            result.append("&#62;");
            break;
          default :
            result.append(aChar);
            break;
        }
        // result = result + ("" + aChar).toUpperCase();
      }
    }
    String sH = result.toString();
    return sH;
  }
  /**
   * 
   * @param in
   * @return
   * @throws IOException
   */
  public static String slurp(InputStream in) throws IOException {
    if (in == null) {
      return null;
    }
    StringBuffer out = new StringBuffer();
    byte[] b = new byte[4096];
    for (int n; (n = in.read(b)) != -1;) {
      out.append(new String(b, 0, n));
    }
    return out.toString();
  }
  /**
   * 
   * @param double ganzahl
   * @return String ganzahl
   * @throws IOException
   */
  public static String formatDoubleGanzahlToStrGanzzahl(Double ganzahl) {
    String result = "0";
    if (ganzahl != null) {
      DecimalFormat df = new DecimalFormat("#0");
      result = df.format(ganzahl);
    }
    return result;
  }
  /**
   * 
   * @param  String  sWert-
   * @return String -sWert
   */
  public static String MinusVonHintenNachVorn(String sWert) {
    sWert = sWert.trim();
    String result = sWert;
    int pos;
    pos = sWert.indexOf("-");
    int iLetztePos = sWert.length() - 1;
    if (pos == iLetztePos) {
      sWert = sWert.substring(0, iLetztePos);
      result = "-" + sWert;
    }
    return result;
  }
  /**
   * 
   * @param  String  zeile
   * @param  String  seperator
   * @param  String  def //Default was ausgegeben werden soll wenn nichts drin steht.
   * @return String  gesuchter Wert aus CSV-Zeile
   */
  public static String GetWertAusCSV_Format(String zeile, char seperator, String def, int pos) {
    // TODO Auto-generated method stub
    String result = def;
    ArrayList<String> aL = HoraString.StringEmptyTokenizer(zeile, seperator);
    for (int i = 0; i < aL.size(); i++) {
      if (i != pos)
        continue;
      result = aL.get(i);
      break;
    }
    return result;
  }
  /**
   * 
   * @param  String  zeile
   * @param  String  seperator
   * @param  String  wert // wert nach dem gesucht wird
   * @return boolean  ist gesuchter Wert aus CSV-Zeile
   */
  public static boolean IsWertImCSV_Format(String zeile, char seperator, String wert) {
    // TODO Auto-generated method stub
    boolean result = false;
    ArrayList<String> aL = HoraString.StringEmptyTokenizer(zeile, seperator);
    for (int i = 0; i < aL.size(); i++) {
      result = wert.equals(aL.get(i)) ? true : false;
      if (result)
        break;
    }
    return result;
  }
}

/*
 * Created on 30.03.2005
 *
 * Datei: HoraTime.java
 * 
 * Letzte Änderung von $Author: schleese $
 * $Date: 2005/07/28 11:04:26 $
 * 
 * $Revision: 1.8 $
 * Copyright: Horatio GmbH, Berlin 2005
 */
package de.horatio.common;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO Doku
 * bei Verwendung von SimpleDateFormat bei der Initialisierung
 * das Wunschland mit angeben (LVMA: Länderformate)
 * 
 * @author A- Lüth (aus uniProcs)
 */
public class HoraTime {

  /**
   * 1sec liefert long-Wert 1000
   */
  public static long C1SEKUNDE        = 1000;
  /**
   * 1min liefert long-Wert 60*1000
   */
  public static long C1MINUTE         = 60 * C1SEKUNDE;
  /**
   * 1h liefert long-Wert 60*60*1000
   */
  public static long C1STUNDE         = 60 * C1MINUTE;
  /**
   * 1d liefert long-Wert 24*60*60*1000
   */
  public static long C1TAG            = 24 * C1STUNDE;
  /**
   * liefert long-Wert 30.12.1899  bis 01.01.1970 !!!!
   */
  public static long DelphiJavaNULL   = 25569;
  /**
   * vereinbart als leere Datumseingabe für JahreskappungStichtag JahreskappungStichtag2
   */
  public static long DelphiDATUM_LEER = -9999;

  /**
   * @param date
   * @return
   */
  public static String dateTimeFormatTempras(Date date) {
    SimpleDateFormat fmt = new SimpleDateFormat();
    fmt.applyPattern("yyMMddHHmm");
    fmt.setTimeZone(TimeZone.getTimeZone(getUsersTimeZone()));
    return fmt.format(date);
  }
  /**
  *
  * 
  * @param date Date
  * @param pattern String
  * @return String
  * @since
  */
  public static String dateOnlyToStr(Date date, String pattern) {
    //Arndt 08.02.2013
    SimpleDateFormat fmt = new SimpleDateFormat();
    fmt.applyPattern(pattern);
    fmt.setTimeZone(TimeZone.getTimeZone(getUsersTimeZone()));
    return fmt.format(date);
  }
  /**
   *
   * 
   * @param date Date
   * @return String
   * @since
   */
  public static String dateOnlyToStr(Date date) {
    SimpleDateFormat fmt = new SimpleDateFormat();
    fmt.applyPattern("dd.MM.yyyy");
    fmt.setTimeZone(TimeZone.getTimeZone(getUsersTimeZone()));
    return fmt.format(date);
  }
  /**
   * Diese Funktion wandelt ein Date-Object in einen long-Wert.
   * 
   * @param date Zeit in Date-Format
   * @return lMillis long
   * @since
   */
  public static long DateToMillis(Date date) {
    GregorianCalendar cal = new GregorianCalendar();
    cal.setTime(date);
    String sTag = "" + cal.get(Calendar.DATE);
    String sMonat = "" + (cal.get(Calendar.MONTH) + 1);
    String sJahr = "" + cal.get(Calendar.YEAR);
    Date datum = strToDate(sTag + "." + sMonat + "." + sJahr);
    long lMillis = datum.getTime();
    return lMillis;
  }
  /**
   * Diese Funktion wandelt ein Date-Object in einen String
   * der Form 01.01.2005 12:00:00
   * 
   * @param date Zeit in Date-Format
   * @return String formatiertes Date-Object als String
   * @since
   */
  public static String dateToStr(Date date) {
    SimpleDateFormat fmt = new SimpleDateFormat();
    fmt.applyPattern("dd.MM.yyyy HH:mm:ss");
    fmt.setTimeZone(TimeZone.getTimeZone(getUsersTimeZone()));
    return fmt.format(date);
  }
  /**
   *  
   * 
   * @param lLang long
   * @param lKurz long
   * @return lKurz long
   * @since
   */
  public static long DatumAngleichen(long lLang, long lKurz) {
    GregorianCalendar cal = new GregorianCalendar();
    cal.setTime(milliToDate(lLang));
    String sTag = "" + cal.get(Calendar.DATE);
    String sMonat = "" + (cal.get(Calendar.MONTH) + 1);
    String sJahr = "" + cal.get(Calendar.YEAR);
    Date datum = strToDate(sTag + "." + sMonat + "." + sJahr);
    long lDifferenz = datum.getTime();
    lKurz += lDifferenz;
    return lKurz;
  }
  /**
   * 
   * 
   * @param zeitpunkt Zeit in Date-Format
   * @return date
   * @since
   */
  public static Date DatumNullUhr(Date zeitpunkt) {
    if (zeitpunkt == null)
      return null;
    GregorianCalendar cal = new GregorianCalendar();
    cal.setTime(zeitpunkt);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return cal.getTime();
  }
  /**
   * Diese Funktion liefert die Zahl des Wochentages eines bestimmten Datums.
   * 0..6 für Mo..So
   * 
   * @param datum Date-Object
   * @return i int, Wochentagsnummer
   * @since
   */
  public static int dayOfWeek(Date datum) {
    int i = 0;
    GregorianCalendar cal = new GregorianCalendar();
    cal.setTime(datum);
    i = cal.get(Calendar.DAY_OF_WEEK);
    // 1-7 So...Sa wird umgebaut zu:
    i = i - 2; // -1...5 wie ein Array zählen
    // nun fängt unsere woche aber Montags an.
    if (i == -1) {
      i = 6;
    }
    return i;
  }
  /**
   * Diese Funktion liefert das heutige Datum in der Form 01.01.2005
   *  
   * @return sDatum Datum-String
   * @since
   */
  public static String DatumStempel() {
    String sDatum = "";
    SimpleDateFormat fmt = new SimpleDateFormat();
    // fmt.applyPattern("dd.MM.yyyy HH.mm.ss");
    fmt.applyPattern("dd.MM.yyyy");
    fmt.setTimeZone(TimeZone.getTimeZone(getUsersTimeZone()));
    sDatum = fmt.format(new java.util.Date());
    return sDatum;
  }
  /**
   * Diese Funktion wandelt den Uhrzeiteintrag der Datenbank in HH:MM.
   * Kann auch 24:00 und grösser sein!!
   * Beispiel:
   * 0.5  --> 12:00
   * 0.75 --> 18:00
   * 
   * @param zeit als double-Wert
   * @return vz Zeit als String
   * @since
   */
  public static String doubleToHHMM(Double zeit) {
    if (zeit == null)
      return "00:00";
    String ret;
    if (zeit < 0) {
      ret = "-";
      zeit = -zeit;
    } else {
      ret = "";
    }
    int x = (int) Math.round(zeit * 1440); // 1440 = 24 * 60
    int hour = x / 60;
    int min = x % 60;
    if (hour < 10)
      ret += "0";
    ret += hour + ":";
    if (min < 10)
      ret += "0";
    ret += min;
    return ret;
  }
  /**
   * Diese Funktion wandelt den Uhrzeiteintrag der Datenbank in HH:MM.
   * Kann auch 24:00 und grösser sein!!
   * Beispiel:
   * 0.25 -->  6:00
   * 0.5  --> 12:00
   * 0.75 --> 18:00
   * 
   * @param zeit als double-Wert
   * @return vz Zeit als String
   * @since
   */
  public static String doubleToHMM(Double zeit) {
    if (zeit == null)
      return "00:00";
    String ret;
    if (zeit < 0) {
      ret = "-";
      zeit = -zeit;
    } else {
      ret = "";
    }
    int x = (int) Math.round(zeit * 1440); // 1440 = 24 * 60
    int hour = x / 60;
    int min = x % 60;
    //if (hour < 10)
    // ret += "0";
    ret += hour + ":";
    if (min < 10)
      ret += "0";
    ret += min;
    return ret;
  }
  /**
   * Diese Funktion wandelt den Uhrzeiteintrag der Datenbank in HH,MM.
   * Beispiel:
   * 0.5  --> 12,30
   * 
   * @param zeit als double-Wert
   * @return Industrie-Zeit als String
   * @since
   */
  public static String doubleToIndustrieZeit(Double zeit) {
    //Arndt 18.02.2014
    if (zeit == null)
      return "0,0";
    String ret;
    if (zeit < 0) {
      ret = "-";
      zeit = -zeit;
    } else {
      ret = "";
    }
    int x = (int) Math.round(zeit * 1440); // 1440 = 24 * 60
    int hour = x / 60;
    int min = x % 60;
    float fmin = (float) x % 60 / 60;
    min = (int) Math.round(fmin * 100);
    if (hour < 10)
      ret += "0";
    ret += hour + ",";
    if (min < 10)
      ret += "0";
    ret += min;
    return ret;
  }
  public static String doubleToHHMMorIndustiezeitVonHisXML(Double wert, boolean hhmm) {
    DecimalFormat f = new DecimalFormat("#.00");
    if (hhmm) {
      return HoraTime.doubleToHMM(wert);
    } else {
      return f.format(wert);
    }
  }
  /**
   * Diese Funktion wandelt den Uhrzeiteintrag der Datenbank in HH:MM oder Dezimal.
   * Kann auch 24:00 und grösser sein!!
   * Beispiel:
   * 0.5  --> 12:00
   * 0.75 --> 18:00
   * 
   * @param zeit als double-Wert
   * @param hhmm als boolean-Wert
   * @return Uhrzeit aus double wird entweder zu HHMM oder Industriezeit gewandelt.
   * @since
   */
  public static String doubleToHHMMorIndustiezeit(Double zeit, boolean hhmm) {
    return hhmm ? doubleToHMM(zeit) : doubleToIndustrieZeit(zeit);
  }
  /**
   * Diese Funktion wandelt nur die Minuten des doubles der Datenbank in HH:MM oder Dezimal.
   * Dabei bleiben die Stunden so vorhanden wie sie uebergeben wurden.
   * Kann auch 24:00 und grösser sein!!
   * Beispiel:
   * 12.0  --> 12:00
   * 18.75 --> 18:45
   * 
   * @param zeit als double-Wert
   * @param hhmm als boolean-Wert
   * @return Uhrzeit aus double wird entweder zu HHMM oder Industriezeit gewandelt.
   * @since
   */
  public static String doubleTimeToHHMMorIndustiezeit(Double zeit, boolean hhmm) {
    return hhmm ? doubleToHMM(zeit / 24) : doubleToIndustrieZeit(zeit / 24);
  }
  /**

   * Convert string in ##,## format to float value
   * Returns null on convert error
   * 
   * @param wert Inputstring (##:##)
   * @return zeitwert in float 
   */
  public static float IndustriezeitToDouble(String wert) {
    int faktor = 1; //Arndt 12.10.2012
    float result = 0;
    try {
      int i = wert.indexOf("-"); //Arndt 23.07.2012 - erst mal ausschneiden am Ende wieder vorne ran.
      if (i > -1) {
        wert = wert.replace("-", "");
        faktor = -1;
      }
      wert = wert.replace(",", ".");
      result = Float.valueOf(wert.trim()).floatValue() / 24;
      return result;
    } catch (Exception e) {
      return 0;
    }
  }
  /**

   * Convert string in ##:## format to float value
   * Returns null on convert error
   * 
   * @param text Inputstring (##:##)
   * @return Outputvalue (0-1)
   */
  public static Float HHMMToDouble(String text) {
    int faktor = 1; //Arndt 12.10.2012
    int i = text.indexOf("-"); //Arndt 23.07.2012 - erst mal ausschneiden am Ende wieder vorne ran.
    if (i > -1) {
      text = text.replace("-", "");
      faktor = -1;
    }
    Pattern p = Pattern.compile("(\\d+):(\\d+)");
    Matcher m = p.matcher(text);
    if (m.matches()) {
      int hour = Integer.parseInt(m.group(1));
      int min = Integer.parseInt(m.group(2));
      if (hour >= 0 && min >= 0 && min <= 59 || hour == 24 && min == 0) {
        double val = hour * 60 + min;
        float fWert = Float.valueOf((float) (val / 1440.0)); // 1440 = 24 * 60 
        return fWert * faktor; //Arndt 23.07.2012 aus dem positiven Wert wieder einen negativen Wert machen.
      }
    }
    return null;
  }
  /**
   * Diese Funktion liefert den Differenzbetrag zweier long-Werte.
   * 
   * @param lWert1 long
   * @param lWert2 long
   * @return result long Differenz
   * @since
   */
  public static long getDiffBetrag(long lWert1, long lWert2) {
    if ((lWert1 - lWert2) > 0) {
      return (lWert1 - lWert2);
    } else {
      return ((-1) * (lWert1 - lWert2));
    }
  }
  /**
   * Diese Funktion liefert die Millisekunden eines double-Wertes.
   * 
   * @param d Zeit als double-Wert
   * @return result Zeit als long-Wert
   * @since
   */
  public static long getMillies(double d) {
    long result = 0;
    result = (long) (d * C1TAG);
    return (((result + 500) / 1000) * 1000);
  }
  /**
   * Diese Funktion liefert die eingestellte Zeitzone des Rechners.
   * 
   * @return result Zeitzone als String
   * @since
   */
  public static String getUsersTimeZone() {
    // nötig bei Linux zugriff auf die eigene Zeitzone
    String result = "ETC";
    result = System.getProperty("user.timezone");
    return result;
  }
  /**
   * Diese Funktion liefert die Millisekunden seit 01.01.1970 bis heute 00:00.
   * 
   * @return d long
   * @see 
   * HoraTime#ZeitKorrektur()
   * @since
   */
  public static long Heute() {
    long d;
    d = System.currentTimeMillis() - ZeitKorrektur();
    d = d / (24 * 3600 * 1000);
    d = d * (24 * 3600 * 1000);
    d = d + ZeitKorrektur();
    return d;
  }
  /**
   * Test, ob ein String Zeit-Datum enthält "19.08.2003 15:06:41"
   * besser String in Calendar einsetzen und bei Fehler exception auswerten !!
   * 
   * @param sTest
   * @return true / false
   */
  public static boolean isDateTime(String sTest) {
    StringBuffer sBuff = new StringBuffer(sTest);
    for (int i = 0; i < sBuff.length(); i++) {
      if (((sBuff.charAt(i) < '0') || (sBuff.charAt(i) > '9')) && (sBuff.charAt(i) != '.') && (sBuff.charAt(i) != ':')
          && (sBuff.charAt(i) != ' ')) {
        return (false); // kein break erforderlich
      }
    }
    return (true);
  }
  /**
   * Diese Funktion prüft ein Datum auf Wochenende.
   * Ist das Datum ein SA oder SO, wird true zurückgegeben.
   * 
   * @param datum Zeit als Date-Object
   * @return boolean true / false
   * @since
   */
  public static boolean isWeekEnd(Date datum) {
    switch (dayOfWeek(datum)) {
      case 5 :
      case 6 :
        return true;
      default :
        break;
    }
    return false;
  }
  /**
   * Diese Funktion macht aus einer long-Object ein Date-Object.
   * 
   * @param l long
   * @return d Date
   * @since
   */
  public static Date milliToDate(long l) {
    Date d = new Date();
    d.setTime(l);
    return d;
  }
  /**
   * 
   * 
   * @param l Zeit in Millisekunden
   * @return String
   * @since
   */
  public static String milliToDatumOnlyStr(long l) {
    Date d = milliToDate(l);
    return dateOnlyToStr(d);
  }
  /**
   * 
   * 
   * @param l Zeit in Millisekunden
   * @return String
   * @since
   */
  public static String milliToDatumStr(long l) {
    Date d = milliToDate(l);
    return dateToStr(d);
  }
  /**
   * Diese Funktion macht aus einem long-Wert einen String der Form
   * hh:mm
   * 
   * @param l long 
   * @return result String
   * @since
   */
  public static String milliToHHHMM(long l) {
    String result = "";
    long s = (((l + 500) / 1000) * 1000);
    long h = s / C1STUNDE;
    s = s - h * C1STUNDE;
    result = result + HoraString.fVornullen("" + h, 2) + ":";
    long m = s / C1MINUTE;
    s = s - m * C1MINUTE;
    result = result + HoraString.fVornullen("" + m, 2);
    if (result.equals("00:00"))
      result = "     ";
    return result;
  }
  /**
   * 20:15". Das Leezeichen störte.
   * 
   * 
   * @param l
   * @return
   */
  public static String milliToHHHMMEx(long l) {
    String result = "";
    if (l < 0) {
      result = "-";
      l = -l;
    }
    long s = (((l + 500) / 1000) * 1000);
    s = s + 30 * C1SEKUNDE; // dü
    long h = s / C1STUNDE;
    s = s - h * C1STUNDE;
    // dü result = result + fVornullen("" + h, 2) + ":";
    result = result + h + ":";
    long m = s / C1MINUTE;
    s = s - m * C1MINUTE;
    result = result + HoraString.fVornullen("" + m, 2);
    // Bieber 12.11.07 if (result.equals("00:00")) result = " ";
    if (result.equals("-0:00")) {
      result = "00:00";
    }
    return result;
  }
  /**
   *  
   * @param l long
   * @return result String
   * @since
   */
  public static String milliToZeitDauer(long l) {
    // Tage Stunden Minuten Sekunden
    // 3 Tage 21h 30min 50s
    String result = "";
    long s = l;
    long d = (l / C1TAG);
    s = s - d * C1TAG;
    if (d > 0) {
      result = result + d + " Tage";
    }
    long h = s / C1STUNDE;
    s = s - h * C1STUNDE;
    result = result + " " + HoraString.fVornullen("" + h, 2) + " Stunden";
    long m = s / C1MINUTE;
    s = s - m * C1MINUTE;
    result = result + " " + HoraString.fVornullen("" + m, 2) + " Minuten";
    s = s / 1000;
    result = result + " " + HoraString.fVornullen("" + s, 2) + " Sekunden";
    return result;
  }
  /**
   * 
   * 
   * @param datumUrzeit long mit Sekunden
   * @return result Datum Uhrzeit als long ohne Sekunden
   * @throws Exception
   * @since
   */
  public static long datetimeLongZuLongOhneSekunden(long datumUrzeit) {
    //Arndt 30.09.2013
    long d = datumUrzeit % 60000;
    return datumUrzeit - d;
  }
  /**
   * 
   * 
   * @param sH Datum als String
   * @return result Datum als Date
   * @throws Exception
   * @since
   */
  public static Date strToDate(String sH) {
    Date result = null;
    if (sH == null)
      return result;
    try {
      SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
      result = format.parse(sH);
    } catch (Exception e) {
      //e.printStackTrace();
      // HoraString.ZeigeStackTrace("exeption strToDateTime : "+sH+"\n"+e);
      System.out.println(e);
      return null;
    }
    return result;
  }
  /**
   * 
   * 
   * @param sH Datum als String
   * @return result Datum HHmm als Date
   * @throws Exception
   * @since
   */
  public static Date strToDateTime(String sH) {
    Date result = null;
    if (sH == null)
      return result;
    try {
      SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
      result = format.parse(sH);
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
    return result;
  }
  /**
   * Nur für Datum mit Uhrzeit mit Sekunden verwenden!!
   * 
   * @param sH String
   * @return result Zeit als Date-Object
   * @throws Exception
   * @since
   */
  public static Date strToDateTimeBuchungsSatz(String sH) throws Exception {
    Date result = null;
    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    result = format.parse(sH);
    return result;
  }
  /**
   * Nur für Datum mit Uhrzeit(englisch) ohne Sekunden verwenden!!
   * 
   * @param sH String
   * @return result Zeit als Date-Object
   * @throws Exception
   * @since
   */
  public static Date strToDateTimeBuchung(String sH) throws Exception {
    Date result = null;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    result = format.parse(sH);
    return result;
  }
  /*public static Date strToDateTimeBuchungsSatz(String sH) throws Exception{
   Date result = null;
   try {
   SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
   result = format.parse(sH);
   } catch (Exception e) {
   e.printStackTrace();
   //Monitor.printLn("strToDateTime: " + e.toString());
   HoraString.ZeigeStackTrace("strToDateTimeBuchungsSatz: ");
   }
   return result;
   }*/
  /**
   * 
   * 
   * @param sUhrZeit Zeit als String-Object
   * @return z Zeit als long
   * @since
   */
  public static long StrToTime(String sUhrZeit) {
    /*
     * ein gültige Uhrzeit hat mindest 1, max 2 Doppelpunkte Rest Zahlen und
     * es darf nicht mehr als 24h raus kommne 15:17:23 Zerlegen in
     * Doppelpunktzeilen und jeweils analysieren
     */
    ArrayList<String> aL = HoraString.StringEmptyTokenizer(sUhrZeit, ':');
    long z = 0;
    String sH = "";
    for (int i = 0; i < aL.size(); i++) {
      switch (i) {
        case 0 :
          // Stunden
          sH = aL.get(i);
          z = z + 60 * 60 * Integer.parseInt(sH);
          break;
        case 1 :
          // Minuten
          sH = aL.get(i);
          z = z + 60 * Integer.parseInt(sH);
          break;
        case 2 :
          // Sekunden
          sH = aL.get(i);
          z = z + Integer.parseInt(sH);
          break;
        default :
          break;
      }
    }
    z = 1000 * z;
    return z;
  }
  /**
   * 
   * 
   * @param date Zeit als Date-Object
   * @return result String
   * @since
   */
  public static String timeOnlyToStr(Date date) {
    SimpleDateFormat fmt = new SimpleDateFormat();
    fmt.applyPattern("HH:mm");
    fmt.setTimeZone(TimeZone.getTimeZone(getUsersTimeZone()));
    String result = fmt.format(date);
    if (result.equals("00:00"))
      result = "     ";
    return result;
  }
  /**
   * 
   * 
   * @param date Zeit als Date-Object
   * @return result String
   * @since
   */
  public static String timeOnlyToStrEx(Date date) {
    SimpleDateFormat fmt = new SimpleDateFormat();
    fmt.applyPattern("HH:mm");
    fmt.setTimeZone(TimeZone.getTimeZone(getUsersTimeZone()));
    String result = fmt.format(date);
    return result;
  }
  /**
   * Diese Funktion korrigiert die Millisekunden.
   * 
   * @return long
   * @since
   */
  public static long ZeitKorrektur() {
    // 
    long offset = new GregorianCalendar().get(Calendar.ZONE_OFFSET);
    long dest = new GregorianCalendar().get(Calendar.DST_OFFSET);
    return -(offset + dest);
  }
  /**
   * Diese Funktion gibt true zurück, wenn der
   * Zeitraum a..b vollständig im Zeitraum von..bis ist
   * 
   * @param a long
   * @param b long
   * @param von long
   * @param bis long
   * @return result boolean
   * @since
   */
  public static boolean zeitraumInZeitraum(long a, long b, long von, long bis) {
    boolean result = false;
    if (a >= von) {
      if (a <= bis) {
        if (b >= von) {
          if (b <= bis) {
            result = true;
          }
        }
      }
    }
    return result;
  }
  /**
   * Diese Funktion liefert einen Zeitstempel als String-Object
   * der Form 12:00:00 
   * 
   * @return sStempel String
   * @since
   */
  public static String ZeitStempel() {
    String sStempel = "";
    SimpleDateFormat fmt = new SimpleDateFormat();
    // fmt.applyPattern("dd.MM.yyyy HH.mm.ss");
    fmt.applyPattern("HH:mm:ss");
    fmt.setTimeZone(TimeZone.getTimeZone(getUsersTimeZone()));
    sStempel = fmt.format(new java.util.Date());
    return sStempel;
  }
  /**
   * Diese Funktion 
   * 
   * @param sSaldoZeit
   * @return
   * @since iHeutePLUSzeit
   */
  public static long ZeitToMillis(String sSaldoZeit) {
    // Gibt einen beliebigen Zeitwert (PC-Datum)
    // in Millisekunden (ab dem 01.01.1970) zurück
    long iHeutePLUSzeit = 0;
    String sZeit1h = HoraString.StringEmptyTokenizer(sSaldoZeit, ':').get(0);
    String sZeit1m = HoraString.StringEmptyTokenizer(sSaldoZeit, ':').get(1);
    int iZeit1h = Integer.parseInt((sZeit1h));
    int iZeit1m = Integer.parseInt((sZeit1m));
    int iZeit1MS = (iZeit1h * 60 * 60 * 1000) + (iZeit1m * 60 * 1000);
    long heute = HoraTime.Heute();
    // System.out.println("heute " + heute);
    iHeutePLUSzeit = (int) (heute + iZeit1MS);
    return iHeutePLUSzeit;
  }
  /**
   * TODO Doku
   * @param datum
   * @return sDatum
   */
  public static String getDatumDeutsch(Date datum) {
    String sDatum = "";
    DateFormat df;
    //0:  Freitag, 15. April 2005
    //1:  15. April 2005
    //2:  15.04.2005
    //3:  15.04.05
    df = DateFormat.getDateInstance(1, Locale.GERMAN);
    sDatum = df.format(datum);
    return sDatum;
  }
  /**
   * 
   * @param cal
   * @param fwdOrBwd
   * @return
   */
  public static Date getNextOrLastDatum(GregorianCalendar cal, boolean fwdOrBwd) {
    Date result = null;
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    if (cal.get(Calendar.DAY_OF_MONTH) == cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {//letzter Tag im Monat
      cal.set(Calendar.DAY_OF_MONTH, 1);
      int i = cal.get(Calendar.MONTH);
      cal.set(Calendar.MONTH, i + 1);
      result = cal.getTime();
      return result;
    } else {//Tag einen vor
      int i = cal.get(Calendar.DAY_OF_MONTH);
      cal.set(Calendar.DAY_OF_MONTH, i + 1);
      result = cal.getTime();
      return result;
    }
  }
  /**
   * Nur das Datum zurückgeben dd.mm.yyyy
   * @param date
   * @return
   */
  public static String dateToStrDateOnly(Date date) {
    SimpleDateFormat fmt = new SimpleDateFormat();
    fmt.applyPattern("dd.MM.yyyy");
    fmt.setTimeZone(TimeZone.getTimeZone(getUsersTimeZone()));
    return fmt.format(date);
  }
  /**
   * 
   * @param date
   * @return
   */
  public static String timeSecoundOnlyToStr(Date date) {
    SimpleDateFormat fmt = new SimpleDateFormat();
    fmt.applyPattern("HH:mm:ss");
    fmt.setTimeZone(TimeZone.getTimeZone(getUsersTimeZone()));
    String result = fmt.format(date);
    if (result.equals("00:00:00"))
      result = "     ";
    return result;
  }
  /**
   * @return aktuelles Jahr von heute
   */
  public static int getAktuellesJahr() {
    GregorianCalendar cal = new GregorianCalendar();
    cal.setTime(new Date());
    return cal.get(Calendar.YEAR);
  }
  /**
   * @return aktuelle Monat von heute
   */
  public static int getAktuellerMonat() {
    GregorianCalendar cal = new GregorianCalendar();
    cal.setTime(new Date());
    return cal.get(Calendar.MONTH) + 1;
  }
  /**
   * @param monat 1 Januar 2 Februar usw.
   * @param jahr
   * @return
   */
  public static int getMonatsTage(int monat, int jahr) {
    GregorianCalendar cal = new GregorianCalendar(jahr, monat - 1, 1);
    return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
  }
  /**
   * @param jahr
   * @return
   */
  public static boolean isSchaltjahr(int jahr) {
    boolean result = false;
    result = jahr % 4 == 0 && (jahr % 100 != 0 || jahr % 400 == 0);
    return result;
  }
  /**
   * 
   * @return
   */
  public static long now() {
    return System.currentTimeMillis();
  }
  private static long getSecureTime(long now) {
    return Long.reverse(now);
  }
  @SuppressWarnings("unused")
  private static String getSecureTimeStr(long now) {
    return "" + getSecureTime(now);
  }
  /**
   * für Link-Absicherung 
   * versteckter Zeitstempel
   *  
   * @param now
   * @return
   */
  public static String getSecureTimeStr() {
    long now = System.currentTimeMillis();
    return "" + getSecureTime(now);
  }
  /**
   * für Link-Absicherung 
   * versteckten Zeitstempel mit Systemzeit vergleichen
   * 10 Minuten delta wird toleriert

   * @param param
   * @return
   */
  public static boolean isSecure(String param) {
    long i = Long.parseLong(param);
    i = getSecureTime(i);
    long delta = System.currentTimeMillis() - i;
    delta = Math.abs(delta);
    System.out.println("delta=" + delta);
    // 10 minuten  
    return delta < C1MINUTE * 10;
  }
  /**
   * @param args
   */
  public static void main(String[] args) {
    String s;
    long now = System.currentTimeMillis() - C1MINUTE * 9;
    System.out.println(now);
    //s = getSecureTimeStr(now);
    s = getSecureTimeStr();
    System.out.println(s);
    System.out.println(isSecure(s));
  }
  /**
   * ueberprueft ob beim Vergleich 2er Zeitraeume Ueberlappungen vorkommen
   * @param persvon 
   * @param persbis 
   * @param von 
   * @param bis 
   * 
   * @return boolean
   */
  public static boolean ueberlappung(Date persvon, Date persbis, Date von, Date bis) {
    boolean hatUeberlappung = false;
    if (persvon.getTime() <= von.getTime() && persbis.getTime() >= bis.getTime()) {
      hatUeberlappung = true;
    } else
      if (persvon.getTime() >= von.getTime() && persvon.getTime() <= bis.getTime()) {
        hatUeberlappung = true;
      } else
        if (persbis.getTime() >= von.getTime() && persbis.getTime() <= bis.getTime()) {
          hatUeberlappung = true;
        }
    return hatUeberlappung;
  }
  //  public static void main(String[] args) {
  //    Date date = new Date();
  //    date = HoraTime.strToDate("29.2.2012");
  //    System.out.println(date + "       " + tomorrowLastYear(date));
  //    date = HoraTime.strToDate("28.2.2013");
  //    System.out.println(date + "       " + tomorrowLastYear(date));
  //    date = HoraTime.strToDate("29.2.2013");
  //    System.out.println(date + "       " + tomorrowLastYear(date));
  //    date = HoraTime.strToDate("31.12.2014");
  //    System.out.println(date + "       " + tomorrowLastYear(date));
  //    date = HoraTime.strToDate("29.10.2011");
  //    System.out.println(date + "       " + tomorrowLastYear(date));
  //    date = HoraTime.strToDate("30.10.2011");
  //    System.out.println(date + "       " + tomorrowLastYear(date));
  //    date = HoraTime.strToDate("31.10.2011");
  //    System.out.println(date + "       " + tomorrowLastYear(date));
  //  }
  /**
   * @param date
   * @return
   * 
   * morgen vor einem Jahr 
   * Dümchen 2011/10/25
   */
  public static Date tomorrowLastYear(Date date) {
    Calendar cal = new GregorianCalendar();
    cal.setTime(date);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    cal.roll(Calendar.DAY_OF_YEAR, 1);
    cal.roll(Calendar.YEAR, -1);
    return cal.getTime();
  }
  /**
   * @param zahl
   * @return
   */
  public static Float UhrzeitInFloat(String zahl) {
    try {
      String[] werte = zahl.split(":");
      float stunden;
      try {
        stunden = Float.parseFloat(werte[0]);
      } catch (Exception e) {
        stunden = Float.parseFloat("0.0");
      }
      float minuten;
      try {
        minuten = Float.parseFloat(werte[1]);
      } catch (Exception e) {
        minuten = Float.parseFloat("0.0");
      }
      if (stunden < 0)
        minuten *= -1;
      return Float.valueOf((float) ((((stunden * 60.0) + minuten) / 60.0) / 24.0));
    } catch (Exception e) {
      return null;
    }
  }
  /**
   * Rechnet eine Zeit in eine andere Zeitzone um
   * 
   * @param datum
   * @param zeitZone
   * @return
   */
  public static Date convert(Date datum, TimeZone zeitZone) {
    if (datum == null)
      return null;
    if (zeitZone == null)
      return datum;
    Calendar t = new GregorianCalendar(zeitZone);
    t.setTimeInMillis(datum.getTime());
    return new GregorianCalendar(t.get(Calendar.YEAR), t.get(Calendar.MONTH), t.get(Calendar.DAY_OF_MONTH),
        t.get(Calendar.HOUR_OF_DAY), t.get(Calendar.MINUTE), 0).getTime();
  }
  /**
   * Rechnet eine Zeit in eine andere Zeitzone um
   * 
   * @param datum
   * @param zeitZone
   * @return
   */
  public static Date addDay(Date datum, int tageDazu) {
    Calendar cal = new GregorianCalendar();
    cal.setTime(datum);
    cal.add(Calendar.DAY_OF_MONTH, tageDazu);
    return cal.getTime();
  }
  public static String DezimalZeitToHHMM(String sSaldoGestern) {
    // TODO Auto-generated method stub
    if (sSaldoGestern.equals(""))
      return "";
    int j = sSaldoGestern.indexOf(',');
    String sh = sSaldoGestern.substring(0, j);
    String sh1 = sSaldoGestern.substring(j + 1, sSaldoGestern.length());
    int min = 0;
    if (sh1.length() == 1)
      min = Integer.valueOf(sh1) * 60 / 10;
    else
      min = Integer.valueOf(sh1) * 60 / 100;
    return sh + ":" + HoraString.fVornullen(String.valueOf(min), 2);
  }
  public static int getKalenderWoche(Date datum) {
    //Arndt 20.03.2015
    Calendar cal = new GregorianCalendar();
    cal.setTime(datum);
    return cal.get(Calendar.WEEK_OF_YEAR);
  }
  public static Date ersterDesMonats(Date datum) {
    Calendar cal = new GregorianCalendar();
    cal.setTime(datum);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    cal.set(Calendar.DAY_OF_MONTH, 1);
    return cal.getTime();
  }
  public static Date ersterDesMonatsIdx(Date datum, int idxMonate) {
    Calendar cal = new GregorianCalendar();
    cal.setTime(datum);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    cal.set(Calendar.DAY_OF_MONTH, 1);
    cal.add(Calendar.MONTH, idxMonate);
    return cal.getTime();
  }
  public static Date letzterDesVormonats(Date date) { // Arndt 07.11.2012
    Calendar cal = new GregorianCalendar();
    Date result = cal.getTime();
    cal.setTime(date);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    cal.set(Calendar.DAY_OF_MONTH, 1);
    //cal = rollJahresWechselReturn(cal);
    //FIXME Dümchen 14.06.2015 prüfen
    cal.add(Calendar.DAY_OF_YEAR, -1);
    return cal.getTime();
  }
  public static Date letzterDesMonats(Date date) { // Arndt 07.11.2012
    Calendar cal = new GregorianCalendar();
    cal.setTime(date);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    return cal.getTime();
  }
}

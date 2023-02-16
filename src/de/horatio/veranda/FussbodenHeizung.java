/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.horatio.veranda;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author duemchen
 */
class FussbodenHeizung extends Thread {

    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger();
    //
    private final int MINSOLL = 20;
    private final int MAXSOLL = 40;
    //
    private int SOLL = 30; //Sollwert
    private final int PERIODE_MS = 10000; // Gesamt
    private final int FAKTOR = 3;//10; // 1 grad x Prozent  1->10%
    private final double PFAKTOR = 1; // faktor mal der differenz zur lastTemp wird als Regelaufschaltung verwendet.
// faktor mal der differenz zur lastTemp wird als Regelaufschaltung verwendet.

    //
    //
    private final double HYSTERESE = 0.5;
    //
    // schieber nicht ändern.
    private final Veranda.TEMP tempVL; // Temp Vorlsuf
    private final Veranda.TEMP tempRL; // Temp Rücklauf
    private final Veranda.DA hotter; // Richtung heisser = mehr Durchfluss zum Speicher (sonst kälter)
    private final Veranda.DA impuls; // schieber ein stück rücken.
    private final Veranda.DA pumpe; // schieber ein stück rücken.
    //
    private double lastIst = SOLL; // für eine P-Regler die vorige Temp merken um den Anstieg zu haben.

    FussbodenHeizung(AtomicInteger sollwert, Veranda.TEMP tempVL, Veranda.TEMP tempRL, Veranda.DA hot, Veranda.DA impuls, Veranda.DA pumpe) {

        //
        this.tempVL = tempVL;
        this.tempRL = tempRL;
        this.hotter = hot;
        this.impuls = impuls;
        this.pumpe = pumpe;

    }

    // stellventil einige sekunden in die richtung. thread starten warten ende
    // endezeit berechnen und in eigenem thread ausschalten lassen.
    // thread läuft immer. wenn zeit in zukunft an, sonst aus.
    // 100% = 10sek  1% =1 sek  sleep(x*10s) sleep((1-x)*10s)  0 kein impuls
    @Override
    public void run() {
        while (true) {
            try {
                control();
                //System.out.println("...");
            } catch (InterruptedException ex) {
                log.error(ex);
                Thread.currentThread().interrupt(); // very important
                break;
            }

        }
        System.out.println("...stop.");

    }

    /**
     * einfacher IRegler Schieber in etappen öffnen und schliessen Schieber
     * öffnen wenn Wärme kommt. temperatur immer 10 grad höher als spTemp halten
     *
     * gleitend mit der erhöhten SPTemperatur mitziehen
     *
     * NOT: wenn solar > als Grenzwert Auf.
     *
     *
     * @throws InterruptedException
     */
    void control() throws InterruptedException {
        double ist = tempVL.getTempLast().floatValue();
        if (ist == 0) {
            log.info("warte auf gültigen Temperaturwert...");
            Thread.sleep(5000);
            return;
        }
        if (ist == 0) {   // wird nie durchlaufen
            log.info("wird nie durchlaufen! für Testsystem zu negieren");
            Thread.sleep(5000);
            return;
        }

        boolean doOeffnen;
        double prozent;
        boolean doImpuls;

        if (MqttVeranda.getInstance().getHeizungZustand()) {
            // Pumpe Strom sparen wenn wenig Wärme gebraucht wird
            if (MqttVeranda.getInstance().getPumpe()) {
                pumpe.on();
            } else {
                pumpe.off();
            }
        } else {
            pumpe.off();
        }
        // heizen über Aussentemp.gestrichen.
        if (MqttVeranda.getInstance().getHeizungZustand()) {
            //System.out.println("Heizen...");
            SOLL = getSolltemp();

            doOeffnen = (ist < SOLL);
            double abweich = (ist - SOLL);
            //P-Regler
            // Muss gegensteuern.Wenn also temp schon absinkt, muss es den Istwert noch weiter tiefer vortäuschen
            double anstieg = ist - lastIst;  // 29 - 28 = +1 temp steigt  um 1 grad, so tun als wären schon 29+1=30 grad.
            double pAnteil = PFAKTOR * anstieg;
            //System.out.println("Abweichung: " + new DecimalFormat("###.#").format(abweich) + "  Mit P-Anteil: " + new DecimalFormat("###.#").format(abweich + anstieg));
            abweich += pAnteil;
            lastIst = ist;

            //
            abweich = Math.abs(abweich);
            doImpuls = abweich > HYSTERESE;
            prozent = FAKTOR * abweich;
            prozent = Math.min(100, prozent);
            if (MqttVeranda.getInstance().getPumpe()) {
                log.info("Soll:" + SOLL + ", vl:" + tempVL.getTempLast().floatValue() + ", rl:" + tempRL.getTempLast().floatValue() + ", heisser:" + doOeffnen + ", Impuls %:" + new DecimalFormat("###.#").format(prozent));
            } else {
                // Regelung aussetzen wenn Pumpe aus.
                log.info("Soll:" + SOLL + ", vl:" + tempVL.getTempLast().floatValue() + ", rl:" + tempRL.getTempLast().floatValue() + ", heisser:" + doOeffnen + ", Impuls unterdrückt.");
                doImpuls = false;
            }
        } else {
            //System.out.println("Heizung aus.");
            //pumpe.off();
            // abschalten
            doOeffnen = false; //zudrehen!
            prozent = 100;
            doImpuls = true;
        }

        // Richtung einstellen
        if (doOeffnen) {
            hotter.on();
        } else {
            hotter.off();
        }

        // Impuls
        if (doImpuls) {
            impuls.on();
            // je nach Abweichung stärker ausregeln. Prozentual 100% heizen wenn wie weit weg?
            int hotter = (int) ((prozent / 100) * PERIODE_MS);
            //   System.out.println("sleepingHotter:" + hotter);
            Thread.sleep(hotter);
            if (prozent < 90) {
                impuls.off();
            } else {
                // System.out.println("Schieber Dauerbetrieb wegen hoher Abweichung, " + new DecimalFormat("###.#").format(prozent));
            }

        } else {
            // System.out.println("abweich klein:" + abweich + ", Impuls unterdrückt.");
        }
        int sleepingTime = (int) (((100 - prozent) / 100) * PERIODE_MS);
        // System.out.println("sleeping:" + sleepingTime);
        Thread.sleep(sleepingTime);

    }

    private int getSolltemp() {
        int result = MqttVeranda.getInstance().getSolltemp();
        result = Math.max(result, MINSOLL); //mindestens minSoll
        result = Math.min(result, MAXSOLL); //höchstens mxSoll
        MqttVeranda.getInstance().setSoll(SOLL);
        return result;
    }

}

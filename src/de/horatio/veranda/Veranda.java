/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.horatio.veranda;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import de.horatio.common.HoraFile;
import de.horatio.common.HoraIni;

import java.io.IOException;
import static java.lang.Thread.sleep;
import java.text.DecimalFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import se.hirt.w1.Sensor;
import se.hirt.w1.Sensors;

/**
 *
 * @author duemchen
 */
public class Veranda {

    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger();
    private static final GpioController gpio = GpioFactory.getInstance();
    //
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH");
    private static final DecimalFormat df = new DecimalFormat("0.00");
    final static String veranda_ini = "veranda.ini";
    private static MqttVeranda mqttTemperaturen;
    private FussbodenHeizung fusshzg;
    private final int TEMPERATUR_POLLING_SEK = 4; //(war 2)
    private AtomicInteger soll;

    private boolean aussentempToPumpe() {
        double temp = -11;
        try {
            // openweather.OpenWeather ow = new OpenWeather();
            double lon = 12.89;
            double lat = 53.09;
            //  ow.setCoord(lon, lat);
            //  temp = ow.getTemp();
        } catch (Exception e) {

        }
        log.debug("aussentemp: " + temp);
        /*
        boolean result = true;
        if (temp > AUSSENTEMPERATURGRENZE) {
            result = false;
            DA.PUMPE.off();

        } else {
            DA.PUMPE.on();
        }
         */
        
        return false;

    }

    /**
     * iii
     */
    public static enum DA {

        STELLHOT(RaspiPin.GPIO_00), //
        STELLIMP(RaspiPin.GPIO_01), //
        PUMPE(RaspiPin.GPIO_02), //
        A(RaspiPin.GPIO_03),//
        B(RaspiPin.GPIO_04),//
        C(RaspiPin.GPIO_05), //
        D(RaspiPin.GPIO_06), //
        LIVE(RaspiPin.GPIO_10),;

        private GpioPinDigitalOutput pin;

        private DA(Pin pin) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {

            }

            this.pin = gpio.provisionDigitalOutputPin(pin, "", PinState.HIGH);
        }

        public GpioPinDigitalOutput getPin() {
            return pin;
        }

        @Override
        public String toString() {
            String s = super.toString();
            return s.substring(0, 1) + s.substring(1).toLowerCase();
        }

        public void on() {
            pin.low();
        }

        public boolean isOff() {
            return pin.isHigh();

        }

        public boolean isOn() {
            return pin.isLow();
        }

        public void off() {
            pin.high();

        }

    }

    /**
     * jeder fühler wird aktiviert wenn er beim start gefunden wurde - alle IDs
     * sammeln in Hash, sortieren und speichern - laden *
     */
    public enum TEMP {

        VORLAUF,
        RUECKLAUF;
        private Sensor sensor = null;
        private Number lastTemp = 0.0;
        private Number middleTemp = 0.0;

        public void setSensor(Sensor sensor) {
            this.sensor = sensor;
        }

        public Number getTemp() {
            if (sensor == null) {
                return 0;
            }
            try {
                Number iTemp = sensor.getValue();
                //Nullunterdrückung
                if (iTemp != null) {
                    if (iTemp.longValue() >= -10) {
                        if (iTemp.longValue() < 100) {
                            Number middle = mittelWertGewichtet(middleTemp, iTemp, 3.0);
                            // der middleTemp ist immer nahe der ist
                            // wenn also mal ein Ausbrecher kommt, kann middleTemp in diese Richtung verschoben werden.
                            // es heilt sich dann sofort wieder
                            double MAXDELTA = 5.0;
                            double delta = iTemp.doubleValue() - middle.doubleValue();
                            if (Math.abs(delta) < MAXDELTA) {
                                lastTemp = iTemp;
                                middleTemp = middle;
                                log.debug(this.name() + "\tnewVal=" + lastTemp + ", middleTemp=" + df.format(middleTemp) + ", delta=" + df.format(delta));
                            } else {
                                if (delta >= MAXDELTA) {
                                    delta = MAXDELTA * 0.9;
                                } else {
                                    if (delta <= -MAXDELTA) {
                                        delta = -MAXDELTA * 0.9;
                                    }
                                }
                                middleTemp = middleTemp.doubleValue() + delta;
                                log.debug("Ausbrecher " + this.name() + "\tist=" + iTemp + ", middleTemp=" + df.format(middleTemp) + ", delta=" + df.format(delta));
                            }
                        }
                    }
                }
                return lastTemp;

            } catch (IOException ex) {
                log.error("getTemp()");
                log.error(ex);
            }

            return null;
        }

        public Number getTempLast() {
            return lastTemp;
        }

        @Override
        public String toString() {
            return "TemperaturSensor " + this.name() + ", sensor:" + sensor;
        }

        private Number mittelWertGewichtet(Number middle, Number neu, double wichtung) {
            try {
                double result = wichtung * middle.doubleValue() + neu.doubleValue();
                result = result / (wichtung + 1);
                return result;

            } catch (Exception e) {
                log.error("mittel");
                log.error(e);
            }
            return middle;
        }

    }

    private static boolean initTemperatureSensoren() {
        try {

            System.out.println("inifile: " + HoraFile.getCanonicalPath(veranda_ini));
            Set<se.hirt.w1.Sensor> sensors = Sensors.getSensors();
            System.out.println(String.format("Found %d sensors:", sensors.size()));
            // jeder Sensor wird mit ID in eine Propertydatei eingetragen.
            // dort erfolgt die Zuordnung zu dem konkreten Sensor
            // FERN ID = 28-000000e46c60
            for (Sensor sensor : sensors) {
                System.out.println(String.format("%s(%s):%3.2f%s",
                        sensor.getPhysicalQuantity(), sensor.getID(),
                        sensor.getValue(), sensor.getUnitString()));
                HoraIni.SchreibeIniString(veranda_ini, "SensorIDs", sensor.getID(), String.format("%3.2f", sensor.getValue()));

            }
            System.out.println(String.format("Zugeordnete Sensoren:", sensors.size()));
            for (TEMP temp : TEMP.values()) {
                String id = HoraIni.LeseIniString(veranda_ini, "Temperaturen", temp.name(), "", true);
                if ("".equals(id)) {
                    continue;
                }
                // diese ID in sensoren suchen und verschalten
                //System.out.println(id);
                for (Sensor sensor : sensors) {
                    //System.out.println(id + " " + sensor.getID());
                    if (!id.equalsIgnoreCase(sensor.getID())) {
                        continue;
                    }
                    temp.setSensor(sensor);
                    break;
                }
            }
            // alle angebunden?

            for (TEMP temp : TEMP.values()) {
                System.out.println(temp);
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public Veranda() throws InterruptedException, IOException {
        MqttVeranda.getInstance().sendHallo();

        while (!initTemperatureSensoren()) {
            sleep(2000);
            log.debug("repeat initTemperatureSensoren");
        }
        fusshzg = new FussbodenHeizung(soll, TEMP.VORLAUF, TEMP.RUECKLAUF, DA.STELLHOT, DA.STELLIMP, DA.PUMPE);
        // s2ww = new SpeicherToWarmwasser(TEMP.KONTROLLWERT, TEMP.WARMWASSER, DA.PUMPE);
        fusshzg.setName("FussbodenHeizung");
//        s2ww.setName("speicher2Warmwasser");
        fusshzg.start();
        //      s2ww.start();
        DA.PUMPE.on();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    fusshzg.interrupt();
                    fusshzg.join(1000);

                } catch (InterruptedException ex) {
                    log.error(ex);
                }

                for (DA da : DA.values()) {
                    da.off();
                }
                MqttVeranda.getInstance().sendByebye();
                System.out.println("addShutdownHook. End.");
                log.info("addShutdownHook. End.");

            }
        });
        int i = 0;
        int lastHour = -1;
        while (true) {

            Thread.sleep(TEMPERATUR_POLLING_SEK * 1000);
            //
            String s = sdf.format(new Date());
            Integer hour = Integer.parseInt(s);
            if (lastHour != hour.intValue()) {
                lastHour = hour.intValue();
                // pumpe abschalten wenn temperatur > 15 grad ist.
                // boolean heizen = aussentempToPumpe();
                //fusshzg.setRegler(heizen);

            }

//
            if (DA.LIVE.isOn()) {
                DA.LIVE.off();
            } else {
                DA.LIVE.on();
            }

            for (TEMP temp : TEMP.values()) {
                temp.getTemp();
            }

            // da testen
            /*
             if (i % 20 == 0) {
             for (DA da : DA.values()) {
             da.on();
             Thread.sleep(50);
             da.off();
             Thread.sleep(100);
             }
             }*/
            if (i % 2 == 0) {
                try {
                    //TODO minütliche info ans MQTT, nach minutenwechsel
                    MqttVeranda.getInstance().sendTemps();
                } catch (Exception ex) {
                    log.error(ex);
                }

            }
            i++;

        }

    }

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws IOException {

        log.info("Start Veranda.");
        log.info(HoraFile.getCanonicalPath("log4j.xml"));
        try {
            Veranda veranda = new Veranda();
        } catch (IOException | InterruptedException e) {
            log.error("main meldet:");
            log.error(e);
            log.error("Programmende.");
        }

        //while (true){
        //   log.info("Veranda... "+new Date());            
        // try {
        //      Thread.sleep(2000);
        // } catch (InterruptedException e) {
        //   break;
        //  }
        //}
    }

}

/*
 TODOS



 */

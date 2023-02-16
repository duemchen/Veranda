/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.horatio.veranda;

import de.horatio.common.HoraIni;
import de.horatio.common.HoraString;
import de.horatio.common.HoraTime;
import de.horatio.common.NetworkUtil;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.time.Clock;
import java.util.Date;
import java.util.logging.Level;
import org.apache.logging.log4j.core.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author duemchen
 */
class MqttVeranda implements MqttCallbackExtended {

    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger();
    private static MqttVeranda instance = null;
    private final MqttClient client;
    private String MQUSER;
    private static final String MQURI = HoraIni.LeseIniString("veranda.ini", "MQTT", "URI", "tcp://192.168.10.51:1883", true);
    private static final String MQROOT = HoraIni.LeseIniString("veranda.ini", "MQTT", "ROOT", "simago/xfussboden", true);

    //
    private boolean heizungzustand = "true".equalsIgnoreCase(HoraIni.LeseIniString("persist.ini", "Heizung", "Zustand", "false", true));
    private boolean setPumpe = "true".equalsIgnoreCase(HoraIni.LeseIniString("persist.ini", "Heizung", "Pumpe", "false", true));
    private int setSoll = HoraIni.LeseIniInt("persist.ini", "Heizung", "setSoll", 29, true);

    private int getSoll = 0;

    public static MqttVeranda getInstance() {
        if (instance == null) {
            try {
                instance = new MqttVeranda();
            } catch (MqttException ex) {
                log.error(ex);
            }
        }
        return instance;
    }

    public MqttVeranda() throws MqttException {
        //client = new MqttClient("tcp://duemchen.ddns.net:1883", "publishTempVeranda");

        MQUSER = NetworkUtil.getMyHostname("dummy");
        MQUSER += (int) (Math.random() * 999);
        log.info("MQURI: " + MQURI + ", MQUSER: " + MQUSER);
        client = new MqttClient(MQURI, MQUSER, new MemoryPersistence());
        client.connect();
        //
        sendSetSoll(setSoll);
        sendSetPumpe(setPumpe);
        //

    }

    public boolean getHeizungZustand() {
        return heizungzustand;
    }

    public void setSoll(int soll) {
        this.getSoll = soll;
    }

    void sendTemps() throws JSONException {
        //TODO Format json

        JSONObject jo = new JSONObject();
        JSONObject temperaturen = new JSONObject();
        JSONObject relais = new JSONObject();
        for (Veranda.TEMP temp : Veranda.TEMP.values()) {
            //content += temp.name() + "=" + temp.getTempLast() + ", ";
            temperaturen.put(temp.name(), temp.getTempLast());
        }

        for (Veranda.DA da : Veranda.DA.values()) {
            relais.put(da.name(), da.isOn());
        }
        jo.put("time", HoraTime.dateToStr(new Date()));
        jo.put("solltemp", getSoll);
        jo.put("temperatures", temperaturen);
        jo.put("relais", relais);

        MqttMessage message = new MqttMessage();
        Runtime rt = Runtime.getRuntime();

        String content = jo.toString();
        message.setPayload(content.getBytes());
        try {
            if (!client.isConnected()) {
                client.connect();
            }
            client.publish(MQROOT, message);
        } catch (MqttException ex) {
            log.error(ex);
        }
        try {
            Number d = Veranda.TEMP.VORLAUF.getTempLast();
            content = String.format("%.2f", d); //d.toString();
            message.setPayload(content.getBytes());

            if (!client.isConnected()) {
                client.connect();
            }
            client.publish(MQROOT + "/vorlauf", message);
        } catch (MqttException ex) {
            log.error(ex);
        }
        try {
            Number d = Veranda.TEMP.RUECKLAUF.getTempLast();
            content = String.format("%.2f", d);
            message.setPayload(content.getBytes());

            if (!client.isConnected()) {
                client.connect();
            }
            client.publish(MQROOT + "/ruecklauf", message);
        } catch (MqttException ex) {
            log.error(ex);
        }

    }

    void sendByebye() {
        //TODO Format json
        MqttMessage message = new MqttMessage();
        String content = "Ende FussbodenHeizungsregler.  Speicher:" + getMemory();
        message.setPayload(content.getBytes());
        try {
            if (!client.isConnected()) {
                client.connect();
            }
            client.publish(MQROOT, message);

        } catch (MqttException ex) {
            log.error(ex);
        }

    }

    void sendHallo() {
        MqttMessage message = new MqttMessage();
        String content = "Start FussbodenHeizungsregler. Speicher: " + getMemory();
        message.setPayload(content.getBytes());

        try {
            if (!client.isConnected()) {
                client.connect();
            }
            client.setCallback(this);
            client.publish(MQROOT, message);
        } catch (MqttException ex) {
            log.error(ex);
        }

    }

    private String getMemory() {
        Runtime rt = Runtime.getRuntime();
        return new DecimalFormat("###,###.###").format((rt.totalMemory() - rt.freeMemory()));

    }

    @Override
    public void connectionLost(Throwable thrwbl) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void messageArrived(String path, MqttMessage mm) throws Exception {
        log.debug("de.horatio.veranda.MqttVeranda.messageArrived()" + path + ",,," + mm);
        String s = mm.toString();
        if (path.toLowerCase().contains("zustand")) {
            heizungzustand = s.toLowerCase().contains("on");
            HoraIni.SchreibeIniString("persist.ini", "Heizung", "Zustand", "" + heizungzustand);
        }
        if (path.toLowerCase().contains("setpumpe")) {
            setPumpe = s.toLowerCase().contains("on");
            HoraIni.SchreibeIniString("persist.ini", "Heizung", "Pumpe", "" + setPumpe);
        }

        if (path.toLowerCase().contains("setsoll")) {
            setSoll = Integer.parseInt(s);
            HoraIni.SchreibeIniString("persist.ini", "Heizung", "setSoll", "" + setSoll);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void connectComplete(boolean bln, String string) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        System.out.println("MqttVeranda connectComplete. subscribe...");
        log.info("MqttVeranda connectComplete. subscribe...");
        try {
            client.subscribe(MQROOT + "/zustand", 1);//Test 16.1.23
            client.subscribe(MQROOT + "/setSoll", 1);
            client.subscribe(MQROOT + "/setPumpe", 1);
            log.info("MqttVeranda subscribe done ok.");
        } catch (MqttException ex) {
            log.error(ex);
        }
    }

    int getSolltemp() {
        return setSoll;
    }

    private void sendSetSoll(int setSoll) {
        MqttMessage message = new MqttMessage();
        String content = "" + setSoll;
        message.setPayload(content.getBytes());

        try {
            if (!client.isConnected()) {
                client.connect();
            }
            client.setCallback(this);
            client.publish(MQROOT + "/setSoll", message);
        } catch (MqttException ex) {
            log.error(ex);
        }

    }

    boolean getPumpe() {
        return setPumpe;
    }

    private void sendSetPumpe(boolean setPumpe) {
        MqttMessage message = new MqttMessage();
        String content = "" + (setPumpe ? "on" : "off");
        message.setPayload(content.getBytes());

        try {
            if (!client.isConnected()) {
                client.connect();
            }
            client.setCallback(this);
            client.publish(MQROOT + "/setPumpe", message);
        } catch (MqttException ex) {
            log.error(ex);
        }

    }

}

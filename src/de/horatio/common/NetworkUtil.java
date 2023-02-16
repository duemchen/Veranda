/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.horatio.common;
import java.net.InetAddress;

public class NetworkUtil {
    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger();

      public static String getMyHostname(String dummy) {        
        try {
            InetAddress address = InetAddress.getByName("google.com"); 
            //log.info(address.getLocalHost().getHostAddress());
            return address.getLocalHost().getHostName();
            
        } catch (Exception e) {
            log.error(e);
        }
        return dummy;
    }
    
    
        public static void main(String[] args) {
        NetworkUtil nu = new NetworkUtil();      
        System.out.println("IP: " + nu.getMyHostname("dummy"));
    }

} 
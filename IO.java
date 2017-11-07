package ENGG2800;

import gnu.io.*;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TooManyListenersException;
import java.util.*;
import java.sql.Date;

public class IO implements SerialPortEventListener{
    private Enumeration ports = null;
    private HashMap portMap = new HashMap();
    private InputStream input = null;
    private OutputStream output = null;
    private boolean bConnected = false;
    private CommPortIdentifier selectedPort = null;
    private SerialPort serialPort = null;
        
    public static GoogleMap gmap = new GoogleMap();
    
    public static ArrayList<Double> lat = new ArrayList<Double>();
    public static ArrayList<Double> lon = new ArrayList<Double>();
    public static ArrayList<Float> map_t = new ArrayList<>();
    public static ArrayList<Float> map_w = new ArrayList<>();
    public static ArrayList<Float> map_s = new ArrayList<>();
    public static ArrayList<String> map_time = new ArrayList<>();

    String logText = "";
    String port_use = new String();
    String temp = new String();
    GUI gu = main_app.ui;
    
    /*find the port
    parameter none
    return none*/
    public void searchForPorts() {
        int if_run = 1;
        ports = CommPortIdentifier.getPortIdentifiers();
        
        //begin to search available port
        while (if_run == 1) {
            if (!ports.hasMoreElements()) {
                if_run = 0;
                break;
            }
            
            CommPortIdentifier curPort = (CommPortIdentifier)ports.nextElement();
            
            //find one can be used
            if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL) {                
                port_use = curPort.getName();
                portMap.put(curPort.getName(), curPort);
                System.out.println("I find one port can be used: "+ curPort.getName());
            }
            
            
        }
    }
    
    /*try to connect with  a available port*/
    public void connect(){
        selectedPort = (CommPortIdentifier)portMap.get(port_use);
        CommPort commPort = null;
        
        //try to connect
        try {
            commPort = selectedPort.open("TigerControlPanel", 2000);
            serialPort = (SerialPort)commPort;
            this.bConnected = true;
        } catch (Exception e) {
            System.out.println("port is not available");
        } 
    }
    
    /*try to init port
    parameter none
    return boolean*/
    public boolean initIOStream()
    {
        boolean successful = false;

        try {
            input = serialPort.getInputStream();
            output = serialPort.getOutputStream();           
            successful = true;
            return successful;
        }
        catch (IOException e) {
            System.out.println("port is not available");
            return successful;
        }
    }
    
    
    /*init listener
    parameter none
    return none*/
    public void initListener() {
        try {
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (TooManyListenersException e)
        {
            System.out.println("initlistener failed");
        }
    }
    
    /*get status
    parameter none
    return boolean*/
    final public boolean getConnected() {
        return bConnected;
    }
    
    /*disconnect port*/
    public void disconnect() {
        //close the serial port
        try {
            serialPort.removeEventListener();
            serialPort.close();
            input.close();
            output.close();
            this.bConnected = false;

        }
        catch (Exception e)
        {
            System.out.println("failed to disconnect");

        }
    }
    
    /*update the GPS value from the file
    parameter arraylist<float>
    return none*/
    public void UpdateGPS (ArrayList<Float> GPS, String t) {
        map_time.add(t);
        map_w.add(GPS.get(1));
        map_t.add(GPS.get(2));
        map_s.add(GPS.get(3));
        lat.add((double)GPS.get(4));
        lon.add((double)GPS.get(5));        
    }
    
    /*clean the GPS value 
    parameter none
    return none*/
    public void CleanGPS () {
        map_time.clear();
        map_w.clear();
        map_t.clear();
        map_s.clear();
        lat.clear();
        lon.clear();
    }

    /*listen the port
    parameter port
    return none*/
    public void serialEvent(SerialPortEvent evt) {
        Scanner z;
        if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                byte singleData = (byte)input.read();

                if (singleData != 10) {
                    logText = new String(new byte[] {singleData});
                    //System.out.printf("%s",logText);
                    temp += logText;
                }
                else {
                    temp = temp.trim();
                    if (temp.charAt(0) == '!' || temp.charAt(1) == '!') {

                        z = new Scanner(temp.substring(1).replaceAll("[^-.0-9]", " "));
                        map_t.add(z.nextFloat());
                        map_w.add(z.nextFloat());
                        map_s.add(z.nextFloat());
                        
                        lat.add(z.nextDouble());
                        lon.add(z.nextDouble());
                        map_time.add(z.next());
                        z.close();
                        
                        
                    } else if (temp.charAt(0) == '?' || temp.charAt(1) == '?') {
                        System.out.println(map_t);
                        System.out.println(map_w);
                        System.out.println(map_s);
                        System.out.println(lat);
                        System.out.println(lon);
                        gmap.run_map()
                                ;
                        
                        map_t.clear();
                        map_w.clear();
                        map_s.clear();
                        lat.clear();
                        lon.clear();
                        
                    } else {
                        gu.update2(temp);
                        
                    }

                    temp = "";
                }
            }
            catch (Exception e){
                logText = "Failed to read data. (" + e.toString() + ")";
                System.out.println("Failed to read data");
            }
        }
    }

    /*write towards the usb port
    parameter int
    return none*/
    public void writeData(int c) {
        try {
            output.write(c);
            output.flush();
        
        }
        catch (Exception e) {
            System.out.println("Failed to write data");
 
        }
    }

}

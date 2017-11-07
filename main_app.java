package ENGG2800;

import java.awt.FlowLayout;
import java.awt.event.AdjustmentListener;
import java.awt.event.ActionEvent;
import java.awt.Component;
import java.awt.Container;
import java.io.IOException;
import javax.swing.*;

/*main class whiich is used to start the program*/
public class main_app {
    public static GUI ui;
    public static IO communicator = new IO();
    
    /*main method whiich is used to start the program
    parameter: none
    return: none    */
    public static void main (String args[])throws IOException {
        float[][] data;
        int if_run = 1;
        int delay_time = 0;    
        ui = new GUI();
        
        //connect with software
        communicator.searchForPorts();
        communicator.connect();
        //if it has connected
        if (communicator.getConnected() == true)
        {
            if (communicator.initIOStream() == true)
            {
                communicator.initListener();
            }
        }
        
        // timmer
        while (if_run == 1) {
            delay_time = ui.get_speed();
            
            java.awt.Window win[] = java.awt.Window.getWindows();  
            
            /*check if the gui is closed*/
            if (win[0].getName().equals("frame0")) {
                try {
                    Thread.sleep(delay_time);                 

                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                
                ui.update_data();
            } else {
                if_run = 0;
            }
        
        }
         
        
        

    }
    
}

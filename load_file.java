/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ENGG2800;

/**
 *
 * @author lvzheng
 */
import java.io.*;
import java.util.*;

public class load_file {
    
    private Scanner data_file;
    
    /*method is used to open file
    parameter: string
    return float[][]*/
    public void openF(String datafile) {
        int dim1;
        int i = 0, j = 0;
        String timeSting;
        ArrayList<Float> record_data = new ArrayList<Float>();                
        String buffer, buffer2;
        GUI gu = main_app.ui;
        
        //open a scanner
        try{
            data_file = new Scanner(new File(datafile));
        }
        catch (Exception error) {
            System.out.println("I did not find the file");
        }
        
        main_app.communicator.CleanGPS();
        
        //read file
        while(data_file.hasNext()) {
            record_data.clear();
            buffer = data_file.nextLine();
            timeSting = buffer.substring(0, 13);
            buffer2 = buffer.replaceAll("S.", "-").replaceAll("E.", "").replaceAll("N.", "").replaceAll("W.", "-").trim();
            
            for (String x: buffer2.split(",")){
                record_data.add(Float.parseFloat(x)); 
            }
           
            if (record_data.size() > 4) {
                main_app.communicator.UpdateGPS(record_data, timeSting);
            } else {
                gu.update3(record_data.get(2), record_data.get(1), record_data.get(3));
                gu.update_tim(timeSting);
            }
        }
        data_file.close();
        
        if (IO.lat.size() != 0) {
            try {
                IO.gmap.run_map();
            } catch (Exception e) {
                System.out.println("fail to run GPS map from load file");
            }
            
        }
    }
}

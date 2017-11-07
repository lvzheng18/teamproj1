package ENGG2800;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.*;
import java.util.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.lang.Math;

public class GUI extends JFrame implements MouseWheelListener, MouseListener {
    public int[] line_data = new int[30];
    public int[] bar_data = new int[10];
    public float[] current_value = new float[3];
    public ArrayList<Float> temp = new ArrayList();
    public ArrayList<Float> wind = new ArrayList();
    public ArrayList<Float> sun = new ArrayList();
    public ArrayList<String> tim = new ArrayList();
    
    public ArrayList<Float> temp_dis = new ArrayList();
    public ArrayList<Float> wind_dis = new ArrayList();
    public ArrayList<Float> sun_dis = new ArrayList();
    
    public ArrayList<Float> tempPoints = new ArrayList();
    public ArrayList<Float> windPoints = new ArrayList();
    public ArrayList<Float> sunPoints = new ArrayList();

    public draw paper;
    /*digit:1; bar chat:2; strip: 3*/
    private int graph_type = 1;
    private Graphics g;
    private int i;
    private int index = 0;
    private float hight;
    private String[] combo_string = {"x1","x5", "x10"};
    private ImageIcon icon;
    private int google_run = 0;
    private int wheel = 800;
    private JPanel  screen = new JPanel ();
    private JFrame window = new JFrame();
    private JScrollPane scpanel = new JScrollPane(screen);
    private JComboBox menu;
    private int[] line_position = new int[4];
    private int[] label_position = new int[3];
    private DateFormat dateFormat = new SimpleDateFormat("ddMMyy.HHmmss");
    
    // create gui
    public GUI() {
        
        super("ENGG2800: weather station");
        //data1 = data2;
        graph_type = 1;
        
        /*clean the array*/
        for (i = 0; i < 10; i++) {
            bar_data[i] = 0;
        }
        bar_data[0] = 40;
        bar_data[1] = 80;
        bar_data[4] = 380;
        bar_data[7] = 680;
        

        window.setSize(850,500);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel buttonLine = new JPanel(); 
        buttonLine.setSize(800,50);
        buttonLine.setBackground(Color.yellow);
        buttonLine.setLayout(new GridLayout(1,6));
        scpanel.setWheelScrollingEnabled(false); 
        
       
        screen.setPreferredSize(new Dimension(840, 400));
        screen.setBackground(Color.gray);
        screen.setLayout(new BorderLayout());
        
        paper = new draw();
        screen.add(paper);
        
        /*add mouse listener*/
        screen.addMouseListener(new MouseAdapter() { 
            
            // add mouse press listener
            public void mousePressed(MouseEvent event) { 

                if (graph_type == 3) {

                    line_position[0] = event.getX();
                    line_position[2] = event.getX();
                    line_position[1] = 0;
                    line_position[3] = 420;
                    
                    label_position[0] = 1;
                    label_position[1] = event.getX();
                    label_position[2] = event.getY();
                    
                    paper.repaint();
                }
            } 
            
            // add mouse release listener
            public void mouseReleased(MouseEvent event) {
                if (graph_type == 3) {

                    line_position[0] = 0;
                    line_position[2] = 0;
                    line_position[1] = 0;
                    line_position[3] = 0;
                    
                    label_position[0] = 0;
                    paper.repaint();
                }
            }
          
        });
        window.addMouseWheelListener(this);
        
        //add button listener
        JButton digit = new JButton("digit");
        buttonLine.add(digit);
        digit.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        screen.setPreferredSize(new Dimension(840, 400));
                        change_screen(screen);
                        graph_type = 1;
                        paper.repaint();
                        SwingUtilities.updateComponentTreeUI(window);
                    }
                }
        );
        
        //add button listener
        JButton bar = new JButton("bar chart");
        buttonLine.add(bar);
        bar.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        screen.setPreferredSize(new Dimension(840, 400));
                        change_screen(screen);
                        graph_type = 2;
                        paper.repaint();
                        SwingUtilities.updateComponentTreeUI(window);
                    }
                }
        );
        
        //add button listener
        JButton strip = new JButton("strip chart");
        buttonLine.add(strip);
        strip.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        screen.setPreferredSize(new Dimension(wheel + 40, 400));
                        change_screen(screen);
                        graph_type = 3;
                        paper.repaint(); 
                        SwingUtilities.updateComponentTreeUI(window);
                    }
                }
        );
        
        //add button listener
        JButton map = new JButton("map");
        buttonLine.add(map);
        map.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {                                               
                        main_app.communicator.writeData(115);                        
                    }
                }
        );
        
        menu = new JComboBox(combo_string);
        buttonLine.add(menu);
        
        //add button listener
        JButton load = new JButton("load");
        buttonLine.add(load);
        load.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {                        
                        int time = 1;                        
                        index = 0;
                        temp.clear();
                        wind.clear();
                        sun.clear();
                        tim.clear();
                        
                        temp_dis.clear();
                        wind_dis.clear();
                        sun_dis.clear();
                        
                        tempPoints.clear();
                        windPoints.clear();
                        sunPoints.clear();
                        
                        load_file f = new load_file();
                        f.openF("load_file.csv");
                    
                    }
                }
        );
        
        JButton save = new JButton("save");
        buttonLine.add(save);
        save.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        
                        try {
                            PrintWriter outf = new PrintWriter("save_file.csv", "UTF-8");
                            for (int i = 0; i < temp.size(); i++) {
                                outf.print(tim.get(i) + "," + wind.get(i) + "," + temp.get(i) + "," + sun.get(i) + "\n");                           
                            }
                            
                            if (IO.lat.size() > 0) {
                                for (int i = 0; i < IO.lat.size(); i++) {
                                    outf.print(IO.map_time.get(i) + "," + IO.map_w.get(i) + "," + IO.map_t.get(i) + "," + IO.map_s.get(i) + "," 
                                            + (IO.lat.get(i) > 0 ? "N." + IO.lat.get(i) : "S." + Math.abs(IO.lat.get(i))) + ","
                                            + (IO.lon.get(i) > 0 ? "E." + IO.lon.get(i) : "W." + Math.abs(IO.lon.get(i))) + "\n");                            
                                }
                            }
                            
                            outf.close();
                        } catch(Exception e) {
                            System.out.println("I fail to open a file to save the file");
                        }
                        
                        
                        
                    }
                }
        );
        
        window.add(scpanel, BorderLayout.CENTER);
        window.add(buttonLine, BorderLayout.SOUTH);
        window.setVisible(true);
        this.pack();
        
    }
    
    public void change_screen(JPanel  s) {
        if (google_run == 1) {
            s.removeAll();
            s.add(paper);
            google_run = 0;
        }
    }
    
    /*receive from firmware*/
    public void update2 (String temp1) {
        
        float t = 0;
        float w = 0;
        float s = 0;
        Scanner x = new Scanner(temp1.replaceAll("[^-.0-9]", " "));

        
        t = x.nextFloat();
        w = x.nextFloat();
        s = x.nextFloat();
        this.temp.add(t);
        wind.add((float)(w / 10.0));
        sun.add(s);
        tim.add(dateFormat.format(new Date()));
    }
    

    
    public int get_speed() {
        if (menu.getSelectedIndex() == 1) {
            return 200;
        } else if (menu.getSelectedIndex() == 2) {
            return 100;
        } else {
            return 1000;
        }
    }
    
    /*load data*/
    public void update3 (float t, float w, float s) {
        temp.add(t);
        wind.add(w);
        sun.add(s);
    
    }
    
    public void update_tim (String time) {
        tim.add(time);
    }
    
    public void update_data() {
        float t = 0;
        float w = 0;
        float s = 0;
        float intervalT, intervalW, intervalS;
        int t1, t2, timeCoefficient;
        hight = 440;
        
        
        
        if (index < (temp.size())) {
            
            if (index == 0) {
                tempPoints.add(temp.get(index));
                windPoints.add(wind.get(index));
                sunPoints.add(sun.get(index));
            } else {
                t1 = Integer.parseInt(tim.get(index - 1).substring(7));
                t2 = Integer.parseInt(tim.get(index).substring(7));       
                timeCoefficient = (t2 % 100 - t1 % 100) + 60 * (t2 / 100 % 100 - t1 / 100 % 100) + 3600 * (t2 / 10000 - t1 / 10000);
                
                intervalT = (temp.get(index) - temp.get(index - 1)) / timeCoefficient;
                intervalW = (wind.get(index) - wind.get(index - 1)) / timeCoefficient;
                intervalS = (sun.get(index) - sun.get(index - 1)) / timeCoefficient;
                
                for (int i = 0; i < timeCoefficient; i++) {
                    tempPoints.add(temp.get(index - 1) + (i + 1) * intervalT);
                    windPoints.add(wind.get(index - 1) + (i + 1) * intervalW);
                    sunPoints.add(sun.get(index - 1) + (i + 1) * intervalS);
                }
            }
            
                    
            t = (float)((temp.get(index) + 20) * 1.2);
            w = wind.get(index) * 5;
            s = (sun.get(index) / 50);

            if (t > 100) {
                t = 100;
            } else if (w > 100) {
                w = 100;
            } else if (s > 100) {
                s = 100;
            }
            current_value[0] = temp.get(index);
            current_value[1] = wind.get(index);        
            current_value[2] = sun.get(index);

            if (temp_dis.size() >= 3600) {
                return;
            }
            
            temp_dis.add((float)(hight - t*3.0));
            
            bar_data[3] = (int)(t * 3);
            bar_data[2] = (int)(hight - bar_data[3]);

            wind_dis.add((float)(hight - w*3.0));
            
            bar_data[6] = (int)(w * 3);
            bar_data[5] = (int)(hight - bar_data[6]);

            sun_dis.add((float)(hight - s*3.0));

            bar_data[8] = (int)(hight - s*3.0);
            bar_data[9] = (int)(s * 3);

            paper.repaint();
            index++;
        }
        
    }
    
    private class draw extends JPanel{
        
        public draw() {
            setPreferredSize(new Dimension(800,440));
        }
        
        public void paint(Graphics g) {
            int c = 0;
            double x = 0;
            int t1, t2;
            int timeCoefficient = 0;
            
            super.paint(g);
            
            this.setBackground(Color.white);
            

            g.clearRect(0, 0, getWidth(), getHeight());
            if (graph_type == 1) {
                g.setColor(Color.black);
                g.drawString("temperature: " + current_value[0],350,100);
                
                g.setColor(Color.blue);
                g.drawString("wind speed: " + current_value[1],350,220);
                
                g.setColor(Color.red);
                g.drawString("sunshine: " + current_value[2],350,350);
            } else if (graph_type == 2) {
                g.setColor(Color.black);
                g.fillRect(bar_data[1], bar_data[2], bar_data[0], bar_data[3]);
                
                g.setColor(Color.black);
                g.drawString("temperature: "  + current_value[0],80,100);
                
                g.setColor(Color.blue);
                g.fillRect(bar_data[4], bar_data[5], bar_data[0], bar_data[6]);
                
                g.setColor(Color.blue);
                g.drawString("wind speed: "  + current_value[1],400,100);

                g.setColor(Color.red);
                g.fillRect(bar_data[7], bar_data[8], bar_data[0], bar_data[9]);
                g.setColor(Color.red);
                g.drawString("sun shine: " + current_value[2],680,100);
            } else if (graph_type == 3) {
                x = 0;
                c = temp_dis.size() - 1;
                if (c > 3599) {
                    c = 3599;
                } 
                double interval = wheel / 3601.0;
                for (int i = 0; i < c; i++) {
                    g.setColor(Color.black);
                    
                    /*if (!tim.get(i).substring(0, 6).equals(tim.get(i + 1).substring(0, 6))) {
                        return;
                    }*/
                    
                    t1 = Integer.parseInt(tim.get(i).substring(7));
                    t2 = Integer.parseInt(tim.get(i + 1).substring(7));
                    
                    timeCoefficient = (t2 % 100 - t1 % 100) + 60 * (t2 / 100 % 100 - t1 / 100 % 100) + 3600 * (t2 / 10000 - t1 / 10000);
                    //System.out.println(i);
                    g.drawLine((int)(x + 100), temp_dis.get(i).intValue(), (int)(x + interval * timeCoefficient + 100), temp_dis.get(i + 1).intValue());
                   
                    g.setColor(Color.green);
                    g.drawLine((int)(x + 100), wind_dis.get(i).intValue(), (int)(x + interval * timeCoefficient + 100), wind_dis.get(i + 1).intValue());
                  
                    g.setColor(Color.red);
                    g.drawLine((int)(x + 100), sun_dis.get(i).intValue(), (int)(x + interval * timeCoefficient + 100), sun_dis.get(i + 1).intValue());
                    x += interval * timeCoefficient;
                }
                
                if (label_position[0] == 1) {
                    g.setColor(Color.black);
                    g.drawLine(line_position[0], line_position[1], line_position[2], line_position[3]);
                    
                    int label_index = (int)((label_position[1] - 100) / interval);
                    float tl = 0, wl = 0, sl = 0;
                    
                    if ((label_index < tempPoints.size()) && (label_index >= 0)) {
                        tl = tempPoints.get(label_index);
                        wl = windPoints.get(label_index);
                        sl = sunPoints.get(label_index);
                    }
                    

                    g.drawString("Time: "+ (label_index / 60) + "m" + (label_index % 60)+"s; T: " + tl + "; W: " + wl + "; S: " + sl, label_position[1] + 10, label_position[2] - 10);
                }
                
                int yInterval = 44;
                
                g.setColor(Color.black);
                g.drawLine(10, 30, 10, 440);
                for (int i = 0; i < 11; i++) {
                    g.setColor(Color.black);
                    g.drawString("-" + ((i * 10) - 20),10, 440 - i * yInterval);
                    
                }
                g.setColor(Color.blue);
                g.drawLine(30, 30, 30, 440);
                g.drawLine(10, 30, 10, 440);
                for (int i = 0; i < 11; i++) {
                    g.setColor(Color.black);
                    g.drawString("-" + ((i * 3)),30, 440 - i * yInterval);
                    
                }
                
                g.setColor(Color.red);
                g.drawLine(50, 30, 50, 440);
                for (int i = 0; i < 11; i++) {
                    g.setColor(Color.black);
                    g.drawString("-" + ((i * 500)),50, 440 - i * yInterval);
                    
                }
                
                double time_interval = 0;
                
                if (wheel <= 4000) {
                    for (int i = 0; i < 7; i++) {
                        g.setColor(Color.black);
                        g.drawString(i + "0 min",(int)(time_interval * 600 + 100),20);
                        time_interval += wheel/3601.0;
                    }
                } else if (wheel > 4000) {
                    for (int i = 0; i < 61; i++) {
                        g.setColor(Color.black);
                        g.drawString(i + " min",(int)(time_interval * 60 + 100),20);
                        time_interval += wheel/3601.0;
                    }
                                   
                }
                if (wheel >= 8000) {
                    time_interval = 0;
                    for (int i = 0; i < 301; i++) {
                        g.setColor(Color.black);
                        g.drawString("|",(int)(time_interval * 12 + 100),5);
                        time_interval += wheel/3601.0;
                    }

                }
            }
            
            
            
        }
    }
    
@Override
        public void mouseWheelMoved(MouseWheelEvent event) {
            int buffer;
            if (graph_type == 3) {
            
                wheel = wheel + event.getWheelRotation() * 400;
                if (wheel > 24800) {
                    wheel = 24800;
                }

                if (wheel < 800) {
                    wheel = 800;
                }
                screen.setPreferredSize(new Dimension(wheel + 140, 400));
                SwingUtilities.updateComponentTreeUI(window);
            }
        }

@Override        
        public void mousePressed(MouseEvent event) {
            
        }

@Override        
        public void mouseReleased(MouseEvent event) {
            
        }
        
@Override
        public void mouseExited(MouseEvent event) {
            
            
         }
        
@Override
        public void mouseEntered(MouseEvent event) {
            
            
         }
        
@Override
        public void mouseClicked(MouseEvent e) {
            
           
         }
    
}


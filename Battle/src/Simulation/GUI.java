package Simulation;

import Schemes.Colors;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Random;
import java.util.TimerTask;

/**
 * Created by anka on 15.01.17.
 */
public class GUI extends JFrame {
    private Simulator simulator;
    private Controller controller;
    private java.util.Timer timer;
    private TimerTask task;
    private int height;
    private int width;

    public GUI (){
        super("Viking Simulator");
        super.frameInit();
        this.getContentPane().setLayout(new CardLayout(0,0));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Setting bounds
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double screenWidth = screenSize.getWidth();

        width = 1202;
        height = (int) (screenSize.getHeight() - 60);

        this.setBounds((int) ((screenWidth-1202)/2), 0, width, height);

        // Creating start panel
        controller = new Controller();
        this.getContentPane().add(controller, "Controller");
        controller.setLayout(null);
        controller.setVisible(true);

        // Logo and maker
        BufferedImage bLogo = null;
        BufferedImage bMaker = null;
        try {
            bMaker = ImageIO.read(this.getClass().getResource("madeBy.png"));
            bLogo = ImageIO.read(this.getClass().getResource("Title.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(bLogo.getHeight());
        Image logo = bLogo.getScaledInstance(resize(bLogo.getWidth()), resize(bLogo.getHeight()), Image.SCALE_DEFAULT);
        Image maker = bMaker.getScaledInstance(resize(bMaker.getWidth()), resize(bMaker.getHeight()), Image.SCALE_DEFAULT);
        JLabel logoLabel = new JLabel(new ImageIcon(logo));
        JLabel makerLabel = new JLabel(new ImageIcon(maker));
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        makerLabel.setHorizontalAlignment(JLabel.CENTER);
        logoLabel.setBounds(0,0,width, logo.getHeight(null));
        makerLabel.setBounds(0, resize(925), width, maker.getHeight(null));
        controller.add(logoLabel);
        controller.add(makerLabel);


        // VARIABLES
        // Informations
        final JLabel info = new JLabel("If ready press generate!");
        info.setFont(new Font("Romain", Font.PLAIN, resize(25)));
        info.setHorizontalAlignment(JLabel.CENTER);
        info.setBounds(0, resize(700), width, resize(50));
        controller.add(info);


        // Size of village
        JLabel village = new JLabel("<html><center>Choose size<br />of the village<center></html>");
        village.setForeground(Colors.BUILDING);
        village.setFont(new Font("Romain", Font.PLAIN, resize(30)));
        village.setBounds(60, resize(300), 300, resize(100));
        controller.add(village);

        final JCheckBox villageSize1 = new JCheckBox("4 buildings");
        villageSize1.setFont(new Font("Romain", Font.PLAIN, resize(20)));
        villageSize1.setBounds(60, resize(400), 150, resize(50));
        controller.add(villageSize1);

        final JCheckBox villageSize2 = new JCheckBox("5 buildings");
        villageSize2.setFont(new Font("Romain", Font.PLAIN, resize(20)));
        villageSize2.setBounds(60, resize(460), 150, resize(50));
        controller.add(villageSize2);

        final JCheckBox villageSize3 = new JCheckBox("6 buildings BUGS!");
        villageSize3.setFont(new Font("Romain", Font.PLAIN, resize(20)));
        villageSize3.setBounds(60, resize(520), 210, resize(50));
        controller.add(villageSize3);

        ButtonGroup villageSize = new ButtonGroup();
        villageSize.add(villageSize1);
        villageSize.add(villageSize2);
        villageSize.add(villageSize3);

        villageSize1.setSelected(true);


        // Size of vikings squad
        JLabel vikings = new JLabel("<html><center>Choose size of<br />the viking squads<center></html>");
        vikings.setForeground(Colors.VIKING);
        vikings.setFont(new Font("Romain", Font.PLAIN, resize(30)));
        vikings.setBounds(451, resize(300), 300, resize(100));
        controller.add(vikings);

        JLabel minLabel1 = new JLabel("<html><center>Minimal<br />size of squad<center><html>");
        JLabel maxLabel1 = new JLabel("<html><center>Maximal<br />size of squad<center><html>");
        minLabel1.setBounds(480, resize(420), 150, resize(50));
        minLabel1.setFont(new Font("Romain", Font.PLAIN, resize(20)));
        maxLabel1.setBounds(480, resize(500), 150, resize(50));
        maxLabel1.setFont(new Font("Romain", Font.PLAIN, resize(20)));
        controller.add(minLabel1);
        controller.add(maxLabel1);

        final JFormattedTextField minField1 = new JFormattedTextField();
        minField1.setHorizontalAlignment(JFormattedTextField.CENTER);
        minField1.setBounds(640, resize(420), 50, resize(50));
        controller.add(minField1);
        minField1.setValue(1);

        final JFormattedTextField maxField1 = new JFormattedTextField();
        maxField1.setHorizontalAlignment(JFormattedTextField.CENTER);
        maxField1.setBounds(640, resize(500), 50, resize(50));
        controller.add(maxField1);
        maxField1.setValue(10);

        minField1.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                JFormattedTextField src = (JFormattedTextField) propertyChangeEvent.getSource();
                int min = (int) ((Number)src.getValue()).doubleValue();
                int max = (int) ((Number)maxField1.getValue()).doubleValue();
                if (min < 1 || min > 10){
                    minField1.setValue(1);
                    controller.getVikings().x = 1;
                }
                else if (max < min){
                    minField1.setValue(max);
                    controller.getVikings().x = max;
                }
                else
                    controller.getVikings().x = min;
            }
        });

        maxField1.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                JFormattedTextField src = (JFormattedTextField) propertyChangeEvent.getSource();
                int max = (int) ((Number)src.getValue()).doubleValue();
                int min = (int) ((Number)minField1.getValue()).doubleValue();
                if (max < 1 || max > 10){
                    maxField1.setValue(10);
                    controller.getVikings().y = 10;
                }
                else if (min > max){
                    maxField1.setValue(min);
                    controller.getVikings().y = min;
                }
                else
                    controller.getVikings().y = max;
            }
        });


        // Size of villagers squad
        JLabel villagers = new JLabel("<html><center>Choose size of<br />the villager squads<center></html>");
        villagers.setForeground(Colors.VILLAGER);
        villagers.setFont(new Font("Romain", Font.PLAIN, resize(30)));
        villagers.setBounds(842, resize(300), 300, resize(100));
        controller.add(villagers);

        JLabel minLabel2 = new JLabel("<html><center>Minimal<br />size of squad<center><html>");
        JLabel maxLabel2 = new JLabel("<html><center>Maximal<br />size of squad<center><html>");
        minLabel2.setBounds(891, resize(420), 150, resize(50));
        minLabel2.setFont(new Font("Romain", Font.PLAIN, resize(20)));
        maxLabel2.setBounds(891, resize(500), 150, resize(50));
        maxLabel2.setFont(new Font("Romain", Font.PLAIN, resize(20)));
        controller.add(minLabel2);
        controller.add(maxLabel2);

        final JFormattedTextField minField2 = new JFormattedTextField();
        minField2.setHorizontalAlignment(JFormattedTextField.CENTER);
        minField2.setBounds(1051, resize(420), 50, resize(50));
        controller.add(minField2);
        minField2.setValue(1);

        final JFormattedTextField maxField2 = new JFormattedTextField();
        maxField2.setHorizontalAlignment(JFormattedTextField.CENTER);
        maxField2.setBounds(1051, resize(500), 50, resize(50));
        controller.add(maxField2);
        maxField2.setValue(10);

        minField2.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                JFormattedTextField src = (JFormattedTextField) propertyChangeEvent.getSource();
                int min = (int) ((Number)src.getValue()).doubleValue();
                int max = (int) ((Number)maxField2.getValue()).doubleValue();
                if (min < 1 || min > 10){
                    minField2.setValue(1);
                    controller.getVillagers().x = 1;
                }
                else if (max < min){
                    minField2.setValue(max);
                    controller.getVillagers().x = max;
                }
                else
                    controller.getVillagers().x = min;
            }
        });

        maxField2.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                JFormattedTextField src = (JFormattedTextField) propertyChangeEvent.getSource();
                int max = (int) ((Number)src.getValue()).doubleValue();
                int min = (int) ((Number)minField2.getValue()).doubleValue();
                if (max < 1 || max > 10){
                    maxField2.setValue(10);
                    controller.getVillagers().y = 10;
                }
                else if (min > max){
                    maxField2.setValue(min);
                    controller.getVillagers().y = min;
                }
                else
                    controller.getVillagers().y = max;
            }
        });


        // Random button
        final JButton randomButton = new JButton("@$%  Randomize  %$@");
        randomButton.setFont(new Font("Romain", Font.PLAIN, resize(20)));
        randomButton.setBounds(430, resize(600), 342, resize(50));
        controller.add(randomButton);
        randomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Random r = new Random();
                int village = r.nextInt(3);
                switch (village){
                    case 0:
                        villageSize1.setSelected(true);
                        break;
                    case 1:
                        villageSize2.setSelected(true);
                        break;
                    case 2:
                        villageSize3.setSelected(true);
                        break;
                }
                int max1 = r.nextInt(10) + 1;
                int min1 = r.nextInt(max1) + 1;
                int max2 = r.nextInt(10) + 1;
                int min2 = r.nextInt(max2) + 1;
                minField1.setValue(min1);
                minField2.setValue(min2);
                maxField1.setValue(max1);
                maxField2.setValue(max2);
                controller.setVikings(new Point(min1, max1));
                controller.setVillagers(new Point(min2, max2));
            }
        });


        // Start button
        final JButton startButton = new JButton("GENERATE");
        startButton.setFont(new Font("Romain", Font.BOLD, resize(50)));
        startButton.setBounds(75,resize(800),1052,resize(100));
        controller.add(startButton);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {

                // Getting variables from controller
                if (villageSize1.isSelected()) controller.setVillage(4);
                if (villageSize2.isSelected()) controller.setVillage(5);
                if (villageSize3.isSelected()) controller.setVillage(6);

                // Creating simulation
                simulator = new Simulator(controller.getVillage(), controller.getVikings(), controller.getVillagers());
                getContentPane().add(simulator, "Simulator");
                simulator.setLayout(null);

                // Setting visibility
                simulator.setVisible(true);
                controller.setVisible(false);

                // start timer
                task = new TimerTask() {
                    public void run() {
                        simulator.simulation();
                    }
                };
                timer = new java.util.Timer();
                timer.schedule(task,0,1000/60);

                // Buttons
                final JButton backButton = new JButton("<html><center>Back<br />to menu<center></html>");
                backButton.setBounds(1100,0,100,100);
                simulator.add(backButton);

                backButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        controller.setVisible(true);
                        simulator.setVisible(false);
                        getContentPane().remove(simulator);
                        timer.cancel();
                        task.cancel();
                    }
                });

                final JButton pauseButton = new JButton("Start");
                pauseButton.setBounds(1000,0,100,100);
                simulator.add(pauseButton);
                pauseButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        if (!simulator.getState()){
                            simulator.setState(true);
                            pauseButton.setText("Stop");
                        }
                        else {
                            simulator.setState(false);
                            pauseButton.setText("Start");
                        }
                    }
                });


                final JLabel fps = new JLabel("<html><center>FPS = 60<center><html>");
                fps.setFont(new Font("Romain", Font.BOLD, 15));
                fps.setBounds(1050,110,100,20);
                simulator.add(fps);

                final JSlider speed = new JSlider(JSlider.HORIZONTAL, 1, 200, 60);
                speed.setBounds(1000, 130, 200, 20);
                simulator.add(speed);
                speed.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent changeEvent) {
                        JSlider src = (JSlider) changeEvent.getSource();
                        if (src.getValueIsAdjusting()) return;
                        timer.cancel();
                        task.cancel();
                        task = new TimerTask() {
                            public void run() {
                                simulator.simulation();
                            }
                        };
                        timer = new java.util.Timer();
                        timer.schedule(task,0,1000/src.getValue());
                        String s = "FPS = " + src.getValue();
                        fps.setText("<html><center>" + s + "<center><html>");
                    }
                });
            }
        });

        this.setVisible(true);
        this.setResizable(false);
    }

    // Exit
    public void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            this.dispose();
            System.out.print("\n\nQuitting the application!\n");
            System.exit(0);
        }
    }

    public int resize(double size){
        return (int) (height*size/1020);
    }
}




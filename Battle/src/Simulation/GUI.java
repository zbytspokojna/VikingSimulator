package Simulation;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.TimerTask;

/**
 * Created by anka on 15.01.17.
 */
public class GUI extends JFrame {
    private Simulator simulator;
    private Controller controller;
    private java.util.Timer timer;
    private TimerTask task;
    int rows, cols, seeds;

    public GUI (){
        super("Battle");
        super.frameInit();
        this.getContentPane().setLayout(new CardLayout(0,0));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        this.rows = 1000;
        this.cols = 1000;
        this.seeds = 900;

        this.setBounds(0, 0, rows + 202, cols + 29);

        // Creating start panel
        controller = new Controller();
        this.getContentPane().add(controller, "Controller");
        controller.setLayout(null);
        controller.setVisible(true);

        // Buttons
        final JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {

                // Creating simulation
                simulator = new Simulator(rows, cols, seeds);
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
                final JButton backButton = new JButton("<html>Back<br />to menu</html>");
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
                backButton.setBounds(1100,0,100,100);
                simulator.add(backButton);

                final JButton pauseButton = new JButton("Start");
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
                pauseButton.setBounds(1000,0,100,100);
                simulator.add(pauseButton);

                final JTextField textField = new JTextField("FPS = 60");
                textField.setBounds(1050,120,100,20);
                simulator.add(textField);

                final JSlider speed = new JSlider(JSlider.HORIZONTAL, 1, 200, 60);
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
                        textField.setText("FPS = " + src.getValue());
                    }
                });
                speed.setBounds(1000, 150, 200, 20);
                simulator.add(speed);

                //

            }
        });
        startButton.setBounds(550,550,100,100);
        controller.add(startButton);


        this.setVisible(true);
        this.setResizable(false);
    }

    public void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            this.dispose();
            System.out.print("\n\nQuitting the application!\n");
            System.exit(0);
        }
    }
}




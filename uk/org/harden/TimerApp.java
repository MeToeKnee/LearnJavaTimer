package uk.org.harden;

import javax.swing.*;
import java.awt.*;
//import java.util.logging.Logger;

class TimerApp {

    //private static final Logger LOGGER = Logger.getLogger(TimerApp.class.getName());

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                runProgram();
            }
        });
    }

    private static void runProgram() {
        final JFrame guiFrame = new JFrame();
        guiFrame.setLayout(new BorderLayout());

        //make sure the program exits when the frame closes
        guiFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        guiFrame.setTitle("Timer");

        JComponent mainPane = new AppView(2 + (int) (Math.random() * 4));
        guiFrame.setContentPane(mainPane);
        guiFrame.pack();
        guiFrame.setSize(900, 470);
        guiFrame.setVisible(true);
    }
}

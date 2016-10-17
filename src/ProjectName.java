import javax.swing.*;

/**
 * Created by Alex on 9/26/16.
 *
 * Code Adapted from template by Dave Small
 *
 * ProjectName.java
 *  - Declare ImageFrame of set size, modify close policy, display image generated using ProjectName simulation
 *
 */
public class ProjectName {

    // WINDOW SIZE
    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;

    // Main
    public static void main(String[] args) {
        JFrame frame = new ImageFrame(WIDTH, HEIGHT);               // Declare
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);       // Set close policy
        frame.setVisible(true);                                     // Show GUI
    }


}

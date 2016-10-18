import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressing;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import javafx.scene.transform.Affine;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Alex on 10/17/16.
 *
 * Code Adapted from template by Dave Small
 *
 * ImageFrame.java
 *  - Setup target_image frame for use by AffineTransformFractal.java
 *  - Setup title bar
 *  - File menu w/
 *      - Load IFS description
 *      - Configure Image
 *      - Display IFS
 *      - Save Image
 *      - Exit
 *
 *  - Load IFS Description :
 *      - Prompt user for the file containing IFS description (JFileChooser)
 *      - Load IFS description from config file
 *      - Use scanner to read from file
 *
 *  - Configure Image :
 *      - Prompt user for image height and width
 *      - Prompt user for the background & foreground colors as hex #s
 *      - Create BufferedImage
 *
 *  - Display IFS :
 *    - Prompt user for the # of generations n
 *    - If implements MRCM you may prompt for an image to tile
 *    - Generate the nth generation image
 *    - Display image
 *
 *  - Save Image :
 *      - Program shall prompt user for output file and save current IFS image as PNG
 *      - Use codeblock given for output
 *
 *  - Testing :
 *      - Use provided test cases for testing output
 *
 */

public class ImageFrame extends JFrame {

    private JFileChooser chooser = new JFileChooser();  // File chooser
    private File sourceIFSFile, outputIFSFile;          // Chosen IFS file & output file
    private BufferedImage targetImage;                  // Saved output image
    private Graphics2D targetGraphics;                  // Target graphics2d object
    private ArrayList<IFSTransform> transforms;         // Transformations for simulation

    private boolean debug = true;                       // Debugging

    // Colors & color masks
    private int color_background = 0xff000000,
                red_mask = 0x00ff0000,
                green_mask = 0x0000ff00,
                blue_mask = 0x000000ff;

    // Constructor
    public ImageFrame(int width, int height) {

        this.setTitle("CAP3027 2016 -- HW07 -- R. Alex Clark");             // Frame Title
        this.setSize(width, height);                                        // Frame Size
        addMenu();                                                          // Add Menu to Frame

    }

    // Setup menu for frame
    private void addMenu() {

        JMenu fileMenu = new JMenu("File");                                 // Setup file menu

        JMenuItem loadIFSDescItem = new JMenuItem("Load IFS Description");  // Load IFS item
        JMenuItem configureImgItem = new JMenuItem("Configure Image");      // Configure image item
        JMenuItem displayIFSItem = new JMenuItem("Display IFS");            // Display IFS item
        JMenuItem saveImgItem = new JMenuItem("Save Image");                // Save Image Item
        JMenuItem exitItem = new JMenuItem("Exit");                         // Setup Exit Ttem

        // Setup listener for File menu exit action
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Exit program function
            }
        });

        // Setup listener for Load IFS Description Item
        loadIFSDescItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                boolean setupReady = false;                     // Check for valid file
                sourceIFSFile = getFile();                      // Get source file
                if (sourceIFSFile != null) setupReady = true;   // Check ready for simulation

                if (setupReady) {
                    transforms = readIFSFile(sourceIFSFile);    // Get transformations for simulations
                }

                else System.out.println("User cancelled simulation");

            }

        });

        // Setup listener for Configure Image Item
        configureImgItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }

        });

        // Setup listener for Display IFS Item
        displayIFSItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }

        });

        // Setup listener for Save Image Item
        saveImgItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

//                try
//                {
//                    javax.imageio.ImageIO.write( image, "png", outputFile );
//                }
//                catch ( IOException e )
//                {
//                    JOptionPane.showMessageDialog( ImageFrame.this,
//                            "Error saving file",
//                            "oops!",
//                            JOptionPane.ERROR_MESSAGE );
//                }

            }

        });

        // File menu setup
        fileMenu.add(loadIFSDescItem);
        fileMenu.add(configureImgItem);
        fileMenu.add(displayIFSItem);
        fileMenu.add(saveImgItem);
        fileMenu.add(exitItem);

        JMenuBar menuBar = new JMenuBar();                  // Declare menuBar
        menuBar.add(fileMenu);                              // Add 'File' menu to bar
        this.setJMenuBar(menuBar);                          // Set menuBar into frame

    }

    // Get the source image
    private BufferedImage getSourceImage() {

        BufferedImage src_img = null;                           // Source img
        File file = getFile();                                  // Get file object using chooser

        if (file != null) {                                     // Set BufferedImage if successful
            try {
                src_img = ImageIO.read(file);
            }
            catch (IOException e) {                             // Log failure to user
                // Output failure and notify user
                System.out.println("Display of buffered target_image failed");
                JOptionPane.showMessageDialog(this, e);
            }
        }

        return src_img;                                         // Return image
    }

    // Read data from given file
    private ArrayList<IFSTransform> readIFSFile(File sourceIFSFile) {

        ArrayList<IFSTransform> transforms = new ArrayList<IFSTransform>();         // Transform collection
        IFSTransform transform;

        try {

            BufferedReader br = new BufferedReader(new FileReader(sourceIFSFile));                  // Create new reader for given file
            String transformDescLine = br.readLine();                                               // Read each line as one transformation description

            Scanner sc;                                                                             // Scanner for reading each line
            double[] tValues = new double[7];                                                       // Array for values, up to prob
            int pos;                                                                                // Index of current value pos

            while(transformDescLine != null) {

                sc = new Scanner(transformDescLine);                                                // Set scanner
                pos = 0;                                                                            // Set prob to 0.0

                while(sc.hasNextDouble()) {
                    tValues[pos++] = sc.nextDouble();                                               // Get all values from line
                }

                if (debug) {
                    for(double d : tValues) {
                        System.out.print(d + " ");
                    }
                    System.out.print("\n");
                }

                // Create new transform
                /* T Constructor order: m00 m10 m01 m11 m02 m12 prob
                                        a   c   b   d   e   f   p
                 */
                // Values order: a b c d e f p
                transform = new IFSTransform(tValues[0], tValues[2], tValues[1],
                                                tValues[3], tValues[4], tValues[5],
                                                tValues[6]);

                transforms.add(transform);                                  // Add transform to collection
                transformDescLine = br.readLine();                          // Read next line
            }


        }
        catch (FileNotFoundException e) {   // Handle file not found exception
            System.out.println("File not found for passed FILE object");
        }
        catch (IOException e) {             // Handle IO exception for reading lines
            System.out.println("Unable to read line from file, IOException encountered");
        }

        System.out.println("Finished");

        return transforms;

    }

    // Prompt file selection from user and return it
    private File getFile() {

        File file = null;                                   // Declare file

        // Test if JFrame approve button was pressed and get the chosen file (?)
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
        }

        return file;                                        // Return file to display in frame
    }

    // Prompt user input of step count
    private int promptCountWithMessage(String prompt) {

        int count = -1;                                                                             // Result of capture parse
        String capture;                                                                             // Capture step count

        while(true) {
            try {
                capture = JOptionPane.showInputDialog(prompt);                                      // Display prompt for steps & capture value
                if(capture != null) count = Integer.parseInt(capture);                              // Parse captured string as integer
            } catch (Exception e) {
                System.out.println("Failure to parse user input: " + e.getLocalizedMessage());      // Log error
                JOptionPane.showMessageDialog(this, "Please enter an integer!");                    // Notify user of error
                continue;                                                                           // Display prompt again after error
            }
            break;      // Break out of pane if size was successfully set or user cancelled the pane
        }

        return count;   // Return parsed step count
    }

    // Display inputted buffered target_image in frame
    private void displayBufferedImage(BufferedImage image) {

        // Set the content to contain the target_image inside of an icon inside of a label, make label scrollable
        this.setContentPane(new JScrollPane(new JLabel(new ImageIcon(image))));
        // Validate the ImageFrame with new content
        this.validate();

    }

    // Clamp the values to [0, 255]
    private int clampValue(int color) {
        if(color > 255) color = 255;    // Clamp ceiling
        if(color < 0) color = 0;        // Clamp floor
        return color;                   // Return clamped integer of [0, 255]
    }

}

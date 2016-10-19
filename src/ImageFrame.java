import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Intercepter;
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
    private File sourceIFSFile;          // Chosen IFS file & output file
    private BufferedImage targetImage;                  // Saved output image
    private Graphics2D targetGraphics;                  // Target graphics2d object
    private IFSTransformList transforms;         // Transformations for simulation

    private boolean debug = true;                       // Debugging

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

                    // Start thread on reading IFS file and creating the transforms list
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            transforms = readIFSFile(sourceIFSFile);                                    // Get transformations for simulations
                            if (!transforms.transformWeightsSet) transforms.assignDeterminantWeights(); // Assign det weights if they were not already manually listed
                        }
                    }).start();

                }
                else System.out.println("User cancelled loading IFS description");

            }

        });

        // Setup listener for Configure Image Item
        configureImgItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                boolean setupReady = false;                         // Check for valid input
                int
                        size = -1,                                  // Image size
                        foregroundHex = Integer.MIN_VALUE,          // Image foreground hex color
                        backgroundHex = Integer.MIN_VALUE;          // Image background hex color

                // Capture user input
                size = (int) promptCountWithMessage("Enter image size: ");
                if (size > 0) foregroundHex = (int) promptCountWithMessage("Enter hexcode for image foreground: ");
                if (foregroundHex != Integer.MIN_VALUE) backgroundHex = (int) promptCountWithMessage("Enter hexcode for image background: ");
                if (backgroundHex != Integer.MIN_VALUE) setupReady = true;

                if (setupReady) {

                    // Create finals of captured vars for thread
                    final int
                            fSize = size,
                            fForegroundHex = foregroundHex,
                            fBackgroundHex = backgroundHex;

                    // Start thread on creating new BufferedImage
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            targetImage = new BufferedImage(fSize, fSize, BufferedImage.TYPE_INT_ARGB);   // Create image
                            targetGraphics = (Graphics2D) targetImage.getGraphics();                    // Generate g2d obj for image

                            targetGraphics.setColor(new Color(fBackgroundHex));                          // Set background
                            targetGraphics.fillRect(0, 0, fSize, fSize);

                            targetGraphics.setColor(new Color(fForegroundHex));                          // Set foreground color
                        }
                    }).start();

                }
                else System.out.println("User cancelled image configuration");


            }

        });

        // Setup listener for Display IFS Item
        displayIFSItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                boolean setupReady = false;                         // Flag for valid input
                int n = -1;                                         // Number of generations for IFS

                // Capture user input
                n = (int) promptCountWithMessage("Enter number of generations for IFS simulation: ");
                if (n >= 0) setupReady = true;

                if (setupReady) {
                    final int fN = n;                               // Final n

                    // Start thread on running IFS algorithm simulation
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // Run IFS algorithm
                            //runIFSGenerationWithThreshold()

                        }
                    }).start();

                }
                else System.out.println("User cancelled IFS generation simulation");

            }

        });

        // Setup listener for Save Image Item
        saveImgItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Output file, handle exceptions
                try {
                    File outputFile = new File("IFS_Image.png");                    // File name, location project directory
                    javax.imageio.ImageIO.write(targetImage, "png", outputFile);   // Write image to file of type png using outputFile
                }
                catch ( IOException ioe ) {

                    // Handle IOExceptions
                    JOptionPane.showMessageDialog( ImageFrame.this,
                            "Error saving file",
                            "oops!",
                            JOptionPane.ERROR_MESSAGE );

                }

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

    private int runIFSTransformWithThreshold(int n) {

        if (n == 0) return 0;

        // Choose random unit sq. position
        double x = Math.random(),
                y = Math.random();

        // Map to image position
        x *= targetImage.getWidth();
        y *= targetImage.getHeight();



        // Select random transform
        IFSTransform selTransform = RandomTransformSelector.chooseWithWeight(transforms);

        // Compute p'



        return runIFSTransformWithThreshold(n--);
    }

    // Read data from given file
    private IFSTransformList readIFSFile(File sourceIFSFile) {

        IFSTransformList transforms = new IFSTransformList();                                       // Transform collection

        try {

            BufferedReader br = new BufferedReader(new FileReader(sourceIFSFile));                  // Create new reader for given file
            String transformDescLine;                                                               // Read each line as one transformation description

            Scanner sc;                                                                             // Scanner for reading each line
            double[] tValues = new double[7];                                                       // Array for values, up to prob
            int pos;                                                                                // Index of current value pos
            int fileFormat = 0;                                                                     // Format of filetype
                                                                                                    // 0 - No given probabilities, just matricies
                                                                                                    // 1 - Probability of selection given for each matrix @ end of line

            while((transformDescLine = br.readLine()) != null) {

                sc = new Scanner(transformDescLine);                                                // Set scanner
                pos = 0;                                                                            // Reset position index

                while(sc.hasNextDouble()) {
                    tValues[pos++] = sc.nextDouble();                                               // Get all values from line
                }

                if (fileFormat == 0 && pos == 7) fileFormat = 1;                                    // Set filetype

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
                IFSTransform transform = new IFSTransform(tValues[0], tValues[2], tValues[1],
                                                tValues[3], tValues[4], tValues[5],
                                                tValues[6]);

                if (fileFormat == 1) transforms.transformWeightsSet = true; // We do not have to compute weights, they were added with file

                transforms.add(transform);                                  // Add transform to collection
            }


        }
        catch (FileNotFoundException e) {   // Handle file not found exception
            System.out.println("File not found for passed FILE object");
        }
        catch (IOException e) {             // Handle IO exception for reading lines
            System.out.println("Unable to read line from file, IOException encountered");
        }

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
    private float promptCountWithMessage(String prompt) {

        float count = -1;                                                                           // Result of capture parse
        String capture;                                                                             // Capture step count

        while(true) {
            try {
                capture = JOptionPane.showInputDialog(prompt);                                      // Display prompt for steps & capture value
                if(capture != null) {
                    if (capture.contains("0x")) {
                        count = (int) Long.parseLong( capture.substring(2, capture.length()), 16);  // Capture Hexcode as int
                    }
                    else count = Float.parseFloat(capture);                                         // Parse captured string as float
                }
            } catch (Exception e) {
                System.out.println("Failure to parse user input: " + e.getLocalizedMessage());      // Log error
                JOptionPane.showMessageDialog(this, "Please enter a number!");                      // Notify user of error
                continue;                                                                           // Display prompt again after error
            }
            break;      // Break out of pane if size was successfully set or user cancelled the pane
        }

        return count;   // Return parsed step count
    }

}

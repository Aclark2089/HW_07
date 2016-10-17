import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
    private BufferedImage source_image, target_image;   // Source and Target images
    private Graphics2D target_graphics;                 // Target graphics2d object

    // Colors & color masks
    private int color_background = 0xff000000,
                red_mask = 0x00ff0000,
                green_mask = 0x0000ff00,
                blue_mask = 0x000000ff;

    // Constructor
    public ImageFrame(int width, int height) {

        this.setTitle("CAP3027 2016 -- HW07 -- R. Alex Clark");        // Frame Title
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

    void fillSquare(Graphics2D target_graphics, BufferedImage source_image, float x, float y, float width, float height) {

        // Compute color average for given start x,y over span WxL
        Color avg_c = computeAvgPixColor(source_image, x, x + width, y, y + height);
        // Draw circle using given graphics object from given start x,y with radius sqrt(W*L / PI) and average section color
        drawCirlce(target_graphics, x, y, width, height, avg_c);

        // If we can keep going down in circle size depth, recursively call function to continue to next layer
        if ((width/2) > 2) {
            fillSquare(target_graphics, source_image, x, y, width/2, height/2);                             // Top Left
            fillSquare(target_graphics, source_image, x + (width/2), y, width/2, height/2);                 // Top Right
            fillSquare(target_graphics, source_image, x, y + (height/2), width/2, height/2);                // Bottom Left
            fillSquare(target_graphics, source_image, x + (width/2), y + (height/2), width/2, height/2);    // Bottom Right
        }

    }

    // Compute the average pixel color of given quadrant
    private Color computeAvgPixColor(BufferedImage image, float xmin, float xmax, float ymin, float ymax) {

        int r, g, b, ARGB = 0;
        float r_value = 0, b_value = 0, g_value = 0, i, j, size = 0;

        for (i = ymin; i < ymax; i++) {
            for (j = xmin; j < xmax; j++) {
                ARGB = image.getRGB((int) j, (int) i);      // Capture each channel
                r_value += ((ARGB & red_mask) >>> 16);
                g_value += ((ARGB & green_mask) >>> 8);
                b_value += (ARGB & blue_mask);
                size++;
            }
        }

        // Average float values
        r_value /= size;
        g_value /= size;
        b_value /= size;

        // Clamp average values
        r = clampValue((int) r_value);
        g = clampValue((int) g_value);
        b  = clampValue((int) b_value);

        // Return new avg color
        return new Color(r,  g, b);

    }

    // Draw circle with given graphics object over given coords with given color object
    private void drawCirlce(Graphics2D target_graphics, float x, float y, float width, float height, Color avg_color) {
        Shape circle = new Ellipse2D.Float(x, y, width, height);
        target_graphics.setColor(avg_color);
        target_graphics.draw(circle);
        target_graphics.fill(circle);
    }

    // Set passed buffered image background pixel color
    private void setupSimulationBufferedImage(BufferedImage image) {
        for(int i = 0, j; i < image.getHeight(); i++)
            for (j = 0; j < image.getWidth(); j++)
                image.setRGB(i, j, color_background);
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

        if (src_img != null) src_img = performImgCrop(src_img); // Crop image to equal LxW
        return src_img;                                         // Return image
    }

    // Crop the given image to the correct size
    private BufferedImage performImgCrop(BufferedImage src_img) {

        BufferedImage sub_img;                                                          // Sub image for cropping to square
        int width = src_img.getWidth(), height = src_img.getHeight();                   // Get src_img size attrs

        // Perform crop based on largest size attr, prefer smaller
        if (width > height) sub_img = src_img.getSubimage(0, 0, height, height);        // Crop width
        else if (width < height) sub_img = src_img.getSubimage(0, 0, width, width);     // Crop height
        else sub_img = src_img;                                                         // Equal, do nothing

        System.out.println("Reported cropped image size values of: " + sub_img.getWidth() + " " + sub_img.getHeight());

        return sub_img;                                                                 // Return new sub image
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

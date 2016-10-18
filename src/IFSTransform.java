import java.awt.geom.AffineTransform;

/**
 * Created by Alex on 10/18/16.
 */
public class IFSTransform extends AffineTransform {

    private double probability = 0;                             // Transform's probability of selection
    public boolean hasProbability = false;                      // Flag for probability

    public IFSTransform(double m00, double m10, double m01,
                        double m11, double m02, double m12,
                        double prob) {

        super(m00, m10, m01, m11, m02, m12);                    // Call super constructor for AT

        if (prob > 0) {
            this.hasProbability = true;                         // Set probability flag
            this.probability = prob;                            // Set probability value
        }

    }

    // Get probability
    public double getProbability() {
        return probability;
    }

}

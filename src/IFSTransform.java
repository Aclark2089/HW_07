import java.awt.geom.AffineTransform;

/**
 * Created by Alex on 10/18/16.
 */
public class IFSTransform extends AffineTransform {

    private double detWeight = 0;                             // Transform's determinant weight of selection

    public IFSTransform(double m00, double m10, double m01,
                        double m11, double m02, double m12,
                        double detWeight) {
        super(m00, m10, m01, m11, m02, m12);                    // Call super constructor for AT
        if (detWeight < 1) detWeight = 1;                       // Minimum determinant weight must be 1
        this.detWeight = detWeight;                             // Set detWeight value
    }

    // Get determinant weight
    public double getDetWeight() {
        return detWeight;
    }

    // Set determinant weight
    public void setDetWeight(double detWeight) {
        this.detWeight = detWeight;
    }
}

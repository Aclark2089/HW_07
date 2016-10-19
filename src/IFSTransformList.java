import java.util.ArrayList;

/**
 * Created by Alex on 10/19/2016.
 */
public class IFSTransformList extends ArrayList<IFSTransform> {

    public boolean transformWeightsSet = false;

    private double getSumOfDeterminants() {

        double detSum = 0;          // Sum of determinant values

        for(IFSTransform t : this) {
            detSum += Math.abs(t.getDeterminant());
        }

        return detSum;
    }

    public void assignDeterminantWeights() {

        double detSum = getSumOfDeterminants();                 // Get determinant total
        double detWeight = 0;

        for(IFSTransform t : this) {
            detWeight = t.getDeterminant() / detSum;    // Compute ratio of this transform determinant to total
            if (detWeight < 1) detWeight = 1;           // Minimum determinant weight is 1%
            t.setDetWeight(detWeight);                  // Set % of weight for current determinant
        }

        // Reflect and fudge weights to equal 100%

    }

}

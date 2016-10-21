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

        double
                detSum = getSumOfDeterminants(),                 // Get determinant total
                detWeight = 0,                                  // Weight of current transform
                weightTotal = 0;

        int minimumCounter = 0;

        for(IFSTransform t : this) {

            detWeight = (Math.abs(t.getDeterminant()) / detSum) * 100;    // Compute ratio of this transform determinant to total out of 100
            detWeight = Math.floor(detWeight);
            if (detWeight < 1)  {
                detWeight = 1;                      // Minimum determinant weight is 1%
                minimumCounter++;                   // Count number of minimum values
            }

            weightTotal += detWeight;
            t.setDetWeight(detWeight);                          // Set % of weight for current determinant

        }

        // Fudge weights by adding or subtracting from the non minimum values a gradient
        if (weightTotal != 100) {

            double weightDiff = Math.abs(weightTotal - 100);
            double weightDiffGradient = weightDiff / (this.size() - minimumCounter);

            if (weightTotal > 100) weightDiffGradient *= -1;
            weightTotal = 0;

            for(IFSTransform t : this) {
                if (t.getDetWeight() > 1) t.setDetWeight(t.getDetWeight() + weightDiffGradient);
                weightTotal += t.getDetWeight();
            }
        }

    }

}

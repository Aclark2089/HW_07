/**
 * Created by Alex on 10/19/16.
 */
public class RandomTransformSelector {

    public static IFSTransform chooseWithWeight(IFSTransformList transforms) {

        double totalWeight = 100,
                r = Math.floor(Math.random() * totalWeight),
                selectionWeight = 0;

        for (IFSTransform t : transforms) {
            selectionWeight += t.getDetWeight();
            if (selectionWeight >= r) return t;
        }

        throw new RuntimeException("Could not select transform");

    }

}

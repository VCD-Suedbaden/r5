package com.conveyal.r5.rastercost;

import com.conveyal.r5.streets.EdgeStore;
import org.apache.commons.math3.util.FastMath;

/**
 * Created by abyrd on 2021-07-20
 */
public class ToblerCalculator implements ElevationCostField.ElevationSegmentConsumer {

    private double weightedToblerSum = 0;
    private double xDistanceConsumed = 0;

    @Override
    public void consumeElevationSegment (int index, double xMeters, double yMeters) {
        weightedToblerSum += xMeters * tobler(xMeters, yMeters);
        xDistanceConsumed += xMeters;
    }

    public double getDistanceConsumed () {
        return xDistanceConsumed;
    }

    public double weightedToblerAverage() {
        return weightedToblerSum / xDistanceConsumed;
    }

    /**
     * Tobler's hiking function, normalized to 1 rather than 5 on flat ground so results can scale user-specified speeds.
     * The function peaks at 6 on a slight downgrade, so we apply the factor 6/5 or 1.2.
     * Elevation points are evenly spaced. We can store average normalized speeds over the linestring for an edge.
     * See: https://en.wikipedia.org/wiki/Tobler%27s_hiking_function
     * Also: https://wildfiretoday.com/documents/Slope_travel_rates.pdf
     */
    public static double tobler (double dx, double dy) {
        return 1.2 * FastMath.exp(-3.5 * FastMath.abs((dy/dx) + 0.05));
    }

}
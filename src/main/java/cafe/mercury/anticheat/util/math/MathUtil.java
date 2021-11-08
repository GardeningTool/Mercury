package cafe.mercury.anticheat.util.math;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Collection;

/**
 *
 * Majority of these methods come from Nemesis / Baldr
 * Credit: @sim0n
 */
@UtilityClass
public class MathUtil {

    public double hypot(double... values) {
        AtomicDouble squaredSum = new AtomicDouble(0D);

        Arrays.stream(values).forEach(value -> squaredSum.getAndAdd(Math.pow(value, 2D)));

        return Math.sqrt(squaredSum.get());
    }

    /**
     * Calculates the average (mean) of {@param values}
     * @param values The number values
     * @return The average (mean) of {@param values}
     */
    public double getAverage(Collection<? extends Number> values) {
        return values.stream()
                .mapToDouble(Number::doubleValue)
                .average()
                .orElse(0D);
    }

    /**
     * Calculates the clicks per second (CPS) from the average of {@param values}
     * @param values The number values
     * @return The average clicks per second (CPS) from {@param values}
     */
    public double getCPS(Collection<? extends Number> values) {
        return 20 / getAverage(values);
    }

    /**
     * Calculates the standard deviation of {@param values}
     * @param values The input values
     * @return The standard deviation of {@param values}
     */
    public double getStandardDeviation(Collection<? extends Number> values) {
        double average = getAverage(values);

        AtomicDouble variance = new AtomicDouble(0D);

        values.forEach(delay -> variance.getAndAdd(Math.pow(delay.doubleValue() - average, 2D)));

        return Math.sqrt(variance.get() / values.size());
    }

    /**
     * Calculates the kurtosis of {@param values}
     * @param values The number values
     * @return The kurtosis of {@param values}
     */
    public double getKurtosis(Collection<? extends Number> values) {
        double n = values.size();

        if (n < 3)
            return Double.NaN;

        double average = getAverage(values);
        double stDev = getStandardDeviation(values);

        AtomicDouble accum = new AtomicDouble(0D);

        values.forEach(delay -> accum.getAndAdd(Math.pow(delay.doubleValue() - average, 4D)));

        return n * (n + 1) / ((n - 1) * (n - 2) * (n - 3)) *
                (accum.get() / Math.pow(stDev, 4D)) - 3 *
                Math.pow(n - 1, 2D) / ((n - 2) * (n - 3));
    }

}

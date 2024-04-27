package org.example;

import java.util.List;

public class AverageCalculator {
    public static double calculateAverage(List<Double> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            throw new IllegalArgumentException("The list of numbers must not be null or empty");
        }

        double sum = 0.0;
        for (Double number : numbers) {
            sum += number;
        }

        return sum / numbers.size();
    }
}

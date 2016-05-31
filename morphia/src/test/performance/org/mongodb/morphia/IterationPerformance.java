package org.mongodb.morphia;

import org.junit.Test;
import org.mongodb.morphia.utils.IterHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IterationPerformance {
    private static final double NUMBER_OF_ITERATIONS = 10_000;

    public List<String> listToPutValuesInSoOperationIsNotOptimisedAway = new ArrayList<String>();

    @Test
    public void performanceOfAnonymousInnerClass() {
        //warmup
        int numberOfElements = 1000;
        System.out.printf("Number Of Elements: %d%n", numberOfElements);
        System.out.println("Warming Up...");
        warmup(NUMBER_OF_ITERATIONS, numberOfElements);
        listToPutValuesInSoOperationIsNotOptimisedAway = new ArrayList<String>();

        //execution
        System.out.println("Starting Test...");
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            decodeWithAnonymousInnerClass(numberOfElements);
        }
        long endTime = System.currentTimeMillis();

        //results
        long timeTaken = endTime - startTime;
        System.out.printf("Time Taken: %d millis%n", timeTaken);
        System.out.printf("Output: %d%n", listToPutValuesInSoOperationIsNotOptimisedAway.size());
        System.out.printf("Mean time taken: " + timeTaken / NUMBER_OF_ITERATIONS);
    }

    private void warmup(double numberOfIterations, int numberOfElements) {
        for (int i = 0; i < numberOfIterations; i++) {
            decodeWithAnonymousInnerClass(numberOfElements);
        }
        System.gc();
        System.gc();
    }

    @Test
    public void performanceOfLambda() {
        //warmup
        int numberOfElements = 1000;
        System.out.printf("Number Of Elements: %d%n", numberOfElements);
        System.out.println("Warming Up...");
        warmupLambda(NUMBER_OF_ITERATIONS, numberOfElements);
        listToPutValuesInSoOperationIsNotOptimisedAway = new ArrayList<String>();

        //execution
        System.out.println("Starting Test...");
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            decodeWithLambda(numberOfElements);
        }
        long endTime = System.currentTimeMillis();

        //results
        long timeTaken = endTime - startTime;
        System.out.printf("Time Taken: %d millis%n", timeTaken);
        System.out.printf("Output: %d%n", listToPutValuesInSoOperationIsNotOptimisedAway.size());
        System.out.printf("Mean time taken: " + timeTaken / NUMBER_OF_ITERATIONS);
    }

    private void warmupLambda(double numberOfIterations, int numberOfElements) {
        for (int i = 0; i < numberOfIterations; i++) {
            decodeWithAnonymousInnerClass(numberOfElements);
        }
        System.gc();
        System.gc();
    }

    @SuppressWarnings("Convert2Lambda")
    public Object decodeWithAnonymousInnerClass(int numberOfValues) {
        final List<String> values = initialiseArray(numberOfValues);
        new IterHelper<Long, String>().loopMap(values, new IterHelper.MapIterCallback<Long, String>() {
            @Override
            public void eval(final Long key, final String value) {
                listToPutValuesInSoOperationIsNotOptimisedAway.add(value);
            }
        });

        return values;
    }

    public Object decodeWithLambda(int numberOfValues) {
        final List<String> values = initialiseArray(numberOfValues);
        new IterHelper<Long, String>().loopMap(values, (key, value) -> listToPutValuesInSoOperationIsNotOptimisedAway.add(value));

        return values;
    }

    private List<String> initialiseArray(final int numberOfValues) {
        final List<String> result = new ArrayList<>(numberOfValues);
        for (int i = 0; i < numberOfValues; i++) {
            result.add(String.valueOf(i));
        }
        return result;
    }
}

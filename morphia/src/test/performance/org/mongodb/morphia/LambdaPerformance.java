package org.mongodb.morphia;

import com.mechanitis.testing.Repeat;
import com.mechanitis.testing.RepeatRule;
import org.junit.Rule;
import org.junit.Test;
import org.mongodb.morphia.utils.IterHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LambdaPerformance {
    private static final int NUMBER_OF_ITERATIONS = 10_000;
    private static final int NUMBER_OF_VALUES = 1000;

    @Rule
    public RepeatRule repeatRule = new RepeatRule();

//    @SuppressWarnings("WeakerAccess")
    public List<String> listOfResults = new ArrayList<>(NUMBER_OF_VALUES);
    public String[] arrayOfResults = new String[NUMBER_OF_VALUES];

    @Test
    @Repeat(times = 100)
    public void performanceOfAnonymousInnerClass() {
        //warmup
//                System.out.printf("Number Of Elements: %d%n", NUMBER_OF_VALUES);
//        System.out.println("Warming Up...");
        final Map<Long, String> values = initialiseMap(NUMBER_OF_VALUES);
        warmup(NUMBER_OF_ITERATIONS, values);

        //execution
//        System.out.println("Starting Test...");
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            decodeWithAnonymousInnerClass(values);
        }
        long endTime = System.currentTimeMillis();

        //results
        long timeTaken = endTime - startTime;
        System.out.println(timeTaken);
//        System.out.printf("Output: %d%n", listOfResults.length);
//        System.out.printf("Mean time taken: " + timeTaken / NUMBER_OF_ITERATIONS);
    }

    private void warmup(double numberOfIterations, Map<Long, String> values) {
        for (int i = 0; i < numberOfIterations; i++) {
            decodeWithAnonymousInnerClass(values);
        }
        System.gc();
        System.gc();
    }

    @Test
    @Repeat(times = 100)
    public void performanceOfLambda() {
        //warmup
        int numberOfElements = 1000;
//        System.out.printf("Number Of Elements: %d%n", numberOfElements);
//        System.out.println("Warming Up...");
        final Map<Long, String> values = initialiseMap(numberOfElements);
        warmupLambda(NUMBER_OF_ITERATIONS, values);
//        listOfResults = new ArrayList<>();

        //execution
//        System.out.println("Starting Test...");
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            decodeWithLambda(values);
        }
        long endTime = System.currentTimeMillis();

        //results
        long duration = endTime - startTime;
        System.out.println(duration);
        long throughput = NUMBER_OF_ITERATIONS/duration;
        System.out.println("throughput:"+ throughput+"ops/ms");
//        System.out.printf("Output: %d%n", listOfResults[54]);
//        System.out.printf("Mean time taken: " + timeTaken / NUMBER_OF_ITERATIONS);
    }

    private void warmupLambda(double numberOfIterations, Map<Long, String> values) {
        for (int i = 0; i < numberOfIterations; i++) {
            decodeWithLambda(values);
        }
        System.gc();
        System.gc();
    }

    @SuppressWarnings("Convert2Lambda")
    private void decodeWithAnonymousInnerClass(Map values) {
        new IterHelper<Long, String>().loopMap(values, new IterHelper.MapIterCallback<Long, String>() {
            @Override
            public void eval(final Long key, final String value) {
//                System.out.println("key = [" + key + "], value = [" + value + "]");
                arrayOfResults[key.intValue()] = value;
            }
        });
    }

    // NOTE: add (key,value) does an array copy and is SLOW!!

    private void decodeWithLambda(Map values) {
        new IterHelper<Long, String>().loopMap(values,
                                               (key, value) ->
                                                       arrayOfResults[key.intValue()] = value) ;
    }

    @SuppressWarnings("UnnecessaryBoxing")
    private Map<Long, String> initialiseMap(final int numberOfValues) {
        final Map<Long, String> result = new HashMap<>(numberOfValues);
        for (int i = 0; i < numberOfValues; i++) {
            result.put(Long.valueOf(i), String.valueOf(i));
        }
        return result;
    }
}

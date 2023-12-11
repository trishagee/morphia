package dev.morphia.test.aggregation.expressions;

import dev.morphia.test.aggregation.AggregationTest;

import org.testng.annotations.Test;

import static dev.morphia.aggregation.expressions.AccumulatorExpressions.bottomN;
import static dev.morphia.aggregation.expressions.ArrayExpressions.array;
import static dev.morphia.aggregation.expressions.ComparisonExpressions.eq;
import static dev.morphia.aggregation.expressions.ConditionalExpressions.condition;
import static dev.morphia.aggregation.expressions.Expressions.document;
import static dev.morphia.aggregation.expressions.Expressions.field;
import static dev.morphia.aggregation.expressions.Expressions.value;
import static dev.morphia.aggregation.stages.Group.group;
import static dev.morphia.aggregation.stages.Group.id;
import static dev.morphia.aggregation.stages.Match.match;
import static dev.morphia.query.Sort.descending;
import static dev.morphia.query.filters.Filters.eq;
import static dev.morphia.test.ServerVersion.v52;

public class TestBottomN extends AggregationTest {
    @Test
    public void testExample2() {
        testPipeline(v52, false, false, (aggregation) -> aggregation
                .pipeline(
                        match(eq("gameId", "G1")),
                        group(id(field("gameId")))
                                .field("playerId", bottomN(
                                        value(3),
                                        array(field("playerId"), field("score")),
                                        descending("score")))));
    }

    @Test
    public void testExample3() {
        testPipeline(v52, false, false, (aggregation) -> aggregation.pipeline(
                group(id(field("gameId")))
                        .field("playerId", bottomN(
                                value(3),
                                array(field("playerId"), field("score")),
                                descending("score")))));
    }

    @Test
    public void testExample4() {
        testPipeline(v52, false, false, (aggregation) -> aggregation.pipeline(
                group(id(document("gameId", field("gameId"))))
                        .field("gamescores", bottomN(
                                condition(
                                        eq(field("gameId"), value("G2")),
                                        value(1),
                                        value(3)),
                                field("score"),
                                descending("score")))));

    }

}

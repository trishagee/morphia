package dev.morphia.test.aggregation.stages;

import com.github.zafarkhaja.semver.Version;

import dev.morphia.test.ServerVersion;
import dev.morphia.test.aggregation.AggregationTest;

import org.testng.annotations.Test;

import static dev.morphia.aggregation.expressions.AccumulatorExpressions.sum;
import static dev.morphia.aggregation.expressions.Expressions.document;
import static dev.morphia.aggregation.stages.Documents.documents;
import static dev.morphia.aggregation.stages.Group.group;
import static dev.morphia.aggregation.stages.Group.id;
import static dev.morphia.aggregation.stages.Set.set;
import static dev.morphia.aggregation.stages.Sort.sort;
import static dev.morphia.aggregation.stages.UnionWith.unionWith;

public class TestUnionWith extends AggregationTest {
    @Test
    public void testExample1() {
        loadData("sales_2018", 2);
        loadData("sales_2019", 3);
        loadData("sales_2020", 4);
        testPipeline(ServerVersion.ANY, false, true, (aggregation) -> aggregation.pipeline(
                set().field("_id", "2017"),
                unionWith("sales_2018",
                        set().field("_id", "2018")),
                unionWith("sales_2019",
                        set().field("_id", "2019")),
                unionWith("sales_2020",
                        set().field("_id", "2020")),
                sort().ascending("_id", "store", "item")));
    }

    @Test
    public void testExample2() {
        loadData("sales_2018", 2);
        loadData("sales_2019", 3);
        loadData("sales_2020", 4);
        testPipeline(ServerVersion.ANY, false, true, (aggregation) -> aggregation.pipeline(
                unionWith("sales_2018"),
                unionWith("sales_2019"),
                unionWith("sales_2020"),
                group(id("$item"))
                        .field("total", sum("$quantity")),
                sort().descending("total")));
    }

    @Test
    public void testExample3() {
        assumeTrue(serverIsAtLeastVersion(Version.of(6)), "Minimum server version is 6");
        testPipeline(ServerVersion.ANY, false, true, (aggregation) -> aggregation.pipeline(
                unionWith(
                        documents(
                                document("_id", 4)
                                        .field("flavor", "orange"),
                                document("_id", 5)
                                        .field("flavor", "vanilla")
                                        .field("price", 20)))));
    }

}

package dev.morphia.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.lang.NonNull;
import com.mongodb.lang.Nullable;

import dev.morphia.aggregation.Aggregation;
import dev.morphia.aggregation.AggregationImpl;
import dev.morphia.config.MorphiaConfig;
import dev.morphia.mapping.codec.reader.DocumentReader;
import dev.morphia.query.FindOptions;
import dev.morphia.query.MorphiaQuery;

import org.bson.BsonInvalidOperationException;
import org.bson.Document;
import org.bson.codecs.DecoderContext;
import org.bson.json.JsonParseException;
import org.bson.json.JsonWriterSettings;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static java.lang.Character.toLowerCase;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.bson.json.JsonWriterSettings.builder;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

public abstract class TemplatedTestBase extends TestBase {
    private static final Logger LOG = LoggerFactory.getLogger(TemplatedTestBase.class);

    public static final JsonWriterSettings JSON_WRITER_SETTINGS = builder()
            .indent(true)
            .build();

    protected static final String AGG_TEST_COLLECTION = "aggtest";

    private static final Pattern FIND = Pattern.compile("^db\\.(\\w+)\\.find\\(.*$");

    protected final ObjectMapper mapper = new ObjectMapper();
    protected boolean skipActionCheck = false;
    @Deprecated
    protected boolean skipDataCheck = false;

    public TemplatedTestBase() {
    }

    public TemplatedTestBase(MorphiaConfig config) {
        super(config);
    }

    @NotNull
    public static File rootToCore(String path) {
        return new File(CORE_ROOT, path);
    }

    @BeforeMethod
    public void resetSkip() {
        skipActionCheck = false;
    }

    public final String prefix() {
        String root = getClass().getSimpleName().replace("Test", "");
        return toLowerCase(root.charAt(0)) + root.substring(1);
    }

    @Deprecated
    public void skipDataCheck() {
        skipDataCheck = true;
    }

    public void skipActionCheck() {
        skipActionCheck = true;
    }

    @AfterClass
    public void testCoverage() {
        var type = getClass();
        var methods = stream(type.getDeclaredMethods())
                .filter(m -> m.getName().startsWith("testExample"))
                .map(m -> {
                    String name = m.getName().substring(4);
                    return toLowerCase(name.charAt(0)) + name.substring(1);
                })
                .toList();
        String path = type.getPackageName();
        String simpleName = type.getSimpleName().substring(4);
        var operatorName = toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
        var resourceFolder = TemplatedTestBase.rootToCore("src/test/resources/%s/%s".formatted(path.replace('.', '/'), operatorName));

        if (!resourceFolder.exists()) {
            throw new IllegalStateException("%s does not exist inside %s".formatted(resourceFolder,
                    new File(".").getAbsolutePath()));
        }
        List<File> list = Arrays.stream(resourceFolder.list())
                .map(s -> new File(resourceFolder, s))
                .toList();

        List<String> examples = list.stream()
                .filter(d -> new File(d, "action.json").exists())
                .map(File::getName)
                .toList();
        var missing = examples.stream()
                .filter(example -> !methods.contains(example))
                .collect(joining(", "));
        if (!missing.isEmpty()) {
            fail("Missing test cases for $%s: %s".formatted(operatorName, missing));
        }
    }

    protected static String toString(List<Document> actual, String prefix) {
        return actual.stream()
                .map(c -> c.toJson(JSON_WRITER_SETTINGS))
                .collect(joining("\n\t", prefix, ""));
    }

    @Deprecated
    public <D> void testQuery(MorphiaQuery<D> query, FindOptions options, boolean orderMatters) {
        var resourceName = discoverResourceName();

        loadData(resourceName, getDs().getCollection(query.getEntityClass()).getNamespace().getCollectionName());

        List<D> actual = runQuery(resourceName, query, options);

        List<D> expected = map(query.getEntityClass(), loadExpected(resourceName));

        if (orderMatters) {
            assertEquals(actual, expected);
        } else {
            assertListEquals(actual, expected);
        }
    }

    protected void loadData(String resourceName, String collection) {
        if (!skipDataCheck) {
            insert(collection, loadJson(format("%s/%s/data.json", prefix(), resourceName), "data", true));
        }
    }

    protected void loadData(String collection, int index) {
        if (!skipDataCheck) {
            insert(collection, loadJson(format("%s/%s/data%d.json", prefix(), discoverResourceName(), index), "data", true));
        }
    }

    protected void loadData(String resourceName, String collection, int index) {
        if (!skipDataCheck) {
            insert(collection, loadJson(format("%s/%s/data%d.json", prefix(), resourceName, index), "data", true));
        }
    }

    protected void loadIndex(String resourceName, String collectionName) {
        final StackTraceElement[] stackTrace = new Exception().getStackTrace();
        MongoCollection<Document> collection = getDatabase().getCollection(collectionName);
        List<Document> documents = loadJson("%s/%s/index.json".formatted(prefix(), resourceName), "index", false);
        documents.forEach(document -> {
            collection.createIndex(document);
        });
    }

    protected @NotNull List<Document> loadExpected(String resourceName) {
        return loadJson("%s/%s/expected.json".formatted(prefix(), resourceName), "expected", true);
    }

    protected @NotNull <T> List<T> loadExpected(Class<T> type, String resourceName) {
        return loadJson(type, format("%s/%s/expected.json", prefix(), resourceName));
    }

    @NotNull
    protected List<Document> loadJson(String name, String type, boolean failOnMissing) {
        List<Document> data = new ArrayList<>();
        InputStream stream = getClass().getResourceAsStream(name);
        if (stream == null) {
            if (failOnMissing) {
                fail(format("missing " + type + " file: src/test/resources/%s/%s",
                        getClass().getPackageName().replace('.', '/'), name));
            }
        } else {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                while (reader.ready()) {
                    String json = reader.readLine();
                    try {
                        if (json.startsWith("[") && json.endsWith("]")) {
                            json = json.substring(1, json.length() - 1);
                        }
                        data.add(Document.parse(json));
                    } catch (JsonParseException e) {
                        throw new JsonParseException(e.getMessage() + "\n" + json, e);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return data;
    }

    @NotNull
    protected <T> List<T> loadJson(Class<T> type, String name) {
        List<T> data = new ArrayList<>();
        InputStream stream = getClass().getResourceAsStream(name);
        if (stream == null) {
            fail("missing data file: " + name);
        }
        ObjectMapper mapper = new ObjectMapper();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            while (reader.ready()) {
                data.add(mapper.readValue(reader.readLine(), type));
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return data;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected List<Document> runPipeline(String pipelineTemplate, Aggregation<Document> aggregation) {
        String pipelineName = format("%s/%s/action.json", prefix(), pipelineTemplate);
        List<Document> pipeline = ((AggregationImpl) aggregation).pipeline();

        if (!skipActionCheck) {
            List<Document> target = loadPipeline(pipelineName);
            assertEquals(toJson(pipeline), toJson(target), "Should generate the same pipeline");
        }

        if (!skipDataCheck) {
            try (var cursor = aggregation.execute(Document.class)) {
                return cursor.toList();
            }
        } else {
            return emptyList();
        }
    }

    private String toJson(List<Document> pipeline) {
        return pipeline.stream()
                .map(d -> d.toJson(JSON_WRITER_SETTINGS, getDatabase().getCodecRegistry().get(Document.class)))
                .collect(joining("\n, ", "[\n", "\n]"));
    }

    protected String toJson(Document document) {
        return document.toJson(JSON_WRITER_SETTINGS, getDatabase().getCodecRegistry().get(Document.class));
    }

    protected Document loadQuery(String pipelineName) {
        InputStream stream = getClass().getResourceAsStream(pipelineName);
        if (stream == null) {
            fail(format("missing data file: src/test/resources/%s/%s", getClass().getPackageName().replace('.', '/'),
                    pipelineName));
        }

        return parseAction(stream).get(0);
    }

    protected String loadTestName(String resourceName) {
        String name = format("%s/%s/name", prefix(), resourceName);

        InputStream stream = getClass().getResourceAsStream(name);
        if (stream == null) {
            fail(format("missing name file: %s", name));
        }

        try (var reader = new BufferedReader(new InputStreamReader(stream))) {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Document> loadPipeline(String pipelineName) {
        InputStream stream = getClass().getResourceAsStream(pipelineName);
        if (stream == null) {
            fail(format("missing data file: src/test/resources/%s/%s", getClass().getPackageName().replace('.', '/'),
                    pipelineName));
        }

        return parseAction(stream);
    }

    private List<Document> parseAction(InputStream stream) {
        List<String> list = new ArrayList<>(new BufferedReader(new InputStreamReader(stream))
                .lines()
                .map(String::trim)
                //                .map(line -> line.replaceAll("(\\$*\\w+?):", "\"$1\":"))
                .toList());
        unwrapArray(list);
        extractQueryFilters(list);

        var json = list.iterator();
        List<Document> stages = new ArrayList<>();
        String current = "";
        while (json.hasNext()) {
            while (current.isBlank() || !balanced(current)) {
                var next = json.next();
                if (!next.trim().isBlank()) {
                    current += next;
                }
            }
            try {
                stages.add(Document.parse(current));
            } catch (BsonInvalidOperationException e) {
                throw new BsonInvalidOperationException("Failed to parse:\n" + current, e);
            }
            current = "";
        }

        return stages;
    }

    private void extractQueryFilters(List<String> list) {
        String line = list.stream().collect(joining());
        if (FIND.matcher(line).matches()) {
            if (line.endsWith(")")) {
                line = line.substring(line.indexOf("(") + 1, line.lastIndexOf(")"));
                list.clear();
                list.add(line.trim());
            } else {
                throw new IllegalStateException("I don't know how to parse this line: \n\t" + line);
            }

        }

    }

    private static void unwrapArray(List<String> list) {
        String line = list.get(0);
        if (line.startsWith("[")) {
            line = line.substring(1);
            if (!line.isBlank()) {
                list.set(0, line);
            } else {
                list.remove(0);
            }
        }
        line = list.get(list.size() - 1);
        if (line.endsWith("]")) {
            line = line.substring(0, line.length() - 1);
            if (!line.isBlank()) {
                list.set(list.size() - 1, line);
            } else {
                list.remove(list.size() - 1);
            }
        }
    }

    private boolean balanced(String input) {
        int open = 0;
        int close = 0;
        for (char c : input.toCharArray()) {
            if (c == '{')
                open++;
            if (c == '}')
                close++;
        }

        return open == close;
    }

    @NonNull
    protected <D> List<D> runQuery(@NonNull String queryTemplate, @NonNull MorphiaQuery<D> query, @NonNull FindOptions options) {
        String queryName = format("%s/%s/query.json", prefix(), queryTemplate);
        try {

            InputStream stream = getClass().getResourceAsStream(queryName);
            assertNotNull(stream, "Could not find query template: " + queryName);
            Document expectedQuery;
            try (InputStreamReader reader = new InputStreamReader(stream)) {
                expectedQuery = Document.parse(new BufferedReader(reader).readLine());
            }

            assertDocumentEquals(query.toDocument(), expectedQuery);

            try (var cursor = query.iterator(options)) {
                return cursor.toList();
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected String discoverResourceName() {
        var method = findTestMethod();
        String methodName = method.getName();

        if (methodName.startsWith("test")) {
            methodName = methodName.substring(4);
            methodName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
        }
        return methodName;
    }

    protected Method findTestMethod() {
        return stream(new Exception().getStackTrace())
                .map(this::isTestMethod)
                .filter(Objects::nonNull)
                .findFirst()
                .get();
    }

    @Nullable
    private Method isTestMethod(StackTraceElement element) {
        try {
            Class<?> klass = Class.forName(element.getClassName());
            Method method = klass.getDeclaredMethod(element.getMethodName());

            return method.getAnnotation(Test.class) != null ? method : null;
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    private <D> List<D> map(Class<D> entityClass, List<Document> documents) {
        var codec = getDs().getCodecRegistry().get(entityClass);

        DecoderContext context = DecoderContext.builder().build();
        return documents.stream()
                .map(document -> {
                    return codec.decode(new DocumentReader(document), context);
                })
                .collect(toList());
    }
}

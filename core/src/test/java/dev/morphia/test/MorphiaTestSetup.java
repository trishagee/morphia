package dev.morphia.test;

import java.util.List;
import java.util.stream.Collectors;

import com.mongodb.client.MongoClient;

import dev.morphia.config.MorphiaConfig;
import dev.morphia.mapping.Mapper;
import dev.morphia.test.TestBase.ZDTCodecProvider;
import dev.morphia.test.config.ManualMorphiaTestConfig;
import dev.morphia.test.config.MorphiaTestConfig;

import org.semver4j.Semver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MongoDBContainer;
import org.testng.SkipException;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import static dev.morphia.test.TestBase.TEST_DB_NAME;
import static java.lang.String.format;
import static java.util.Arrays.stream;

@SuppressWarnings("removal")
public class MorphiaTestSetup {
    private static final Logger LOG = LoggerFactory.getLogger(MorphiaTestSetup.class);

    private static MongoHolder mongoHolder;

    private MorphiaContainer morphiaContainer;
    private MorphiaConfig morphiaConfig;

    public MorphiaTestSetup() {
        morphiaConfig = buildConfig()
                .codecProvider(new ZDTCodecProvider());
    }

    public MorphiaTestSetup(MorphiaConfig config) {
        morphiaConfig = config;
    }

    @BeforeSuite
    public MorphiaContainer getMorphiaContainer() {
        if (morphiaContainer == null) {
            morphiaContainer = new MorphiaContainer(getMongoHolder().getMongoClient(), morphiaConfig);
        }

        return morphiaContainer;
    }

    public MongoHolder getMongoHolder() {
        if (mongoHolder == null || !mongoHolder.isAlive()) {
            mongoHolder = initMongoDbContainer(false);
        }
        return mongoHolder;
    }

    private static MongoHolder initMongoDbContainer(boolean sharded) {
        String mongodb = System.getProperty("mongodb");
        String connectionString;
        MongoDBContainer mongoDBContainer = null;
        //        if ("local".equals(mongodb)) {
        LOG.info("'local' mongodb property specified. Using local server.");
        connectionString = "mongodb://localhost:27017/" + TEST_DB_NAME;
        //        } else {
        //            DockerImageName imageName;
        //            try {
        //                Versions match = mongodb == null
        //                        ? Versions.latest()
        //                        : Versions.bestMatch(mongodb);
        //                imageName = match.dockerImage();
        //            } catch (IllegalArgumentException e) {
        //                imageName = Versions.latest().dockerImage();
        //                LOG.error(format("Could not parse mongo docker image name.  using docker image %s.", imageName));
        //            }
        //
        //            LOG.info("Running tests using " + imageName);
        //            mongoDBContainer = new MongoDBContainer(imageName);
        //            if (sharded) {
        //                mongoDBContainer
        //                        .withSharding();
        //            }
        //            mongoDBContainer.start();
        //            connectionString = mongoDBContainer.getReplicaSetUrl(TEST_DB_NAME);
        //        }
        return new MongoHolder(mongoDBContainer, connectionString);
    }

    @AfterSuite
    public void stopContainer() {
        morphiaContainer = null;
    }

    protected void assumeTrue(boolean condition, String message) {
        if (!condition) {
            throw new SkipException(message);
        }
    }

    protected void checkMinDriverVersion(DriverVersion version) {
        String property = System.getProperty("driver.version");
        var driverVersion = property != null ? Semver.parse(property) : null;

        assumeTrue(driverVersion == null || driverVersion.isGreaterThanOrEqualTo(version.version()),
                format("Server should be at least %s but found %s", version, getServerVersion()));
    }

    protected void checkMinServerVersion(ServerVersion version) {
        assumeTrue(serverIsAtLeastVersion(version.version()),
                format("Server should be at least %s but found %s", version.version(), getServerVersion()));
    }

    protected MongoClient getMongoClient() {
        return getMongoHolder().getMongoClient();
    }

    protected Semver getServerVersion() {
        return morphiaContainer.getServerVersion();
    }

    protected boolean isReplicaSet() {
        return morphiaContainer.runIsMaster().get("setName") != null;
    }

    /**
     * @param version the minimum version allowed
     * @return true if server is at least specified version
     */
    protected boolean serverIsAtLeastVersion(Semver version) {
        return getServerVersion().isGreaterThanOrEqualTo(version);
    }

    protected void withSharding(Runnable body) {
        var oldHolder = mongoHolder;
        var oldContainer = morphiaContainer;
        try (var holder = initMongoDbContainer(true)) {
            mongoHolder = holder;
            morphiaContainer = new MorphiaContainer(mongoHolder.getMongoClient(), MorphiaConfig.load());
            body.run();
        } finally {
            mongoHolder = oldHolder;
            morphiaContainer = oldContainer;
        }
    }

    private void map(List<Class<?>> classes) {
        Mapper mapper = getMorphiaContainer().getDs().getMapper();
        classes.forEach(mapper::getEntityModel);
    }

    protected void withTestConfig(MorphiaConfig config, List<Class<?>> types, Runnable body) {
        withConfig(new ManualMorphiaTestConfig(config).classes(types), body);
    }

    protected void withTestConfig(List<Class<?>> types, Runnable body) {
        withTestConfig(buildConfig(), types, body);
    }

    protected void withConfig(MorphiaConfig config, Runnable body) {
        var oldContainer = morphiaContainer;
        try {
            morphiaContainer = new MorphiaContainer(mongoHolder.getMongoClient(), config);
            if (config instanceof MorphiaTestConfig testConfig) {
                List<Class<?>> classes = testConfig.classes();
                if (classes != null) {
                    getMorphiaContainer().getDs().getMapper().map(classes);
                }
                if (config.applyIndexes()) {
                    getMorphiaContainer().getDs().applyIndexes();
                }
            }
            body.run();
        } finally {
            morphiaContainer = oldContainer;
        }
    }

    protected static MorphiaConfig buildConfig(Class<?>... types) {
        MorphiaConfig config = new ManualMorphiaTestConfig()
                .database(TEST_DB_NAME);
        if (types.length != 0)
            config = config
                    .packages(stream(types)
                            .map(Class::getPackageName)
                            .collect(Collectors.toList()));
        return config;
    }
}

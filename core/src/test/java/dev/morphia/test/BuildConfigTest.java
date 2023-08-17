package dev.morphia.test;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.zafarkhaja.semver.Version;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import static java.lang.String.format;
import static java.util.List.of;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class BuildConfigTest {
    final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    @Test
    @Ignore
    public void testDocsConfig() throws IOException, XmlPullParserException {
        Version pomVersion = pomVersion();

        Map map = antora();
        Map gitInfo = gitProperties();

        String version = map.get("version").toString();
        boolean master = "master".equals(gitInfo.get("git.branch"));
        if (master) {
            assertEquals(map.get("prerelease"), "-SNAPSHOT");
        } else {
            assertNull(map.get("prerelease"));
        }

        assertEquals(version, format("%s.%s", pomVersion.getMajorVersion(), pomVersion.getMinorVersion()));
        map = walk(map, of("asciidoc", "attributes"));
        version = (String) map.get("version");
        var srcRef = (String) map.get("srcRef");
        if (master) {
            assertEquals(version, pomVersion.toString());
            assertTrue(srcRef.endsWith("/blob/master"));
        } else {
            var branch = format("%s.%s.x", pomVersion.getMajorVersion(), pomVersion.getMinorVersion());
            Version released = Version.forIntegers(pomVersion.getMajorVersion(), pomVersion.getMinorVersion(),
                    pomVersion.getPatchVersion() - 1);
            String format = format("/tree/%s", branch);
            assertTrue(srcRef.endsWith(format), String.format("Should end with %s but found %s", format, srcRef));
            assertEquals(version, released.toString());
        }
    }

    @NotNull
    private static Map gitProperties() throws IOException {
        Map gitInfo;
        try (InputStream inputStream = new FileInputStream("target/git.properties")) {
            var props = new Properties();
            props.load(inputStream);
            gitInfo = new TreeMap(props);
        }
        return gitInfo;
    }

    private Map antora() throws IOException {
        Map map;
        try (InputStream inputStream = new FileInputStream("../docs/antora.yml")) {
            map = objectMapper.readValue(inputStream, LinkedHashMap.class);
        }
        return map;
    }

    private <T> T walk(Map map, List<String> steps) {
        Object value = map;
        for (String step : steps) {
            if (value instanceof Map) {
                value = ((Map<?, ?>) value).get(step);
            }
        }
        return (T) value;
    }

    @NotNull
    private Version pomVersion() throws IOException, XmlPullParserException {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader("../pom.xml"));

        return Version.valueOf(model.getVersion());
    }
}
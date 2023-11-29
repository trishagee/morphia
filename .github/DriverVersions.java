///usr/bin/env jbang "$0" "$@" ; exit $?

//JAVA 17
//DEPS com.github.zafarkhaja:java-semver:0.9.0
//DEPS com.fasterxml.jackson.core:jackson-databind:2.15.2
//DEPS com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.15.2

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.github.zafarkhaja.semver.Version;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Comparator.comparingInt;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.StreamSupport.stream;

public class DriverVersions {

    public static void main(String... args) throws Exception {
        // props to Chris Dellaway for the pointer to this
        var url = "https://repo1.maven.org/maven2/org/mongodb/mongodb-driver-sync/maven-metadata.xml";
        var mapper = new XmlMapper();

        var document = mapper.readTree(new URL(url));

        var list = (ObjectNode) document.get("versioning");
        var latest = format("'%s'", list.get("latest").asText());
        var versions = list.get("versions").get("version");

        var result = stream(spliteratorUnknownSize(versions.elements(), Spliterator.ORDERED), false)
                         .map(JsonNode::asText)
                         .map(Version::valueOf)
                         .filter(it1 -> it1.lessThan(Version.valueOf("5.0.0")))
                         .filter(it1 -> it1.greaterThan(Version.valueOf("4.0.0")))
                         .filter(it1 -> it1.getBuildMetadata().isEmpty())
                         .filter(it1 -> it1.getPreReleaseVersion().isEmpty())
                         .sorted(comparingInt(Version::getMajorVersion)
                                     .thenComparingInt(Version::getMinorVersion)
                                     .thenComparingInt(Version::getPatchVersion)
                                     .reversed())
                         .collect(groupingBy(version -> format("%s.%s", version.getMajorVersion(), version.getMinorVersion())))
                         .values().stream()
                         .map(it -> it.get(0))
                         .map(it -> format("'%s'", it))
                         .collect(Collectors.toList());

        Collections.reverse(result);
        var map = Map.of("latest", List.of(latest), "versions", result);
        if (args.length != 0) {
            if (args[0].equals("all")) {
                System.out.println(map.get("versions"));
            } else {
                System.out.println(map.get("latest"));
            }
        } else {
            System.out.println(map.get("latest"));
            System.out.println(map.get("versions"));
            System.out.println(map);
        }
    }
}
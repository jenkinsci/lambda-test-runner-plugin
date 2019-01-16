package org.jenkinsci.plugins.lambdatestrunner.jenkins.outputs;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class BuildOutputExtractorTest {

    private final String source = "src/test/resources/build-output-extractor";
    private final String target = source + "-test";

    @Before
    public void setup() throws IOException {
        FileUtils.copyDirectory(new File(source), new File(target));
    }

    @After
    public void teardown() throws IOException {
        FileUtils.forceDeleteOnExit(new File(target));
    }

    @Test
    public void testUnpack() throws IOException {
        BuildOutputExtractor extractor = new BuildOutputExtractor(target);
        extractor.explode();

        String fileA = target + "/target-dir/failsafe-reports/a.xml";
        String fileB = target + "/target-dir/surefire-reports/b.xml";
        assertEquals(readFileToString(fileA), "<a>a</a>");
        assertEquals(readFileToString(fileB), "<b>b</b>");
    }

    private String readFileToString(String file) throws IOException {
        Path path = Paths.get(file);
        return Files.lines(path).collect(Collectors.joining("\n"));
    }
}

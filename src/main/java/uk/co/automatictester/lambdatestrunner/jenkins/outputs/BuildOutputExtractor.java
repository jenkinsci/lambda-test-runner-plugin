package uk.co.automatictester.lambdatestrunner.jenkins.outputs;

import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class BuildOutputExtractor {

    private String dir;

    public BuildOutputExtractor(String dir) {
        this.dir = dir;
    }

    public void explode() {
        List<String> zipFiles = getZipFiles();
        unpack(zipFiles);
        deleteFiles(zipFiles);
    }

    private List<String> getZipFiles() {
        try {
            return Files.walk(Paths.get(dir))
                    .map(p -> p.toAbsolutePath().toString())
                    .filter(s -> s.endsWith(".zip"))
                    .sorted()
                    .collect(toList());
        } catch (IOException e) {
            String message = String.format("Error processing dir '%s': %s", dir, e.getMessage());
            throw new RuntimeException(message);
        }
    }

    private void unpack(List<String> files) {
        files.forEach(file -> {
            String unpackToDir = new File(file).getParent();
            ZipUtil.unpack(new File(file), new File(unpackToDir));
        });
    }

    private void deleteFiles(List<String> files) {
        files.forEach(file -> {
            try {
                Files.delete(Paths.get(file));
            } catch (IOException e) {
                String message = String.format("Error deleting file '%s': %s", dir, e.getMessage());
                throw new RuntimeException(message);
            }
        });
    }
}

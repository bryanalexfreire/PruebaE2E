package com.screenplay.project.util.data;

import com.screenplay.project.util.logger.TestLogger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Build step (Gradle task {@code generateFeatures}): copies every feature file from the source
 * folder to the generated folder, writing the external test data into the {@code Examples}.
 * The source features stay untouched, so an interrupted run never leaves them modified.
 *
 * <p>Usage: {@code GenerateFeatures <source dir> <target dir>}</p>
 */
public final class GenerateFeatures {

    private GenerateFeatures() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: GenerateFeatures <source dir> <target dir>");
        }
        int written = generate(Path.of(args[0]), Path.of(args[1]));
        TestLogger.info("Generated " + written + " feature file(s) in " + args[1]);
    }

    /** Writes the generated features and returns how many feature files were written. */
    public static int generate(Path source, Path target) throws IOException {
        if (!Files.isDirectory(source)) {
            throw new IllegalArgumentException("Features folder not found: " + source);
        }
        try (Stream<Path> files = Files.walk(source)) {
            List<Path> features = files.filter(p -> p.toString().endsWith(".feature")).toList();
            for (Path feature : features) {
                List<String> lines = Files.readAllLines(feature, StandardCharsets.UTF_8);
                List<String> output = ExternalDataFeatures.hasDescriptor(lines)
                        ? ExternalDataFeatures.materialize(lines, ExcelReader::read)
                        : lines;
                Path destination = target.resolve(source.relativize(feature)).toAbsolutePath();
                Path folder = destination.getParent();
                if (folder != null) {
                    Files.createDirectories(folder);
                }
                Files.write(destination, output, StandardCharsets.UTF_8);
            }
            return features.size();
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }
}

package pl.wiktor.circle.domain.backup.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.wiktor.circle.domain.exception.ApplicationException;
import pl.wiktor.circle.domain.exception.ExceptionType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilesLookupHelper {
    private static final Logger logger = LoggerFactory.getLogger(FilesLookupHelper.class);
    private final String lookupLocation;
    private final String lookupFilesExtension;

    public FilesLookupHelper(String lookupLocation, String lookupFilesExtension) {
        this.lookupLocation = lookupLocation;
        this.lookupFilesExtension = lookupFilesExtension;
    }

    public List<File> resolveAllFilesExcept(List<String> ignoredFilenames) {
        Predicate<Path> ignoredFilenamesContains = path -> ignoredFilenames.stream()
                .filter(Objects::nonNull)
                .anyMatch(s -> path.toString().contains(s));
        try (Stream<Path> paths = Files.walk(Paths.get(lookupLocation))) {
            return paths
                    .filter(path -> Files.isRegularFile(path) && !ignoredFilenamesContains.test(path))
                    .map(Path::toFile)
                    .filter(o -> o.getAbsolutePath().contains(lookupFilesExtension))
                    .collect(Collectors.toList());
        } catch (NoSuchFileException ignored) {
            return Collections.emptyList();
        } catch (IOException e) {
            logger.error("Error during collecting local files", e);
            throw new ApplicationException(ExceptionType.LISTING_FILES_ERROR);
        }
    }

    public static void removeFile(File file) {
        if (file.delete()) {
            logger.info("File '{}' has been removed", file.getName());
        } else {
            logger.error("File '{}' hasn't been removed despite requirements", file.getName());
        }
    }
}

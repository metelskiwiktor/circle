package pl.wiktor.circle.domain.backup.utils;

import java.io.File;

@FunctionalInterface
public interface FilepathCreator {
    File filepath(String suffix);
}

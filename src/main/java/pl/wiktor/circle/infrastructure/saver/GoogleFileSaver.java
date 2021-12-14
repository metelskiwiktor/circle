package pl.wiktor.circle.infrastructure.saver;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.wiktor.circle.domain.backup.FileSaver;
import pl.wiktor.circle.domain.backup.FileStructure;
import pl.wiktor.circle.domain.exception.ApplicationException;
import pl.wiktor.circle.domain.exception.ExceptionType;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

public class GoogleFileSaver extends FileSaver {
    private static final Logger logger = LoggerFactory.getLogger(GoogleFileSaver.class);
    private final Drive drive;

    public GoogleFileSaver(Drive drive) {
        this.drive = drive;
    }

    @Override
    synchronized public void saveFile(java.io.File file, FileStructure structure) {
        logger.info("GoogleFileSaver executing save on '{}' file", file.getName());
        String monthFolderId = getLowestFolderId(structure);
        uploadFile(file, file.getName(), monthFolderId);
        logger.info("Successfully saved '{}' into Google Drive", file.getName());
    }

    private void uploadFile(java.io.File file, String name, String parent) {
        try {
            File fileMetadata = new File();
            fileMetadata.setName(name);
            fileMetadata.setParents(List.of(parent));
            FileContent mediaContent = new FileContent(Files.probeContentType(file.toPath()), file);
            createMIME(fileMetadata, mediaContent);
        } catch (IOException e) {
            logger.error("Exception during getting content type", e);
            throw new ApplicationException(ExceptionType.GOOGLE_DRIVE_API_FAIL, "błąd przy pobieraniu typu pliku");
        }
    }

    private String getLowestFolderId(FileStructure structure) {
        FileStructure currentLevel = structure;
        String lastId = null;
        while (Objects.nonNull(currentLevel)) {
            String folderId = getFolderId(currentLevel.getName(), currentLevel.getDescription());
            if (Objects.isNull(folderId)) {
                logger.info("Folder '{}' is not existing in Google Drive. Creating a new one.", currentLevel.getName());
                lastId = createFolder(currentLevel.getName(), currentLevel.getDescription(), lastId).getId();
            } else {
                lastId = folderId;
            }
            currentLevel = currentLevel.getNext();
        }
        return lastId;
    }

    private File createFolder(String name, String description, String parent) {
        File fileMetadata = new File();
        fileMetadata.setName(name);
        fileMetadata.setDescription(description);
        fileMetadata.setMimeType(GoogleMimeTypes.FOLDER.getName());
        if (Objects.nonNull(parent)) {
            fileMetadata.setParents(List.of(parent));
        }
        return createMIME(fileMetadata, null);
    }

    private String getFolderId(String name, String description) {
        try {
            String pageToken = null;
            do {
                FileList result = drive.files().list()
                        .setQ(GoogleMimeTypes.FOLDER.getQuery())
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id, name, description)")
                        .setPageToken(pageToken)
                        .execute();
                for (File file : result.getFiles()) {
                    if (file.getName().equals(name) && description.equals(file.getDescription())) {
                        return file.getId();
                    }
                }
                pageToken = result.getNextPageToken();
            } while (pageToken != null);

            return null;
        } catch (IOException e) {
            logger.error("Exception during listing existing mimes", e);
            throw new ApplicationException(ExceptionType.GOOGLE_DRIVE_API_FAIL, "błąd przy wyszukiwaniu folderu");
        }
    }

    private File createMIME(File fileMetadata, FileContent mediaContent) {
        try {
            if (Objects.isNull(mediaContent)) {
                return drive.files().create(fileMetadata)
                        .setFields("id, parents")
                        .execute();
            } else {
                return drive.files().create(fileMetadata, mediaContent)
                        .setFields("id, parents")
                        .execute();
            }
        } catch (IOException e) {
            logger.error("Exception during creating mime", e);
            throw new ApplicationException(ExceptionType.GOOGLE_DRIVE_API_FAIL, "błąd przy tworzeniu mime");
        }
    }
}

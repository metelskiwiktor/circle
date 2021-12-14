package pl.wiktor.circle.domain.backup;

import org.slf4j.LoggerFactory;
import pl.wiktor.circle.domain.exception.ApplicationException;

import java.io.File;

public abstract class FileSaver {
    static final String BACKUP_ROOT_FOLDER_NAME = "Circle backup";
    static final String BACKUP_ROOT_FOLDER_DESCRIPTION = "Stores backup for database and etc. " +
            "Managed by Circle Backend Application through Google Drive API.";
    static final String BACKUP_LEVEL_1_FOLDER_DESCRIPTION = "Folder '%s' stores backup for database and etc. " +
            "Managed by Circle Backend Application through Google Drive API.";
    static final String BACKUP_LEVEL_2_FOLDER_DESCRIPTION = "Folder '%s/%s' stores backup for database and etc. " +
            "Managed by Circle Backend Application through Google Drive API.";

    public void saveFile(File file, FileStructure structure) throws ApplicationException {
        LoggerFactory.getLogger(FileSaver.class)
                .warn("File hasn't been saved due to no provided implementation for FileSaver");
    }
}

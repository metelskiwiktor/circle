package pl.wiktor.circle.domain.backup;

import pl.wiktor.circle.domain.backup.utils.MonthToNameHelper;

import java.time.LocalDate;

import static pl.wiktor.circle.domain.backup.FileSaver.*;

public enum FolderType {
    ROOT {
        @Override
        String getFolderName(LocalDate localDate) {
            return BACKUP_ROOT_FOLDER_NAME;
        }

        @Override
        String getFolderDescription(LocalDate localDate) {
            return BACKUP_ROOT_FOLDER_DESCRIPTION;
        }
    }, LEVEL_1 {
        @Override
        String getFolderName(LocalDate localDate) {
            return String.valueOf(localDate.getYear());
        }

        @Override
        String getFolderDescription(LocalDate localDate) {
            return String.format(BACKUP_LEVEL_1_FOLDER_DESCRIPTION, localDate.getYear());
        }
    }, LEVEL_2 {
        @Override
        String getFolderName(LocalDate localDate) {
            return MonthToNameHelper.getPolishMonthName(localDate.getMonth());
        }

        @Override
        String getFolderDescription(LocalDate localDate) {
            return String.format(BACKUP_LEVEL_2_FOLDER_DESCRIPTION, LEVEL_1.getFolderName(localDate),
                    getFolderName(localDate));
        }
    };

    abstract String getFolderName(LocalDate localDate);

    abstract String getFolderDescription(LocalDate localDate);
}

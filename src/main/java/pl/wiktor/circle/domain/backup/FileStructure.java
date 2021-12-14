package pl.wiktor.circle.domain.backup;

import java.time.LocalDate;

public class FileStructure {
    private final FileStructure next;
    private final String name;
    private final String description;

    public FileStructure(FileStructure next, FolderType folderType, LocalDate localDate) {
        this.next = next;
        this.name = folderType.getFolderName(localDate);
        this.description = folderType.getFolderDescription(localDate);
    }

    public FileStructure getNext() {
        return next;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static FileStructure mapToFileStructure(LocalDate localDate) {
        FileStructure month = new FileStructure(null, FolderType.LEVEL_2, localDate);
        FileStructure year = new FileStructure(month, FolderType.LEVEL_1, localDate);
        return new FileStructure(year, FolderType.ROOT, localDate);
    }
}

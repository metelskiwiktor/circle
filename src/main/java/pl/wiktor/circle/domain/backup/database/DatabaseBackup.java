package pl.wiktor.circle.domain.backup.database;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class DatabaseBackup {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator",
            parameters = {
                    @Parameter(
                            name = "uuid_gen_strategy_class",
                            value = "org.hibernate.id.uuid.CustomVersionOneStrategy"
                    )
            }
    )
    private String id;
    private String fileName;
    private LocalDateTime backupTime;

    public DatabaseBackup() {
    }

    public DatabaseBackup(String fileName, LocalDateTime backupTime) {
        this.fileName = fileName;
        this.backupTime = backupTime;
    }

    public String getFileName() {
        return fileName;
    }

    public LocalDateTime getBackupTime() {
        return backupTime;
    }
}

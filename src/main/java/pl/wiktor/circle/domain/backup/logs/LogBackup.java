package pl.wiktor.circle.domain.backup.logs;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class LogBackup {
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
    private LocalDateTime backupTime;
    private String fileName;

    public LogBackup() {
    }

    public LogBackup(LocalDateTime backupTime, String fileName) {
        this.backupTime = backupTime;
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}

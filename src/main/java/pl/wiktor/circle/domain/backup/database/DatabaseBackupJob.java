package pl.wiktor.circle.domain.backup.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.wiktor.circle.domain.backup.FileSaver;
import pl.wiktor.circle.domain.backup.utils.FilepathCreator;
import pl.wiktor.circle.domain.backup.utils.FilesLookupHelper;

import java.io.File;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static pl.wiktor.circle.domain.backup.FileStructure.mapToFileStructure;
import static pl.wiktor.circle.domain.backup.utils.BackupMessageHelper.prepareTimeMessage;

@EnableScheduling
@Component
@ConditionalOnProperty(value = "project.env.db.backup-scheduler", havingValue = "true")
public class DatabaseBackupJob implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseBackupJob.class);
    private static final String ZIP_DATABASE_EXTENSION = ".zip";
    private static final int MINIMUM_MINUTES_TO_RETRY = 3 * 60;
    private static final DateTimeFormatter databaseSuffix = DateTimeFormatter.ofPattern("dd-MM-yy-HHmm");
    private final DatabaseBackupRepository databaseBackupRepository;
    private final boolean deleteAfterBackup;
    private final FileSaver fileSaver;
    private final Clock clock;
    private final FilepathCreator filepathCreator;

    public DatabaseBackupJob(DatabaseBackupRepository databaseBackupRepository, FileSaver fileSaver, Clock clock,
                             @Value("${project.env.db.delete-after-backup}") boolean deleteAfterBackup,
                             @Value("${project.env.db.location}") String dbLocation,
                             @Value("${project.env.db.name}") String databaseName) {
        this.databaseBackupRepository = databaseBackupRepository;
        this.fileSaver = fileSaver;
        this.clock = clock;
        this.deleteAfterBackup = deleteAfterBackup;
        this.filepathCreator = suffix -> new File(String.format("%s/%s-%s%s", dbLocation, databaseName, suffix,
                ZIP_DATABASE_EXTENSION));
    }

    @Scheduled(cron = "${project.env.db.cron-scheduler}")
    public void databaseBackupScheduler() {
        try {
            logger.info("Database backup scheduler running");
            LocalDateTime currentTime = LocalDateTime.ofInstant(clock.instant(), ZoneId.of("Europe/Paris"));
            int lastBackup = databaseBackupRepository.findAll().stream()
                    .map(backup -> Duration.between(currentTime, backup.getBackupTime()).abs().toMinutes())
                    .mapToInt(Math::toIntExact).min().orElse(MINIMUM_MINUTES_TO_RETRY);
            if (lastBackup < MINIMUM_MINUTES_TO_RETRY) {
                logger.info(prepareTimeMessage(lastBackup, MINIMUM_MINUTES_TO_RETRY));
                return;
            }
            File file = filepathCreator.filepath(createUniqueSuffix(currentTime));
            databaseBackupRepository.backupDB(file.getAbsolutePath());
            logger.info("Successfully created locally new backup of database '{}'", file.getName());
            fileSaver.saveFile(file, mapToFileStructure(currentTime.toLocalDate()));
            databaseBackupRepository.save(new DatabaseBackup(file.getName(), currentTime));
            if (deleteAfterBackup) {
                FilesLookupHelper.removeFile(file);
            }
            logger.info("Database backup scheduler finished");
        } catch (Exception e) {
            logger.error("Database backup error occurred: ", e);
        }
    }

    private String createUniqueSuffix(LocalDateTime machineDate) {
        return machineDate.format(databaseSuffix).concat("-").concat((UUID.randomUUID()).toString().substring(0, 8));
    }

    @Override
    public void run(ApplicationArguments args) {
        new Thread(this::databaseBackupScheduler).start();
    }
}

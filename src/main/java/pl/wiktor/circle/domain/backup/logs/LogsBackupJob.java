package pl.wiktor.circle.domain.backup.logs;

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
import pl.wiktor.circle.domain.backup.utils.FilesLookupHelper;
import pl.wiktor.circle.domain.exception.ApplicationException;
import pl.wiktor.circle.domain.exception.ExceptionType;

import java.io.File;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static pl.wiktor.circle.domain.backup.FileStructure.mapToFileStructure;

@EnableScheduling
@Component
@ConditionalOnProperty(value = "project.env.logs.backup-scheduler", havingValue = "true")
public class LogsBackupJob implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(LogsBackupJob.class);
    private static final String LOGS_BACKUP_EXTENSION = ".gz";
    private final LogBackupRepository logBackupRepository;
    private final Clock clock;
    private final FileSaver fileSaver;
    private final boolean deleteAfterBackup;
    private final FilesLookupHelper filesLookupHelper;

    public LogsBackupJob(LogBackupRepository logBackupRepository, Clock clock, FileSaver fileSaver,
                         @Value("${project.env.logs.location}") String logsLocation,
                         @Value("${project.env.logs.delete-after-backup}") boolean deleteAfterBackup) {
        this.logBackupRepository = logBackupRepository;
        this.clock = clock;
        this.fileSaver = fileSaver;
        this.deleteAfterBackup = deleteAfterBackup;
        this.filesLookupHelper = new FilesLookupHelper(logsLocation, LOGS_BACKUP_EXTENSION);
    }

    @Scheduled(cron = "${project.env.logs.cron-scheduler}")
    public void logsBackupScheduler() {
        try {
            logger.info("Logs backup scheduler running");
            LocalDateTime currentTime = LocalDateTime.ofInstant(clock.instant(), ZoneId.of("Europe/Paris"));
            List<String> alreadyBackupDone = logBackupRepository.findAll().stream()
                    .map(LogBackup::getFileName)
                    .collect(Collectors.toList());
            List<File> filesToBackup = filesLookupHelper.resolveAllFilesExcept(alreadyBackupDone);
            if (filesToBackup.size() == 0) {
                logger.info("There is no logs to backup or already saved. No more actions performing");
                return;
            }
            filesToBackup
                    .forEach(file -> {
                        fileSaver.saveFile(file, mapToFileStructure(extractToDate(file.getName())));
                        logBackupRepository.save(new LogBackup(currentTime, file.getName()));
                        if (deleteAfterBackup) {
                            FilesLookupHelper.removeFile(file);
                        }
                    });
            logger.info("Logs backup scheduler finished");
        } catch (Exception e) {
            logger.error("Logs backup error occurred: ", e);
        }
    }

    private static LocalDate extractToDate(String fileName) {
        Pattern p = Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}");
        Matcher m = p.matcher(fileName);
        if (m.find()) {
            return LocalDate.parse(m.group());
        } else {
            logger.error("Fail during parsing filename '{}'", fileName);
            throw new ApplicationException(ExceptionType.FAILED_PARSING_FILENAME, fileName);
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        new Thread(this::logsBackupScheduler).start();
    }
}

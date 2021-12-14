package pl.wiktor.circle.domain.backup.logs;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LogBackupRepository extends JpaRepository<LogBackup, String> {
}

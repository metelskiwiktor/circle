package pl.wiktor.circle.domain.backup.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface DatabaseBackupRepository extends JpaRepository<DatabaseBackup, String> {
    @Modifying
    @Transactional
    @Query(value = "BACKUP TO ?1", nativeQuery = true)
    void backupDB(String path);
    long count();
}

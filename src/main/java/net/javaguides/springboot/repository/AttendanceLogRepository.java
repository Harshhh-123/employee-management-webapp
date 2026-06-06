package net.javaguides.springboot.repository;

import net.javaguides.springboot.model.AttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, Long> {
    Optional<AttendanceLog> findByWorkerIdAndClockOutIsNull(Long workerId);
    List<AttendanceLog> findByWorkerId(Long workerId);
}
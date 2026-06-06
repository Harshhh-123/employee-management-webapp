package net.javaguides.springboot.repository;

import net.javaguides.springboot.model.AttendanceLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, Long> {

    Optional<AttendanceLog> findByWorkerIdAndClockOutIsNull(Long workerId);

    @EntityGraph(attributePaths = {"worker", "site"})
    Page<AttendanceLog> findByWorkerId(Long workerId, Pageable pageable);

    @EntityGraph(attributePaths = {"worker", "site"})
    java.util.List<AttendanceLog> findByWorkerId(Long workerId);
}
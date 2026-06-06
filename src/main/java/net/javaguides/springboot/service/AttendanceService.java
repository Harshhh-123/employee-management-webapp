package net.javaguides.springboot.service;

import net.javaguides.springboot.model.AttendanceLog;
import net.javaguides.springboot.model.OvertimeEntry;
import net.javaguides.springboot.model.Worker;
import net.javaguides.springboot.model.Site;
import net.javaguides.springboot.repository.AttendanceLogRepository;
import net.javaguides.springboot.repository.OvertimeEntryRepository;
import net.javaguides.springboot.repository.WorkerRepository;
import net.javaguides.springboot.repository.SiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceLogRepository attendanceLogRepository;
    @Autowired
    private OvertimeEntryRepository overtimeEntryRepository;
    @Autowired
    private WorkerRepository workerRepository;
    @Autowired
    private SiteRepository siteRepository;

    @Transactional
    public AttendanceLog clockIn(Long workerId, Long siteId) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));
        if (!worker.getActive())
            throw new RuntimeException("Worker is not active");

        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new RuntimeException("Site not found"));
        if (!site.getActive())
            throw new RuntimeException("Site is not active");

        Optional<AttendanceLog> existing = attendanceLogRepository
                .findByWorkerIdAndClockOutIsNull(workerId);
        if (existing.isPresent())
            throw new RuntimeException("DUPLICATE_CLOCK_IN");

        AttendanceLog log = new AttendanceLog();
        log.setWorker(worker);
        log.setSite(site);
        log.setClockIn(LocalDateTime.now());
        return attendanceLogRepository.save(log);
    }

    @Transactional
    public AttendanceLog clockOut(Long workerId) {
        AttendanceLog log = attendanceLogRepository
                .findByWorkerIdAndClockOutIsNull(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not clocked in"));

        LocalDateTime clockOut = LocalDateTime.now();
        log.setClockOut(clockOut);

        double hours = ChronoUnit.MINUTES.between(log.getClockIn(), clockOut) / 60.0;
        log.setTotalHoursWorked(hours);

        double overtimeHours = 0;
        if (hours > 8) overtimeHours = hours - 8;
        log.setOvertimeHours(overtimeHours);

        if (hours > 16) log.setFlagged(true);

        attendanceLogRepository.save(log);

        if (overtimeHours > 0) {
            createOvertimeEntry(log, overtimeHours);
        }
        return log;
    }

    private void createOvertimeEntry(AttendanceLog log, double overtimeHours) {
        OvertimeEntry entry = new OvertimeEntry();
        entry.setWorker(log.getWorker());
        entry.setAttendance(log);
        entry.setDate(LocalDate.now());
        entry.setOvertimeHours(overtimeHours);

        double rate;
        if (overtimeHours <= 2) {
            rate = log.getWorker().getDailyWageRate() * 1.5 / 8;
        } else {
            rate = log.getWorker().getDailyWageRate() * 2.0 / 8;
        }
        entry.setOvertimeRateApplied(rate);
        entry.setAmount(overtimeHours * rate);
        entry.setSettlementStatus(OvertimeEntry.SettlementStatus.PENDING);
        overtimeEntryRepository.save(entry);
    }

    public List<AttendanceLog> getAttendanceHistory(Long workerId) {
        return attendanceLogRepository.findByWorkerId(workerId);
    }

    public Page<AttendanceLog> getAttendanceHistoryPaged(
            Long workerId, Pageable pageable) {
        return attendanceLogRepository.findByWorkerId(workerId, pageable);
    }
}
package net.javaguides.springboot.controller;

import net.javaguides.springboot.model.AttendanceLog;
import net.javaguides.springboot.service.AttendanceService;
import net.javaguides.springboot.service.ActiveWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private ActiveWorkerService activeWorkerService;

    @PostMapping("/clock-in")
    public ResponseEntity<?> clockIn(@RequestBody Map<String, Long> request) {
        try {
            Long workerId = request.get("workerId");
            Long siteId = request.get("siteId");
            AttendanceLog log = attendanceService.clockIn(workerId, siteId);
            activeWorkerService.addActiveWorker(
                    workerId, siteId,
                    log.getClockIn().toString()
            );
            return ResponseEntity.ok(log);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("DUPLICATE_CLOCK_IN")) {
                return ResponseEntity.status(409)
                        .body(Map.of(
                                "error", "DUPLICATE_CLOCK_IN",
                                "message", "Worker is already clocked in",
                                "timestamp", java.time.LocalDateTime.now().toString()
                        ));
            }
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/clock-out")
    public ResponseEntity<?> clockOut(@RequestBody Map<String, Long> request) {
        try {
            Long workerId = request.get("workerId");
            AttendanceLog log = attendanceService.clockOut(workerId);
            activeWorkerService.removeActiveWorker(workerId);
            return ResponseEntity.ok(log);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveWorkers() {
        return ResponseEntity.ok(activeWorkerService.getActiveWorkers());
    }

    @GetMapping("/log")
    public ResponseEntity<?> getAttendanceLog(
            @RequestParam Long workerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        var result = attendanceService.getAttendanceHistoryPaged(workerId, pageable);
        return ResponseEntity.ok(Map.of(
                "content", result.getContent(),
                "totalElements", result.getTotalElements(),
                "totalPages", result.getTotalPages(),
                "currentPage", result.getNumber()
        ));
    }
}
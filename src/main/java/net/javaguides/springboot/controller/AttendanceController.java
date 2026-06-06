package net.javaguides.springboot.controller;

import net.javaguides.springboot.model.AttendanceLog;
import net.javaguides.springboot.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/clock-in")
    public ResponseEntity<?> clockIn(@RequestBody Map<String, Long> request) {
        try {
            Long workerId = request.get("workerId");
            Long siteId = request.get("siteId");
            AttendanceLog log = attendanceService.clockIn(workerId, siteId);
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
            return ResponseEntity.ok(log);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/log")
    public ResponseEntity<?> getAttendanceLog(@RequestParam Long workerId) {
        return ResponseEntity.ok(
                attendanceService.getAttendanceHistory(workerId)
        );
    }
}
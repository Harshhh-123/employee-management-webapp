package net.javaguides.springboot.controller;

import net.javaguides.springboot.model.OvertimeEntry;
import net.javaguides.springboot.repository.OvertimeEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/overtime")
public class OvertimeController {

    @Autowired
    private OvertimeEntryRepository overtimeEntryRepository;

    @GetMapping("/summary/{workerId}")
    public ResponseEntity<?> getSummary(@PathVariable Long workerId) {
        List<OvertimeEntry> entries = overtimeEntryRepository
                .findByWorkerIdAndSettlementStatus(
                        workerId, OvertimeEntry.SettlementStatus.PENDING
                );
        double totalAmount = entries.stream()
                .mapToDouble(OvertimeEntry::getAmount)
                .sum();
        return ResponseEntity.ok(Map.of(
                "workerId", workerId,
                "entries", entries,
                "totalAmount", totalAmount
        ));
    }

    @PostMapping("/settle/{workerId}")
    public ResponseEntity<?> settle(@PathVariable Long workerId) {
        List<OvertimeEntry> entries = overtimeEntryRepository
                .findByWorkerIdAndSettlementStatus(
                        workerId, OvertimeEntry.SettlementStatus.PENDING
                );
        if (entries.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No pending overtime entries"));
        }
        entries.forEach(e ->
                e.setSettlementStatus(OvertimeEntry.SettlementStatus.SETTLED)
        );
        overtimeEntryRepository.saveAll(entries);
        double totalAmount = entries.stream()
                .mapToDouble(OvertimeEntry::getAmount).sum();
        return ResponseEntity.ok(Map.of(
                "message", "Settled successfully",
                "totalAmount", totalAmount
        ));
    }
}
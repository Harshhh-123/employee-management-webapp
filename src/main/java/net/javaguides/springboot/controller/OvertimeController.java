package net.javaguides.springboot.controller;

import net.javaguides.springboot.model.OvertimeEntry;
import net.javaguides.springboot.repository.OvertimeEntryRepository;
import net.javaguides.springboot.service.OvertimeSettlementEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/overtime")
public class OvertimeController {

    @Autowired
    private OvertimeEntryRepository overtimeEntryRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @GetMapping("/summary/{workerId}")
    public ResponseEntity<?> getSummary(@PathVariable Long workerId) {
        List<OvertimeEntry> entries = overtimeEntryRepository
                .findByWorkerIdAndSettlementStatus(
                        workerId, OvertimeEntry.SettlementStatus.PENDING
                );
        double totalAmount = entries.stream()
                .mapToDouble(OvertimeEntry::getAmount).sum();
        return ResponseEntity.ok(Map.of(
                "workerId", workerId,
                "entries", entries,
                "totalAmount", totalAmount,
                "settlementStatus", "PENDING"
        ));
    }

    @Transactional
    @PostMapping("/settle/{workerId}")
    public ResponseEntity<?> settle(@PathVariable Long workerId) {
        // Cannot settle current month
        YearMonth currentMonth = YearMonth.now();

        List<OvertimeEntry> entries = overtimeEntryRepository
                .findByWorkerIdAndSettlementStatus(
                        workerId, OvertimeEntry.SettlementStatus.PENDING
                );

        if (entries.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No pending overtime entries"));
        }

        // Check if any entry is from current month
        boolean hasCurrentMonth = entries.stream().anyMatch(e ->
                YearMonth.from(e.getDate()).equals(currentMonth)
        );
        if (hasCurrentMonth) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Cannot settle current month"));
        }

        // All-or-nothing settlement
        entries.forEach(e ->
                e.setSettlementStatus(OvertimeEntry.SettlementStatus.SETTLED)
        );
        overtimeEntryRepository.saveAll(entries);

        double totalAmount = entries.stream()
                .mapToDouble(OvertimeEntry::getAmount).sum();

        // SMS fires AFTER transaction commits
        eventPublisher.publishEvent(
                new OvertimeSettlementEvent(this, workerId, totalAmount)
        );

        return ResponseEntity.ok(Map.of(
                "message", "Settled successfully",
                "totalAmount", totalAmount,
                "entriesSettled", entries.size()
        ));
    }
}
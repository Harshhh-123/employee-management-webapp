package net.javaguides.springboot.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "overtime_entries")
public class OvertimeEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id", nullable = false)
    private AttendanceLog attendance;

    private LocalDate date;
    private Double overtimeHours;
    private Double overtimeRateApplied;
    private Double amount;

    @Enumerated(EnumType.STRING)
    private SettlementStatus settlementStatus = SettlementStatus.PENDING;

    public enum SettlementStatus {
        PENDING, SETTLED
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Worker getWorker() { return worker; }
    public void setWorker(Worker worker) { this.worker = worker; }
    public AttendanceLog getAttendance() { return attendance; }
    public void setAttendance(AttendanceLog attendance) { this.attendance = attendance; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Double getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(Double overtimeHours) { this.overtimeHours = overtimeHours; }
    public Double getOvertimeRateApplied() { return overtimeRateApplied; }
    public void setOvertimeRateApplied(Double overtimeRateApplied) { this.overtimeRateApplied = overtimeRateApplied; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public SettlementStatus getSettlementStatus() { return settlementStatus; }
    public void setSettlementStatus(SettlementStatus settlementStatus) { this.settlementStatus = settlementStatus; }
}
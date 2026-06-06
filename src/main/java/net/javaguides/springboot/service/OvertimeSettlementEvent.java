package net.javaguides.springboot.service;

import org.springframework.context.ApplicationEvent;

public class OvertimeSettlementEvent extends ApplicationEvent {
    private final Long workerId;
    private final double totalAmount;

    public OvertimeSettlementEvent(Object source, Long workerId, double totalAmount) {
        super(source);
        this.workerId = workerId;
        this.totalAmount = totalAmount;
    }

    public Long getWorkerId() { return workerId; }
    public double getTotalAmount() { return totalAmount; }
}
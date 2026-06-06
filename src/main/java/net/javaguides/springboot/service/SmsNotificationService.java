package net.javaguides.springboot.service;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Service
public class SmsNotificationService {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSettlementEvent(OvertimeSettlementEvent event) {
        try {
            System.out.println("SMS sent to worker " + event.getWorkerId()
                    + ": Your overtime of " + event.getTotalAmount() + " has been settled.");
        } catch (Exception e) {
            System.err.println("SMS failed - but settlement data is correct: "
                    + e.getMessage());
        }
    }
}
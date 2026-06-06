package net.javaguides.springboot.repository;

import net.javaguides.springboot.model.OvertimeEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OvertimeEntryRepository extends JpaRepository<OvertimeEntry, Long> {
    List<OvertimeEntry> findByWorkerIdAndSettlementStatus(
            Long workerId,
            OvertimeEntry.SettlementStatus status
    );
}
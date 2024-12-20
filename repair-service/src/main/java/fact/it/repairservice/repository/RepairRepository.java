package fact.it.repairservice.repository;

import fact.it.repairservice.model.Repair;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepairRepository extends JpaRepository<Repair, Long> {
}

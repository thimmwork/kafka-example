package net.thimmwork.example.kafka.port.out;

import net.thimmwork.example.kafka.domain.model.WarningEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarningRepository extends JpaRepository<WarningEntity, String> {
}

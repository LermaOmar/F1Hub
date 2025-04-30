package ptzt.f1Hub.instraestructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ptzt.f1Hub.domain.models.Driver;

public interface DriverRepository extends JpaRepository<Driver, Long> {
}
package ptzt.f1Hub.instraestructure.repository.original;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ptzt.f1Hub.domain.models.original.Driver;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query("SELECT d FROM Driver d WHERE d.id NOT IN (" +
            "SELECT d2.id FROM LineUp l JOIN l.drivers d2 WHERE l.league.id = :leagueId)")
    List<Driver> findAllByNotAssignedToLeague(@Param("leagueId") Long leagueId);

    Page<Driver> findAllByActiveTrue(Pageable pageable);

}
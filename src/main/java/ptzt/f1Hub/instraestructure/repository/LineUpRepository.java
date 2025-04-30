package ptzt.f1Hub.instraestructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ptzt.f1Hub.domain.models.Driver;
import ptzt.f1Hub.domain.models.LineUp;
import ptzt.f1Hub.domain.models.Team;

import java.util.List;

public interface LineUpRepository extends JpaRepository<LineUp, Long> {

    List<LineUp> findAllByDriver(Driver driver);

    List<LineUp> findAllByTeam(Team team);

}
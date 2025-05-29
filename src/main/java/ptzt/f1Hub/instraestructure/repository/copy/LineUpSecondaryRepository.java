package ptzt.f1Hub.instraestructure.repository.copy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ptzt.f1Hub.domain.models.copy.*;

import java.util.List;
import java.util.Optional;

public interface LineUpSecondaryRepository extends JpaRepository<LineUp, Long> {

    List<LineUp> findAllByDrivers(List<Driver> drivers);

    List<LineUp> findAllByTeam(Team team);

    Optional<LineUp> findByAppUserAndLeague(AppUser appUser, League league);

    Page<LineUp> findAllByLeague(League league, Pageable pageable);

    Page<LineUp> findAllByAppUser(AppUser appUser, Pageable pageable);

}
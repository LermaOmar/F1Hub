package ptzt.f1Hub.application.services.team;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ptzt.f1Hub.domain.models.original.Team;

import java.util.List;

public interface TeamService {

    Team create(Team team);

    Team update(Team team);

    Team getById(Long id);

    Team getMvp();

    Page<Team> getAll(Pageable pageable);

    Page<Team> getAllActive(Pageable pageable);

    List<Team> getAll();

    List<Team> getAllNotAssigned(Long league);

    void deactivate(Long id);

    void activate(Long id);

    void updateValue(Team team);

}

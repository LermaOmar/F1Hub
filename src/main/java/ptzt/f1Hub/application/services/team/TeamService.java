package ptzt.f1Hub.application.services.team;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ptzt.f1Hub.domain.models.Team;

import java.util.List;

public interface TeamService {

    Team create(Team team);

    Team update(Team team);

    Team getById(Long id);

    Page<Team> getAll(Pageable pageable);

    List<Team> getAll();

    void deactivate(Long id);

    void updateValue(Team team);

}

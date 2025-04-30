package ptzt.f1Hub.application.services.league;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ptzt.f1Hub.domain.models.League;

public interface LeagueService {

    League  create(League league);

    League  update(League league);

    void  delete(League league);

    League getById(Long id);

    Page<League> getAll(Pageable pageable);

}

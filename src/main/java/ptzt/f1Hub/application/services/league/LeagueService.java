package ptzt.f1Hub.application.services.league;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ptzt.f1Hub.domain.models.original.AppUser;
import ptzt.f1Hub.domain.models.original.League;

import java.util.List;

public interface LeagueService {

    League  create(League league, AppUser appUser);

    League  update(League league);

    void  delete(League league);

    League getById(Long id);

    Page<League> getAllByUser(Pageable pageable, AppUser appUser);

    List<League> getAll();

}

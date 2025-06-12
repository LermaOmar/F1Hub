package ptzt.f1Hub.application.services.lineUp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ptzt.f1Hub.domain.models.original.*;

import java.util.List;

public interface LineUpService {

    LineUp create(LineUp lineUp);

    LineUp update(LineUp lineUp);

    void delete(LineUp lineUp);

    LineUp getById(Long id);

    LineUp getByUserAndLeague(AppUser appUser, League league);

    Page<LineUp> getAllByLeague(Pageable pageable, League league);

    List<LineUp> getAllByDriver(List<Driver> drivers);

    List<LineUp> getAllByTeam(Team team);

    List<LineUp> getAll();

    LineUp getByAppUserAndLeague(AppUser appUser, League league);


}

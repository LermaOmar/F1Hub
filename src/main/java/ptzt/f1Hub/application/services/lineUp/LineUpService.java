package ptzt.f1Hub.application.services.lineUp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ptzt.f1Hub.domain.models.Driver;
import ptzt.f1Hub.domain.models.LineUp;
import ptzt.f1Hub.domain.models.Team;

import java.util.List;

public interface LineUpService {

    LineUp create(LineUp lineUp);

    LineUp update(LineUp lineUp);

    void delete(LineUp lineUp);

    LineUp getById(Long id);

    Page<LineUp> getAll(Pageable pageable);

    List<LineUp> getAllByDriver(List<Driver> drivers);

    List<LineUp> getAllByTeam(Team team);


}

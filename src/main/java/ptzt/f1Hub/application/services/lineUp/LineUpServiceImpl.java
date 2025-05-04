package ptzt.f1Hub.application.services.lineUp;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptzt.f1Hub.application.services.driver.DriverService;
import ptzt.f1Hub.application.services.league.LeagueService;
import ptzt.f1Hub.application.services.team.TeamService;
import ptzt.f1Hub.domain.exceptions.EntityNotFoundException;
import ptzt.f1Hub.domain.models.Driver;
import ptzt.f1Hub.domain.models.League;
import ptzt.f1Hub.domain.models.LineUp;
import ptzt.f1Hub.domain.models.Team;
import ptzt.f1Hub.instraestructure.repository.LineUpRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class LineUpServiceImpl implements LineUpService {

    private final LineUpRepository lineUpRepository;
    private final TeamService teamService;
    private final DriverService driverService;
    private final LeagueService leagueService;

    @Transactional
    @Override
    public LineUp create(LineUp lineUp) {

        return lineUpRepository.save(lineUp);

    }

    @Override
    public LineUp update(LineUp lineUp) {

        return lineUpRepository.save(lineUp);

    }

    @Transactional
    @Override
    public void delete(LineUp lineUp) {

        Team team = lineUp.getTeam();
        team.setLineUp(null);
        teamService.update(team);

        Set<Driver> drivers = lineUp.getDrivers();
        drivers.forEach(driver -> {
            Set<LineUp> lineUps = driver.getLineUps().stream()
                    .filter(lineUp1 -> !lineUp1.getId().equals(lineUp.getId()))
                    .collect(Collectors.toSet());

            driver.setLineUps(lineUps);
            driverService.update(driver);
        });

        League league = lineUp.getLeague();
        league.setLineUps(
            league.getLineUps().stream()
                    .filter(lineUp1 -> !lineUp1.getId().equals(lineUp.getId()))
                    .collect(Collectors.toSet())
        );
        leagueService.update(league);

        lineUpRepository.delete(lineUp);

    }

    @Override
    public LineUp getById(Long id) {

        return lineUpRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no lineup with that ID"));

    }

    @Override
    public Page<LineUp> getAll(Pageable pageable) {

        return lineUpRepository.findAll(pageable);

    }

    @Override
    public List<LineUp> getAllByDriver(List<Driver> drivers) {

        return lineUpRepository.findAllByDrivers(drivers);

    }

    @Override
    public List<LineUp> getAllByTeam(Team team) {

        return lineUpRepository.findAllByTeam(team);

    }
}

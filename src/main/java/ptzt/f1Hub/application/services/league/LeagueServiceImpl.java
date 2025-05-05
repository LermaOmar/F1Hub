package ptzt.f1Hub.application.services.league;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptzt.f1Hub.application.services.lineUp.LineUpService;
import ptzt.f1Hub.domain.exceptions.EntityNotFoundException;
import ptzt.f1Hub.domain.models.League;
import ptzt.f1Hub.domain.models.LineUp;
import ptzt.f1Hub.instraestructure.repository.LeagueRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeagueServiceImpl implements LeagueService{

    private final LeagueRepository leagueRepository;
    private final LineUpService lineUpService;

    @Transactional
    @Override
    public League create(League league) {

        return leagueRepository.save(league);

    }

    @Override
    public League update(League league) {

        return leagueRepository.save(league);

    }

    @Transactional
    @Override
    public void delete(League league) {

        league.getLineUps().forEach(lineUpService::delete);

        leagueRepository.delete(league);

    }

    @Override
    public League getById(Long id) {

        return leagueRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("There is no league with that ID"));

    }

    @Override
    public Page<League> getAll(Pageable pageable) {

        return leagueRepository.findAll(pageable);

    }

    @Override
    public List<League> getAll() {

        return leagueRepository.findAll();

    }

}

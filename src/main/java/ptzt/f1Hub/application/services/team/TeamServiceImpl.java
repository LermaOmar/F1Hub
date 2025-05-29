package ptzt.f1Hub.application.services.team;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptzt.f1Hub.application.services.lineUp.LineUpService;
import ptzt.f1Hub.application.services.market.item.MarketItemService;
import ptzt.f1Hub.exceptions.EntityNotFoundException;
import ptzt.f1Hub.exceptions.UnproccesableEntityException;
import ptzt.f1Hub.domain.models.original.LineUp;
import ptzt.f1Hub.domain.models.original.Team;
import ptzt.f1Hub.domain.models.original.market.MarketItem;
import ptzt.f1Hub.instraestructure.repository.original.TeamRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService{

    private final TeamRepository teamRepository;
    private final LineUpService lineUpService;
    private final MarketItemService marketItemService;

    @Transactional
    @Override
    public Team create(Team team) {

        if (teamRepository.findByName(team.getName()).isPresent())
            throw new UnproccesableEntityException("Name is already assigned to other team");

        Team createdTeam = teamRepository.save(team);

        MarketItem marketItem = new MarketItem();
        marketItem.setAuctionableEntity(createdTeam);

        marketItemService.create(marketItem);

        return createdTeam;

    }

    @Override
    public Team update(Team team) {

        Optional<Team> opTeam = teamRepository.findByName(team.getName());

        if (opTeam.isPresent() && !opTeam.get().getId().equals(team.getId()))
            throw new UnproccesableEntityException("Name is already assigned to other team");

        LineUp lineUp = team.getLineUp();

        if (lineUp != null && lineUp.getId() != null)
            lineUpService.update(lineUp);

        return teamRepository.save(team);

    }

    @Override
    public Team getById(Long id) {

        return teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no team with that ID"));

    }

    @Override
    public Team getMvp() {

        return teamRepository.findFirstByOrderByPreviousPointsDesc();

    }

    @Override
    public Page<Team> getAll(Pageable pageable) {

        return teamRepository.findAll(pageable);

    }

    @Override
    public Page<Team> getAllActive(Pageable pageable) {

        return teamRepository.findAllByActiveTrue(pageable);

    }

    @Override
    public List<Team> getAll() {

        return teamRepository.findAll();

    }

    @Override
    public List<Team> getAllNotAssigned(Long league) {

        return teamRepository.findAllByNotAssignedToLineUpInLeague(league);

    }

    @Transactional
    @Override
    public void deactivate(Long id) {

        Team team = getById(id);

        team.setActive(false);

        lineUpService.getAllByTeam(team).forEach(lineUp -> lineUp.setTeam(null));

        teamRepository.save(team);

    }

    @Transactional
    @Override
    public void activate(Long id) {

        Team team = getById(id);
        team.setActive(true);

        teamRepository.save(team);

    }

    @Transactional
    @Override
    public void updateValue(Team team) {

        if (team.getPreviousPoints() == 0)
            return;

        long pointsDifference = team.getPoints() - team.getPreviousPoints();

        double priceChangeFactor =  pointsDifference * 1.0 / team.getPreviousPoints();

        team.setPrice(Math.round(team.getPrice() + (team.getPrice() * priceChangeFactor)));

    }
}

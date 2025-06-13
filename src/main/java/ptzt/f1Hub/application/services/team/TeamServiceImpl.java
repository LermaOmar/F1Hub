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

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final LineUpService lineUpService;
    private final MarketItemService marketItemService;

    @Transactional
    @Override
    public Team create(Team team) {
        if (teamRepository.findByName(team.getName()).isPresent()) {
            throw new UnproccesableEntityException("Name is already assigned to another team");
        }
        Team created = teamRepository.save(team);
        MarketItem mi = new MarketItem();
        mi.setAuctionableEntity(created);
        marketItemService.create(mi);
        return created;
    }

    @Transactional
    @Override
    public Team update(Team team) {
        teamRepository.findByName(team.getName())
                .filter(t -> !t.getId().equals(team.getId()))
                .ifPresent(t -> { throw new UnproccesableEntityException("Name is already assigned to another team"); });
        for (LineUp lu : team.getLineUps()) {
            if (lu.getId() != null) {
                lineUpService.update(lu);
            }
        }
        return teamRepository.save(team);
    }

    @Override
    public Team getById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no team with that ID"));
    }

    @Override
    public boolean checkAuctionableIsTeam(Long id) {
        return teamRepository.findById(id).isPresent();
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
    public List<Team> getAllNotAssigned(Long leagueId) {
        return teamRepository.findAllByNotAssignedToLineOrMarketUpInLeague(leagueId);
    }

    @Transactional
    @Override
    public void deactivate(Long id) {
        Team team = getById(id);
        team.setActive(false);
        for (LineUp lu : team.getLineUps()) {
            lu.setTeam(null);
            lineUpService.update(lu);
        }
        teamRepository.save(team);
        MarketItem marketItem = marketItemService.getByAuctionableEntity(team);
        marketItemService.hideInMarket(marketItem,marketItem.getMarkets().stream().toList());
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
        if (team.getPreviousPoints() == 0) return;
        long diff = team.getPoints() - team.getPreviousPoints();
        double factor = (double) diff / team.getPreviousPoints();
        team.setPrice(Math.round(team.getPrice() + team.getPrice() * factor));
        teamRepository.save(team);
    }
}

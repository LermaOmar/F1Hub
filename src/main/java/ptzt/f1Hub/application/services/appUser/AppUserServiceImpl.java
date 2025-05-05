package ptzt.f1Hub.application.services.appUser;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ptzt.f1Hub.application.services.budget.BudgetService;
import ptzt.f1Hub.application.services.league.LeagueService;
import ptzt.f1Hub.application.services.lineUp.LineUpService;
import ptzt.f1Hub.domain.exceptions.EntityNotFoundException;
import ptzt.f1Hub.domain.exceptions.UnproccesableEntityException;
import ptzt.f1Hub.domain.models.AppUser;
import ptzt.f1Hub.domain.models.Budget;
import ptzt.f1Hub.domain.models.League;
import ptzt.f1Hub.domain.models.LineUp;
import ptzt.f1Hub.instraestructure.repository.AppUserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final LeagueService leagueService;
    private final LineUpService lineUpService;
    private final BudgetService budgetService;

    @Override
    public AppUser create(AppUser appUser) {

        return appUserRepository.save(appUser);

    }

    @Override
    public AppUser update(AppUser appUser) {

        return appUserRepository.save(appUser);

    }

    @Override
    public void delete(AppUser appUser) {

        appUserRepository.delete(appUser);

    }

    @Override
    public AppUser getById(Long id) {

        return appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no user with that id"));

    }

    @Override
    public Page<AppUser> getAll(Pageable pageable) {

        return appUserRepository.findAll(pageable);

    }

    @Override
    public void joinLeague(AppUser appUser, Long leagueId) {

        League foundLeague = leagueService.getById(leagueId);

        //Verify user already in league
        if (appUser.getLineUps().stream().anyMatch(lineUp -> lineUp.getLeague().getId().equals(leagueId))) {
            throw new UnproccesableEntityException("User is already in this league");
        }


        LineUp lineUp = new LineUp();
        lineUp.setAppUser(appUser);
        lineUp.setLeague(foundLeague);
        LineUp createdLineUp = lineUpService.create(lineUp);

        Budget budget = new Budget();
        budget.setLeague(foundLeague);
        budget.setAppUser(appUser);
        Budget createdBudget = budgetService.create(budget);


        foundLeague.getBudgets().add(createdBudget);
        foundLeague.getLineUps().add(createdLineUp);

        leagueService.update(foundLeague);

        appUser.getBudgets().add(createdBudget);
        appUser.getLineUps().add(createdLineUp);
        update(appUser);
    }


    @Override
    public void leaveLeague(AppUser appUser, Long leagueId) {

        League foundLeague = leagueService.getById(leagueId);

        //Verify user not in league
        if (appUser.getLineUps().stream().anyMatch(lineUp -> lineUp.getLeague().getId().equals(leagueId))) {
            throw new UnproccesableEntityException("User is not in this league");
        }


        LineUp lineUp = lineUpService.getByAppUserAndLeague(appUser,foundLeague);


        Budget budget = budgetService.getByUserAndLeague(appUser,foundLeague);


        lineUpService.delete(lineUp);
        budgetService.delete(budget);

        appUser.getBudgets().removeIf(budget1 -> budget1.getId().equals(budget.getId()));
        appUser.getLineUps().removeIf(lineUp1 -> lineUp1.getId().equals(lineUp.getId()));
        update(appUser);
    }

}

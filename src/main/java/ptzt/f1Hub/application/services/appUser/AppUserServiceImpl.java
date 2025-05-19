package ptzt.f1Hub.application.services.appUser;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ptzt.f1Hub.application.services.account.AccountService;
import ptzt.f1Hub.application.services.budget.BudgetService;
import ptzt.f1Hub.application.services.driver.DriverService;
import ptzt.f1Hub.application.services.league.LeagueService;
import ptzt.f1Hub.application.services.lineUp.LineUpService;
import ptzt.f1Hub.application.services.offer.OfferService;
import ptzt.f1Hub.application.services.team.TeamService;
import ptzt.f1Hub.domain.models.*;
import ptzt.f1Hub.exceptions.EntityNotFoundException;
import ptzt.f1Hub.exceptions.UnproccesableEntityException;
import ptzt.f1Hub.instraestructure.repository.AppUserRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final LeagueService leagueService;
    private final LineUpService lineUpService;
    private final AccountService accountService;
    private final BudgetService budgetService;
    private final OfferService offerService;
    private final DriverService driverService;
    private final TeamService teamService;

    @Override
    public AppUser create(AppUser appUser) {

        return appUserRepository.save(appUser);

    }

    @Override
    public AppUser update(AppUser appUser) {

        return appUserRepository.save(appUser);

    }

    @Transactional
    @Override
    public void delete(Long id) {
        AppUser appUser = getById(id);

        if (appUser.getOffers() != null) {
            appUser.getOffers().forEach(offer -> {
                offer.setAppUser(null);
                offerService.delete(offer); // importante que lo elimines por el service si hay lógica adicional
            });
            appUser.getOffers().clear();
        }

        if (appUser.getBudgets() != null) {
            appUser.getBudgets().forEach(budget -> {
                budget.setAppUser(null);
                budgetService.delete(budget);
            });
            appUser.getBudgets().clear();
        }

        if (appUser.getLineUps() != null) {
            appUser.getLineUps().forEach(lineUp -> {
                lineUp.setAppUser(null);
                lineUpService.delete(lineUp);
            });
            appUser.getLineUps().clear();
        }

        Account account = appUser.getAccount();
        if (account != null) {
            appUser.setAccount(null);
            account.setAppUser(null);
            account.setActive(false);
            accountService.update(account);
        }

        update(appUser);

        appUserRepository.delete(appUser);
    }


    @Override
    public AppUser getById(Long id) {

        return appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no user with that id"));

    }

    @Override
    public AppUser getByAccount(Account account) {

        return appUserRepository.findByAccount(account)
                .orElseThrow(() -> new EntityNotFoundException("There is no user with that account"));

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
        lineUp.setDrivers(getRandomDrivers(leagueId));
        lineUp.setTeam(getRandomTeam(leagueId));
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

    private Set<Driver> getRandomDrivers(Long league){

        List<Driver> drivers = driverService.getAllNotAssigned(league);

        Collections.shuffle(drivers);

        return new HashSet<>(drivers.subList(0, 2));
    }

    private Team getRandomTeam(Long league){

        List<Team> teams = teamService.getAllNotAssigned(league);

        Collections.shuffle(teams);

        return teams.get(0);
    }

}

package ptzt.f1Hub.instraestructure.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ptzt.f1Hub.application.services.appUser.AppUserService;
import ptzt.f1Hub.application.services.budget.BudgetService;
import ptzt.f1Hub.application.services.league.LeagueService;
import ptzt.f1Hub.domain.mappers.BudgetMapper;
import ptzt.f1Hub.instraestructure.dto.out.budget.BudgetOutDto;

@RestController
@RequestMapping("/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;
    private final AppUserService appUserService;
    private final LeagueService leagueService;
    private final BudgetMapper budgetMapper;

    @GetMapping("/{userId}/user/{leagueId}/league")
    public ResponseEntity<BudgetOutDto> getByUserAndLeague(@PathVariable Long userId, @PathVariable Long leagueId){

        return ResponseEntity.ok(
                budgetMapper.toOutDto(
                        budgetService.getByUserAndLeague(appUserService.getById(userId), leagueService.getById(leagueId))
                )
        );

    }

}

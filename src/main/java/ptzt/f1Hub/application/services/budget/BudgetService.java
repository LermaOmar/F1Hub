package ptzt.f1Hub.application.services.budget;

import ptzt.f1Hub.domain.models.AppUser;
import ptzt.f1Hub.domain.models.Budget;
import ptzt.f1Hub.domain.models.League;

public interface BudgetService {

    Budget create(Budget budget);

    Budget update(Budget budget);

    Budget getByUserAndLeague(AppUser appUser, League league);

    void delete(Budget budget);
}

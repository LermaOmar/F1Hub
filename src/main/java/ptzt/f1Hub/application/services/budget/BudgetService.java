package ptzt.f1Hub.application.services.budget;

import ptzt.f1Hub.domain.models.Budget;

public interface BudgetService {

    Budget create(Budget budget);

    Budget update(Budget budget);

}

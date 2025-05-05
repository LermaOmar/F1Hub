package ptzt.f1Hub.application.services.budget;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptzt.f1Hub.domain.exceptions.EntityNotFoundException;
import ptzt.f1Hub.domain.models.AppUser;
import ptzt.f1Hub.domain.models.Budget;
import ptzt.f1Hub.domain.models.League;
import ptzt.f1Hub.instraestructure.repository.BudgetRepository;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService{

    private final BudgetRepository budgetRepository;

    @Transactional
    @Override
    public Budget create(Budget budget) {

        return budgetRepository.save(budget);

    }

    @Override
    public Budget update(Budget budget) {

        return budgetRepository.save(budget);

    }

    @Override
    public Budget getByUserAndLeague(AppUser appUser, League league) {

        return budgetRepository.findByAppUserAndLeague(appUser,league)
                .orElseThrow(() -> new EntityNotFoundException("No budget found for that league and user"));

    }

    @Override
    public void delete(Budget budget) {

        budgetRepository.delete(budget);

    }
}

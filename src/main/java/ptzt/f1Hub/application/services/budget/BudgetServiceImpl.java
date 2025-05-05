package ptzt.f1Hub.application.services.budget;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptzt.f1Hub.domain.models.Budget;
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
}

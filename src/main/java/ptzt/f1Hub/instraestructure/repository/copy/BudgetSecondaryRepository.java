package ptzt.f1Hub.instraestructure.repository.copy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptzt.f1Hub.domain.models.copy.AppUser;
import ptzt.f1Hub.domain.models.copy.Budget;
import ptzt.f1Hub.domain.models.copy.League;

import java.util.Optional;

@Repository
public interface BudgetSecondaryRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByAppUserAndLeague(AppUser appUser, League league);


}
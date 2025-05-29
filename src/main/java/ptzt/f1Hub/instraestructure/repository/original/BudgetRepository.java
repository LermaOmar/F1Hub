package ptzt.f1Hub.instraestructure.repository.original;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptzt.f1Hub.domain.models.original.AppUser;
import ptzt.f1Hub.domain.models.original.Budget;
import ptzt.f1Hub.domain.models.original.League;

import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByAppUserAndLeague(AppUser appUser, League league);


}
package ptzt.f1Hub.instraestructure.repository.original;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ptzt.f1Hub.domain.models.original.League;

import java.util.Optional;

@Repository
public interface LeagueRepository extends JpaRepository<League, Long> {

    @Query("SELECT DISTINCT l.league FROM LineUp l WHERE l.appUser.id = :userId")
    Page<League> findDistinctLeaguesByAppUserId(Pageable pageable, @Param("userId") Long userId);

    Optional<League> findByName(String name);

}
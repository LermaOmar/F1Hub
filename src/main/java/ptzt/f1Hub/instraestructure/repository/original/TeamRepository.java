package ptzt.f1Hub.instraestructure.repository.original;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ptzt.f1Hub.domain.models.original.Team;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByName(String name);

    @Query("""
        SELECT t FROM Team t
        LEFT JOIN t.lineUp l
        WHERE l IS NULL OR l.league IS NULL OR l.league.id <> :leagueId
        """)
    List<Team> findAllByNotAssignedToLineUpInLeague(@Param("leagueId") Long leagueId);

    Page<Team> findAllByActiveTrue(Pageable pageable);

    Team findFirstByOrderByPreviousPointsDesc();



}
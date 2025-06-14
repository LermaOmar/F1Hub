package ptzt.f1Hub.instraestructure.repository.copy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ptzt.f1Hub.domain.models.copy.Team;

import java.util.List;
import java.util.Optional;

public interface TeamSecondaryRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByName(String name);

    @Query("""
        SELECT t
        FROM Team t
        LEFT JOIN t.lineUps l
        ON l.league.id = :leagueId
        WHERE l IS NULL
        """)
    List<Team> findAllByNotAssignedToLineUpInLeague(@Param("leagueId") Long leagueId);

    Page<Team> findAllByActiveTrue(Pageable pageable);

    Team findFirstByOrderByPreviousPointsDesc();



}
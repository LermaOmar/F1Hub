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
        SELECT t
        FROM Team t
        LEFT JOIN t.lineUps l
          ON l.league.id = :leagueId
        WHERE l IS NULL
          AND t.id NOT IN (
            SELECT mi.auctionableEntity.id
            FROM MarketItem mi
            JOIN mi.markets m
            WHERE m.league.id   = :leagueId
              AND mi.available  = true
          )
        """)
    List<Team> findAllByNotAssignedToLineOrMarketUpInLeague(@Param("leagueId") Long leagueId);

    Page<Team> findAllByActiveTrue(Pageable pageable);

    Team findFirstByOrderByPreviousPointsDesc();



}
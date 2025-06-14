package ptzt.f1Hub.instraestructure.repository.original;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ptzt.f1Hub.domain.models.original.Driver;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query("""
        SELECT d
        FROM Driver d
        LEFT JOIN d.lineUps l
          ON l.league.id = :leagueId
        WHERE l IS NULL
          AND d.id NOT IN (
            SELECT mi.auctionableEntity.id
            FROM MarketItem mi
            JOIN mi.markets m
            WHERE m.league.id   = :leagueId
              AND mi.available  = true
          )
        """)
    List<Driver> findAllByNotAssignedToLineUpOrMarketInLeague(@Param("leagueId") Long leagueId);

    Page<Driver> findAllByActiveTrue(Pageable pageable);

}
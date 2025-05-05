package ptzt.f1Hub.instraestructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptzt.f1Hub.domain.models.AppUser;
import ptzt.f1Hub.domain.models.League;
import ptzt.f1Hub.domain.models.market.MarketItem;
import ptzt.f1Hub.domain.models.market.Offer;

import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

    Optional<Offer> findByAppUserAndLeagueAndMarketItem(AppUser appUser, League league, MarketItem marketItem);
}
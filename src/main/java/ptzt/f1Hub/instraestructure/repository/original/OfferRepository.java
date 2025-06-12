package ptzt.f1Hub.instraestructure.repository.original;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptzt.f1Hub.domain.models.original.AppUser;
import ptzt.f1Hub.domain.models.original.League;
import ptzt.f1Hub.domain.models.original.market.MarketItem;
import ptzt.f1Hub.domain.models.original.market.Offer;

import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

    Optional<Offer> findByAppUserAndLeagueAndMarketItem(AppUser appUser, League league, MarketItem marketItem);

    Page<Offer> findByLeagueAndMarketItem( League league, MarketItem marketItem, Pageable pageable);

    Page<Offer> findAllByAppUser(Pageable pageable, AppUser appUser);

}
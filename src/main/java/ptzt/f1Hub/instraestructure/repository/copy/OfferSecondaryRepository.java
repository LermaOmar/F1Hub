package ptzt.f1Hub.instraestructure.repository.copy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptzt.f1Hub.domain.models.copy.AppUser;
import ptzt.f1Hub.domain.models.copy.League;
import ptzt.f1Hub.domain.models.copy.market.MarketItem;
import ptzt.f1Hub.domain.models.copy.market.Offer;

import java.util.Optional;

@Repository
public interface OfferSecondaryRepository extends JpaRepository<Offer, Long> {

    Optional<Offer> findByAppUserAndLeagueAndMarketItem(AppUser appUser, League league, MarketItem marketItem);

    Page<Offer> findAllByAppUser(Pageable pageable, AppUser appUser);
}
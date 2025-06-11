package ptzt.f1Hub.instraestructure.repository.original;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptzt.f1Hub.domain.models.original.AuctionableEntity;
import ptzt.f1Hub.domain.models.original.market.Market;
import ptzt.f1Hub.domain.models.original.market.MarketItem;

import java.util.List;
import java.util.Optional;


@Repository
public interface MarketItemRepository extends JpaRepository<MarketItem, Long> {

    Page<MarketItem> findAllByMarketsContainingAndAvailableTrue(Market market, Pageable pageable);
    List<MarketItem> findAllByMarketsContaining(Market market);
    Optional<MarketItem> findByAuctionableEntity(AuctionableEntity auctionableEntity);

}